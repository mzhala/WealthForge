package com.example.wealthforge

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        // reset password button
        val resetPasswordButton = findViewById<Button>(R.id.resetPasswordButton)
        resetPasswordButton.setOnClickListener {
            // Navigate to the next activity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // already have an account button
        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener {
            // Navigate to the next activity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
