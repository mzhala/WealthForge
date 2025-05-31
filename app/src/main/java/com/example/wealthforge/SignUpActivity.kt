package com.example.wealthforge

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Sign In button
        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener {
            // Navigate to the next activity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // forgotten password button
        val resetPasswordButton = findViewById<Button>(R.id.resetPasswordButton)
        resetPasswordButton.setOnClickListener {
            // Navigate to the next activity
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        // Sign Up button
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            // Navigate to the next activity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

}
