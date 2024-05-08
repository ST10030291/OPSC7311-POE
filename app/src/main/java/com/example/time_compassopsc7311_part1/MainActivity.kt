package com.example.time_compassopsc7311_part1

import UserData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.time_compassopsc7311_part1.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dateOfCreation: String
    private lateinit var fireBaseRef : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Bindings for each Click
        binding.openSignUpActivity.setOnClickListener(this)
        binding.loginUser.setOnClickListener(this)
        binding.textViewForgotPassword.setOnClickListener(this)
    }

    // Function to search for a user by email and return their username
    private fun getUsernameByEmail(email: String): String? {
        val user = UserData.users.find { it.email == email }
        dateOfCreation = user?.dateOfCreation.toString()
        return user?.username ?: "Unknown user"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // Opens the Sign Up Activity
            R.id.openSignUpActivity -> {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            // Logs user in if credentials are Valid
            R.id.loginUser -> {
                // Get email and password
                val editTextEmail = findViewById<EditText>(R.id.editTextEmailLogin)
                val editTextPassword = findViewById<EditText>(R.id.editTextPasswordLogin)

                val enteredEmail = editTextEmail.text.toString()
                val enteredPassword = editTextPassword.text.toString()

                // Check if email / password are valid
                val isCredentialsValid = UserData.users.any() { person ->
                    person.email == enteredEmail && person.password == enteredPassword
                }

                if (isCredentialsValid) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    // Proceed to the Home Screen
                    val username = getUsernameByEmail(enteredEmail)
                    saveUserData(enteredEmail, username, dateOfCreation)
                    //added real time databas ereference test check it
                    fireBaseRef = FirebaseDatabase.getInstance().getReference("User")
                    fireBaseRef.setValue("Kaushil")
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.textViewForgotPassword -> {
                val editTextEmail = findViewById<EditText>(R.id.editTextEmailLogin)
                val enteredEmail = editTextEmail.text.toString()

                if (enteredEmail.isEmpty()) {
                    Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val user = UserData.users.find { it.email == enteredEmail }
                    if (user != null) {
                        // Display the users password
                        Toast.makeText(
                            this,
                            "Your password is: ${user.password}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun saveUserData(email: String, username: String?, dateofcreation: String) {
        sharedPreferences.edit().apply {
            putString("EMAIL", email)
            putString("USERNAME", username)
            putString("DATEOFCREATION", dateofcreation)
            apply()
        }
    }
}