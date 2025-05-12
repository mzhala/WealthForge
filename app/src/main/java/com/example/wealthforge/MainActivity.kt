package com.example.wealthforge

import UserViewModel
import com.example.wealthforge.R

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.wealthforge.data.AppDatabase
import com.example.wealthforge.data.UserDao
import com.example.wealthforge.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var usernameTextView: TextView
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Initialize database and DAO
        db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Get the userId passed from the SignInActivity
        val userId = intent.getIntExtra("User_ID", -1)

        if (userId != -1) {
            userViewModel.setUserId(userId.toString())
        }

        // Setup toolbar custom views
        val titleTextView: TextView = binding.appBarMain.toolbar.findViewById(R.id.toolbar_title)
        val iconMenu: ImageView = binding.appBarMain.toolbar.findViewById(R.id.icon_menu)
        val iconProfile: ImageView = binding.appBarMain.toolbar.findViewById(R.id.icon_profile)

        // If the userId is valid, proceed with fetching the username
        if (userId != -1) {
            // Initialize the TextView here
            usernameTextView = findViewById(R.id.username)

            // Retrieve the username in a coroutine
            lifecycleScope.launch {
                try {
                    // Fetch the username from the database
                    val username = userDao.getUsername(userId)

                    // Update the UI with the username
                    usernameTextView.text = username
                } catch (e: Exception) {
                    // Handle errors (e.g., database issues)
                    Toast.makeText(this@MainActivity, "Error fetching username", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Handle the case where userId is missing or invalid
            Toast.makeText(this, "User ID is missing or invalid", Toast.LENGTH_SHORT).show()
        }

        titleTextView.text = "Welcome"

        iconMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        iconProfile.setOnClickListener {
            Snackbar.make(it, "Profile clicked", Snackbar.LENGTH_SHORT).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_budget, R.id.nav_categories, R.id.nav_new_transaction, R.id.nav_transaction_history, R.id.nav_transaction_history, R.id.nav_profile),
            drawerLayout
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun updateToolbarTitle(title: String) {
        val titleTextView: TextView = binding.appBarMain.toolbar.findViewById(R.id.toolbar_title)
        titleTextView.text = title
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
