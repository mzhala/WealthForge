package com.example.wealthforge

import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.wealthforge.databinding.ActivityMainBinding
import androidx.core.view.GravityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Setup toolbar custom views
        val titleTextView: TextView = binding.appBarMain.toolbar.findViewById(R.id.toolbar_title)
        val iconMenu: ImageView = binding.appBarMain.toolbar.findViewById(R.id.icon_menu)
        val iconProfile: ImageView = binding.appBarMain.toolbar.findViewById(R.id.icon_profile)

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
