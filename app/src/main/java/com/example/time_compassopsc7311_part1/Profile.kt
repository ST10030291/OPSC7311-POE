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
    private var startTime: Long = 0
    private var totalTimeInApp: Long = 0
    private var minNumber: Int = 0
    private var maxNumber: Int = 0

    private val handler = android.os.Handler()
    private val updateInterval = 1000L
    private var isAppRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Initialize Firebase Authentication
        fireBaseAuth = FirebaseAuth.getInstance()

        // Retrieve start time and MIN/MAX values from SharedPreferences
        startTime = sharedPreferences.getLong("START_TIME_$username", System.currentTimeMillis())

        binding.minNumTV.text = minNumber.toString()
        binding.maxNumTV.text = maxNumber.toString()

        // Retrieve the time when the app was last destroyed
        val appDestroyedTime = sharedPreferences.getLong("APP_DESTROYED_TIME_$username", 0)
        // Calculate the total time spent in the app since the last destruction
        totalTimeInApp = sharedPreferences.getLong("TOTAL_TIME_IN_APP_$username", 0) + max(0, System.currentTimeMillis() - appDestroyedTime)

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
        updateAppUsage()
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
            binding.minNumTV.text = minNumberText
            binding.maxNumTV.text = maxNumberText

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

                // Calculate the current app usage time consistently
                val currentTime = System.currentTimeMillis()
                val elapsedTime = totalTimeInApp + (currentTime - startTime)

                // Format the elapsed time to "HH:mm:ss"
                val hours = elapsedTime / 3600000
                val minutes = (elapsedTime % 3600000) / 60000
                val seconds = ((elapsedTime % 3600000) % 60000) / 1000
                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)

                val newDailyGoal = DailyGoal(userID, currentDate, minNumber.toDouble(), maxNumber.toDouble(), formattedTime)
                databaseReference.child(goalID).setValue(newDailyGoal).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        with(sharedPreferences.edit()) {
                            putInt("MIN_NUMBER_$username", minNumber)
                            putInt("MAX_NUMBER_$username", maxNumber)
                            apply()
                        }
                        Toast.makeText(this, "Daily goal set successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to set daily goal!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Invalid, minimum/maximum hours cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to update app usage display with live count
    private fun updateAppUsage() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isAppRunning) {
                    val currentTime = System.currentTimeMillis()
                    val elapsedTime = currentTime - startTime
                    val hours = elapsedTime / 3600000
                    val minutes = (elapsedTime % 3600000) / 60000
                    val seconds = ((elapsedTime % 3600000) % 60000) / 1000
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
                    binding.appUsageTV.text = formattedTime

                    // Reset min and max values if 24 hours have passed
                    if (elapsedTime >= 24 * 3600000) {
                        minNumber = 0
                        maxNumber = 0
                        binding.minNumTV.text = minNumber.toString()
                        binding.maxNumTV.text = maxNumber.toString()

                        // Reset app usage timer
                        binding.appUsageTV.text = "00:00:00"

                        // Reset start time
                        startTime = currentTime
                        // Update SharedPreferences
                        with(sharedPreferences.edit()) {
                            putLong("START_TIME_$username", startTime)
                            apply()
                        }
                    }

                    // Schedule the next update
                    handler.postDelayed(this, updateInterval)
                }
            }
        }, updateInterval)
    }

    override fun onPause() {
        super.onPause()
        isAppRunning = false
        // Calculate the total time spent in the app before pausing
        val currentTime = System.currentTimeMillis()
        totalTimeInApp += currentTime - startTime

        // Save the total time spent in the app and the start time
        with(sharedPreferences.edit()) {
            putLong("TOTAL_TIME_IN_APP_$username", totalTimeInApp)
            putLong("START_TIME_$username", startTime)
            apply()
        }

        // Remove any pending handler callbacks to pause the timer
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        isAppRunning = true
        // Retrieve the total time spent in the app and the start time
        totalTimeInApp = sharedPreferences.getLong("TOTAL_TIME_IN_APP_$username", 0)
        startTime = sharedPreferences.getLong("START_TIME_$username", System.currentTimeMillis())

        // Restart the timer
        updateAppUsage()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Save the current time when the app is destroyed
        val currentTime = System.currentTimeMillis()

        // Save the total time spent in the app and the current time
        with(sharedPreferences.edit()) {
            putLong("TOTAL_TIME_IN_APP_$username", totalTimeInApp + currentTime - startTime)
            putLong("APP_DESTROYED_TIME_$username", currentTime)
            apply()
        }
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

        // Retrieve min and max numbers if they exist for today
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dailyGoalsRef = databaseReference.child("DailyGoals").orderByChild("userID").equalTo(userId)
        dailyGoalsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val dailyGoal = snapshot.getValue(DailyGoal::class.java)
                    if (dailyGoal != null && dailyGoal.currentDate == currentDate) {
                        binding.minNumTV.text = dailyGoal.minValue.toString()
                        binding.maxNumTV.text = dailyGoal.maxValue.toString()
                        break
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Profile, "Failed to retrieve daily goals.", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
