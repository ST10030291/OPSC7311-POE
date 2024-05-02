package com.example.time_compassopsc7311_part1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.time_compassopsc7311_part1.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dateOfCreation: String
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Bindings for each Click
        binding.openSignUpActivity.setOnClickListener(this)
        binding.loginUser.setOnClickListener(this)
        binding.textViewForgotPassword.setOnClickListener(this)
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
                val email = binding.editTextEmailLogin.text.toString()
                val password = binding.editTextPasswordLogin.text.toString()

                if(email.isNotEmpty() && password.isNotEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){task ->
                        if(task.isSuccessful){
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                            saveUserData(email, email, dateOfCreation)
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
                }
            }
            // Update this for firebase
//            R.id.textViewForgotPassword -> {
//                val editTextEmail = findViewById<EditText>(R.id.editTextEmailLogin)
//                val enteredEmail = editTextEmail.text.toString()
//
//                if (enteredEmail.isEmpty()) {
//                    Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT)
//                        .show()
//                } else {
//                    val user = UserData.users.find { it.email == enteredEmail }
//                    if (user != null) {
//                        // Display the users password
//                        Toast.makeText(
//                            this,
//                            "Your password is: ${user.password}",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    } else {
//                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
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