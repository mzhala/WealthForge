package com.example.wealthforge

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.wealthforge.data.User
import com.example.wealthforge.data.UserDao
import com.example.wealthforge.data.Category
import com.example.wealthforge.data.CategoryDao
import com.example.wealthforge.data.AppDatabase

class SignInActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var categoryDao: CategoryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Initialize AppDatabase and UserDao
        db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Initial/default user tester pass
        val newUser = User(
            username = "tester",
            password = "pass",
            name = "John",
            surname = "Doe",
            profilePicture = null
        )

        // Insert the default user into the database
        lifecycleScope.launch {
            try {
                userDao.insertUser(newUser)
            } catch (e: Exception) {
                // Handle errors (e.g., username already taken)
            }
        }

        // Sign-in button clicked
        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener {
            login()
        }
    }

    fun login() {
        val username = findViewById<EditText>(R.id.username).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Use lifecycleScope to launch a coroutine
        lifecycleScope.launch {
            // Query the database using UserDao
            val user = userDao.getUser(username, password)

            if (user != null) {
                // If user exists,
                // Add default categories
                insertDefaultCategories(user.id)
                // navigate to MainActivity
                val intent = Intent(this@SignInActivity, MainActivity::class.java)

                // Pass the userId as an extra to the intent
                intent.putExtra("User_ID", user.id)

                startActivity(intent)
                finish()  // Optional: Close the sign-in screen
            } else {
                // If user not found or invalid credentials
                Toast.makeText(this@SignInActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun insertDefaultCategories(userId: Int) {
        val categoryDao = db.categoryDao()
        val defaultCategories = listOf(
            Category(0, userId, "Groceries", "Expense", false, 0.0, R.drawable.ic_groceries),
            Category(0, userId, "Rent", "Expense", true, 5000.0, R.drawable.ic_rent),
            Category(0, userId, "Transport", "Expense", false, 0.0, R.drawable.ic_transport),
            Category(0, userId, "Emergency Fund", "Goal", false, 0.0, R.drawable.ic_emergency_stop),
        )
        defaultCategories.forEach { category ->
            if (categoryDao.categoryExists(category.categoryName) == 0) {
                categoryDao.insertCategory(category)
            }


        }
    }

}
