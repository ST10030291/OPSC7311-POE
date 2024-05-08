package com.example.time_compassopsc7311_part1

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.time_compassopsc7311_part1.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

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
            // This will be saved to the firebase database
            R.id.buttonCreateAccount -> {
                // RE for Email Auth
                val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

                val Email = binding.editTextEmailSignUp.text.toString()
                val Password = binding.editTextPasswordSignUp.text.toString()
                val Username = binding.editTextUsernameSignUp.text.toString()

                if(Email.isEmpty() || Password.isEmpty() || Username.isEmpty()){
                    Toast.makeText(this, "Enter required details", Toast.LENGTH_SHORT).show()
                }
                else if(Password.length < 6) {
                    Toast.makeText(this, "Password cannot be less than 6 characters", Toast.LENGTH_SHORT).show()
                }
                else if (!Email.matches(emailPattern.toRegex())) {
                    Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                }
                else {
                    // Save a new user to FIREBASE
                    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                        Date()
                    )
                    Log.d("TAG", "Inside Saving User to FIREBASE")

                    firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(this) {task ->
                        if(task.isSuccessful){
                            Toast.makeText(this,"User Created Successfully", Toast.LENGTH_SHORT).show()
                            Log.d("TAG", "Inside Success msg")

                            //Proceed to Home Screen
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Log.d("TAG", "Inside unsuccessful msg")
                            Toast.makeText(this,"SignUp Unsuccessful", Toast.LENGTH_SHORT).show()
                        }
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