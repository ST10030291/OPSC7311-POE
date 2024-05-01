package com.example.time_compassopsc7311_part1

import UserData
import UserDetails
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.time_compassopsc7311_part1.databinding.ActivitySignUpBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private val userList = mutableListOf<UserDetails>()

    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        binding.openLoginActivity.setOnClickListener(this)
        binding.buttonCreateAccount.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            // Opens Login Screen
            R.id.openLoginActivity -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            // Creates and saves a new user to a list
            // This will be saved to the firebase database in the next part
            R.id.buttonCreateAccount -> {
                val editTextEmail = findViewById<EditText>(R.id.editTextEmailSignUp)
                val editTextPassword = findViewById<EditText>(R.id.editTextPasswordSignUp)
                val editTextUsername = findViewById<EditText>(R.id.editTextUsernameSignUp)

                // RE for Email Auth
                val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

                val enteredEmail = editTextEmail.text.toString()
                val enteredPassword = editTextPassword.text.toString()
                val enteredUsername = editTextUsername.text.toString()

                val user = UserData.users.find { it.email == enteredEmail }

                if(enteredEmail.isEmpty() || enteredPassword.isEmpty() || enteredUsername.isEmpty()) {
                    Toast.makeText(this, "Enter required details", Toast.LENGTH_SHORT).show()
                }
                else if(enteredPassword.length < 4) {
                    Toast.makeText(this, "Password cannot be less than 3 characters", Toast.LENGTH_SHORT).show()
                }
                else if (!enteredEmail.matches(emailPattern.toRegex())) {
                    Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                }
                else if (user != null) {
                    Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                }
                else {
                    val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
                    val newUser = UserDetails(enteredEmail, enteredPassword, enteredUsername, currentDate)
                    UserData.users.add(newUser)
                    saveUserData(enteredEmail, enteredUsername, currentDate)
                    Toast.makeText(this,"User Created Successfully", Toast.LENGTH_SHORT).show()

                    //Proceed to Home Screen
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
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