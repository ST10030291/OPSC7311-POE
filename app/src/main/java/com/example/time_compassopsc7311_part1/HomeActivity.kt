package com.example.time_compassopsc7311_part1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.time_compassopsc7311_part1.R
import com.example.time_compassopsc7311_part1.databinding.ActivityHomeBinding
import java.util.*
import kotlin.math.max

class HomeActivity : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var popupMenu: PopupMenu
    private lateinit var sharedPreferences: SharedPreferences
    private var username: String = "Unknown user"

    private var startTime: Long = 0
    private var totalTimeInApp: Long = 0
    private var minNumber: Int = 0
    private var maxNumber: Int = 0

    private val handler = android.os.Handler()
    private val updateInterval = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Retrieve the saved username
        username = sharedPreferences.getString("USERNAME", "Default Username") ?: "Unknown user"

        // Retrieve start time and values from SharedPreferences
        startTime = sharedPreferences.getLong("START_TIME", System.currentTimeMillis())
        minNumber = sharedPreferences.getInt("MIN_NUMBER", 0)
        maxNumber = sharedPreferences.getInt("MAX_NUMBER", 0)

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
        binding.saveButton.setOnClickListener {
            saveNumbers()
        }

        updateAppUsage()

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

        // Calculate total time in app from the start
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
        // Proceed to Stats page
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

    // Function to save the numbers entered by the user
    private fun saveNumbers() {
        // Retrieve the values entered in the EditText fields
        val minNumberText = binding.minNum.text.toString()
        val maxNumberText = binding.maxNum.text.toString()

        binding.minNumTV.text = minNumberText
        binding.maxNumTV.text = maxNumberText

        // Convert the string values to integers
        minNumber = minNumberText.toIntOrNull() ?: 0
        maxNumber = maxNumberText.toIntOrNull() ?: 0

        // Save the values to SharedPreferences
        with(sharedPreferences.edit()) {
            putInt("MIN_NUMBER", minNumber)
            putInt("MAX_NUMBER", maxNumber)
            apply()
        }

        updateAppUsage() // Update app usage after saving numbers
        Toast.makeText(this, "Daily goal set successfully!", Toast.LENGTH_SHORT).show()
    }

    // Function to update app usage display with live count
    private fun updateAppUsage() {
        // Schedule the update at regular intervals
        handler.postDelayed(object : Runnable {
            override fun run() {
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
                    // Update TextViews with reset values
                    binding.minNumTV.text = minNumber.toString()
                    binding.maxNumTV.text = maxNumber.toString()
                    // Reset start time
                    startTime = currentTime
                    // Update SharedPreferences
                    with(sharedPreferences.edit()) {
                        putLong("START_TIME", startTime)
                        putInt("MIN_NUMBER", minNumber)
                        putInt("MAX_NUMBER", maxNumber)
                        apply()
                    }
                }

                // Schedule the next update
                handler.postDelayed(this, updateInterval)
            }
        }, updateInterval)
    }

    override fun onPause() {
        super.onPause();

        // Calculate the total time spent in the app before pausing
        val currentTime = System.currentTimeMillis();
        totalTimeInApp += currentTime - startTime;

        // Save the total time spent in the app
        with(sharedPreferences.edit()) {
            putLong("TOTAL_TIME_IN_APP", totalTimeInApp);
            apply();
        }

        // Reset the start time for the next resume cycle
        startTime = currentTime;
    }

    override fun onResume() {
        super.onResume();

        // Retrieve the total time spent in the app
        totalTimeInApp = sharedPreferences.getLong("TOTAL_TIME_IN_APP", 0);

        // Update the start time based on the total time spent
        startTime = System.currentTimeMillis() - totalTimeInApp;

        // Update the TextViews with the latest values (optional)
        binding.minNumTV.text = minNumber.toString()
        binding.maxNumTV.text = maxNumber.toString()
    }

}