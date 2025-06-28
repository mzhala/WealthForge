package com.example.wealthforge

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.wealthforge.data.AppDatabase
import com.example.wealthforge.data.UserDao
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        val usernameInput = findViewById<EditText>(R.id.username)
        val newPasswordInput = findViewById<EditText>(R.id.password)
        val confirmPasswordInput = findViewById<EditText>(R.id.connfirmPassword)
        val resetPasswordButton = findViewById<Button>(R.id.forgottenPasswordButton)

        findViewById<Button>(R.id.signInButton).setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        resetPasswordButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val newPassword = newPasswordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (username.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = userDao.getUserByUsername(username)
                if (user != null) {
                    val updatedUser = user.copy(password = newPassword)
                    userDao.updateUser(updatedUser)

                    runOnUiThread {
                        Toast.makeText(this@ResetPasswordActivity, "Password updated!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ResetPasswordActivity, SignInActivity::class.java))
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ResetPasswordActivity, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
