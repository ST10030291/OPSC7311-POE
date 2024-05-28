package com.example.time_compassopsc7311_part1

import DailyGoal
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.example.time_compassopsc7311_part1.databinding.ActivityHomeBinding
import com.example.time_compassopsc7311_part1.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

class Profile : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var popupMenu: PopupMenu
    private lateinit var sharedPreferences: SharedPreferences
    private var username: String = "Unknown"
    private var tasks: String = "Unknown"
    private var categories: String = "Unknown"
    private var email: String = "Unknown"
    private lateinit var fireBaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Initialize Firebase Authentication
        fireBaseAuth = FirebaseAuth.getInstance()

        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray

        fireBaseAuth = FirebaseAuth.getInstance()
        val user = fireBaseAuth.currentUser

        if (user != null) {
            email = user.email.toString()
            binding.userEmail.text = email
        }

        // Get total tasks and categories
        getTotalCounts()

        // This makes the nav bar show what page we are on.
        bottomNavigationView.menu.findItem(R.id.profile_icon)?.isChecked = true

        // Initialize PopupMenu
        popupMenu = PopupMenu(this, fabPopupTray)
        popupMenu.inflate(R.menu.popup_menu)

        // Set Listeners
        fabPopupTray.setOnClickListener(this)
        popupMenu.setOnMenuItemClickListener(this)
        binding.saveButton.setOnClickListener {
            saveNumbers(username)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile_icon -> {
                    // Stay on page
                    true
                }
                R.id.bell_icon -> {
                    // Proceed to notification page
                    navigateToStats()
                    finish()
                    true
                }
                R.id.home_icon -> {
                    // Proceed to home page
                    navigateToHome()
                    finish()
                    true
                }
                R.id.controller_icon -> {
                    // Proceed to game page
                    navigateToGame()
                    finish()
                    true
                }
                else -> false
            }
        }

        // Display Total Tasks and Categories
        binding.totalTasksMade.text = tasks
        binding.totalCategoriesMade.text = categories

        username = email.substringBefore('@')
        binding.tvName.text = username

        // Logout button
        binding.logoutbtn.setOnClickListener {
            // Log out the current user
            fireBaseAuth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            // Redirect the user to the login screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabPopupTray -> {
                popupMenu.show()
            }
        }
    }

    // Menu items for Floating Action Button (plus sign)
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.addTask -> {
                // Proceed to add a task
                val intent = Intent(this, AddTask::class.java)
                startActivity(intent)
                return true
            }
            R.id.addCategory -> {
                // Proceed to add a category
                val intent = Intent(this, AddCategory::class.java)
                startActivity(intent)
                return true
            }
            else -> return false
        }
    }

    // Methods to navigate to different pages
    private fun navigateToStats() {
        // Proceed to Stats page
        val intent = Intent(this, Statistics::class.java)
        startActivity(intent)
    }

    private fun navigateToHome() {
        // Proceed to home page
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToGame() {
        // Proceed to game page
        val intent = Intent(this, Game::class.java)
        startActivity(intent)
    }

    // Function to save the min/max daily goals entered by the user
    private fun saveNumbers(username: String) {
        val minNumberText = binding.minNum.text.toString()
        val maxNumberText = binding.maxNum.text.toString()

        if (minNumberText.isNotEmpty() && maxNumberText.isNotEmpty()) {
            val minNumber = minNumberText.toIntOrNull() ?: 0
            val maxNumber = maxNumberText.toIntOrNull() ?: 0

            if (minNumber >= maxNumber) {
                Toast.makeText(this, "Invalid, minimum hours cannot be higher than maximum!", Toast.LENGTH_SHORT).show()
            } else {
                val firebaseAuth = FirebaseAuth.getInstance().currentUser
                val userID = firebaseAuth?.uid.toString()
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                val databaseReference = FirebaseDatabase.getInstance().getReference("DailyGoals")
                val goalID = databaseReference.push().key.toString()

                val appUsageMillis = sharedPreferences.getLong("TOTAL_TIME_IN_APP_$username", 0)
                val appUsageTime = formatTimeFromMillis(appUsageMillis)

                val newDailyGoal = DailyGoal(userID, currentDate, minNumber.toDouble(), maxNumber.toDouble(), appUsageTime)
                databaseReference.child(goalID).setValue(newDailyGoal).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Daily goal set successfully!", Toast.LENGTH_SHORT).show()
                        binding.minNum.text.clear()
                        binding.maxNum.text.clear()
                    } else {
                        Toast.makeText(this, "Failed to set daily goal!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Invalid, minimum/maximum hours cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatTimeFromMillis(millis: Long): String {
        val hours = (millis / (1000 * 60 * 60)) % 24
        val minutes = (millis / (1000 * 60)) % 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


    private fun getTotalCounts() {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val userId = fireBaseAuth.currentUser?.uid

        // Retrieve total number of tasks for the current user
        val tasksRef = databaseReference.child("Tasks").orderByChild("userID").equalTo(userId)
        tasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val totalTasks = dataSnapshot.childrenCount
                binding.totalTasksMade.text = totalTasks.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Profile, "Failed to retrieve tasks count.", Toast.LENGTH_SHORT).show()
            }
        })

        // Retrieve total number of categories for the current user
        val categoriesRef = databaseReference.child("Categories").orderByChild("userID").equalTo(userId)
        categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val totalCategories = dataSnapshot.childrenCount
                binding.totalCategoriesMade.text = totalCategories.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Profile, "Failed to retrieve categories count.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}