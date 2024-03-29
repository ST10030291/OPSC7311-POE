package com.example.time_compassopsc7311_part1

import User
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.time_compassopsc7311_part1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val users = listOf(
        User("avish@gmail.com", "avish"),
        User("kaushil@gmail.com", "kaushil"),
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.openSignUpActivity.setOnClickListener(this)

        binding.loginUser.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.openSignUpActivity -> {
                // Opens the Sign Up Activity
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            R.id.loginUser -> {
                // Get email and password
                val editTextEmail = findViewById<EditText>(R.id.editTextEmailLogin)
                val editTextPassword = findViewById<EditText>(R.id.editTextPasswordLogin)

                val enteredEmail = editTextEmail.text.toString()
                val enteredPassword = editTextPassword.text.toString()

                 // Check if email / password are valid
                val isCredentialsValid = users.any() { person ->
                    person.email == enteredEmail && person.password == enteredPassword
                }

                if (isCredentialsValid) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                     // Proceed to the Home Screen
                } else {
                    Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}