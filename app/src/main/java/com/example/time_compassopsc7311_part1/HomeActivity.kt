package com.example.time_compassopsc7311_part1

import DailyGoal
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.time_compassopsc7311_part1.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max


class HomeActivity : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var popupMenu: PopupMenu
    private lateinit var sharedPreferences: SharedPreferences
    private var username: String = "Unknown user"
    private lateinit var fireBaseAuth: FirebaseAuth
    private var minNumber: Float = 0f
    private var maxNumber: Float = 0f
    private var startTime: Long = 0
    private var totalTimeInApp: Long = 0
    private val handler = android.os.Handler()
    private val updateInterval = 1000L
    private var isAppRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Initialize Firebase Authentication
        fireBaseAuth = FirebaseAuth.getInstance()

        // Retrieve the saved username
        username = sharedPreferences.getString("USERNAME", "Default Username") ?: "Unknown user"
        // Retrieve start time from SharedPreferences
        startTime = sharedPreferences.getLong("START_TIME_$username", System.currentTimeMillis())

        // Retrieve the time when the app was last destroyed
        val appDestroyedTime = sharedPreferences.getLong("APP_DESTROYED_TIME_$username", 0)

        // Calculate the total time spent in the app since the last destruction
        totalTimeInApp = sharedPreferences.getLong("TOTAL_TIME_IN_APP_$username", 0) + max(0, System.currentTimeMillis() - appDestroyedTime)

        // Set welcome message with username
        binding.button3.text = getString(R.string.welcome_message, username)

        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray

        // Initialize PopupMenu
        popupMenu = PopupMenu(this, fabPopupTray)
        popupMenu.inflate(R.menu.popup_menu)

        // Set Listeners
        fabPopupTray.setOnClickListener(this)
        popupMenu.setOnMenuItemClickListener(this)

        // Links to each page on the navigation bar
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile_icon -> {
                    navigateToProfile()
                    true
                }
                R.id.bell_icon -> {
                    navigateToStats()
                    true
                }
                R.id.controller_icon -> {
                    navigateToGame()
                    true
                }
                R.id.home_icon -> {
                    // Stay on page
                    true
                }
                else -> false
            }
        }
        displayMinAndMaxValues()
        updateAppUsage()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabPopupTray -> {
                popupMenu.show()
            }
        }
    }

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
    private fun navigateToProfile() {
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }

    private fun navigateToStats() {
        val intent = Intent(this, Statistics::class.java)
        startActivity(intent)
    }

    private fun navigateToGame() {
        val intent = Intent(this, Game::class.java)
        startActivity(intent)
    }

    fun navigateToAllCategories(view: View) {
        val intent = Intent(this, CategoryAvailable::class.java)
        startActivity(intent)
    }

    fun navigateToAllTasks(view: View) {
        val intent = Intent(this, TaskAvailable::class.java)
        startActivity(intent)
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

                    // Calculate the app usage percentage (assuming 24 hours is 100%)
                    val appUsagePercentage = (elapsedTime.toFloat() / (24 * 3600000)) * 100
                    setAppUsageProgress(appUsagePercentage)

                    // Reset min and max values if 24 hours have passed
                    if (elapsedTime >= 24 * 3600000) {
                        minNumber = 0f
                        maxNumber = 0f
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

                        // Reset progress bars
                        setMinProgress(0f)
                        setMaxProgress(0f)
                        setAppUsageProgress(0f)
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

    private fun displayMinAndMaxValues() {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val userId = fireBaseAuth.currentUser?.uid

        // Retrieve min and max numbers if they exist for today
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dailyGoalsRef = databaseReference.child("DailyGoals").orderByChild("userID").equalTo(userId)
        dailyGoalsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val dailyGoal = snapshot.getValue(DailyGoal::class.java)
                    if (dailyGoal != null && dailyGoal.currentDate == currentDate) {
                        val minValue = dailyGoal.minValue.toFloat()
                        val maxValue = dailyGoal.maxValue.toFloat()
                        binding.minNumTV.text = minValue.toString()
                        binding.maxNumTV.text = maxValue.toString()

                        // Set progress values for the progress bars
                        setMinProgress(minValue)
                        setMaxProgress(maxValue)
                        break
                    } else {
                        binding.minNumTV.text = "0"
                        binding.maxNumTV.text = "0"

                        // Set default progress values for the progress bars
                        setMinProgress(0f)
                        setMaxProgress(0f)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed to retrieve daily goals.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setMinProgress(minValue: Float) {
        val minProgressBar = binding.circularProgressBarMin
        minProgressBar.progressMax = 24f // Max value is 24
        minProgressBar.progress = minValue
    }

    private fun setMaxProgress(maxValue: Float) {
        val maxProgressBar = binding.circularProgressBarMax
        maxProgressBar.progressMax = 24f // Max value is 24
        maxProgressBar.progress = maxValue
    }

    private fun setAppUsageProgress(appUsageValue: Float) {
        val appUsageProgressBar = binding.circularProgressBarUsage
        appUsageProgressBar.progressMax = 100f // Max value is 100%
        appUsageProgressBar.progress = appUsageValue
    }
}