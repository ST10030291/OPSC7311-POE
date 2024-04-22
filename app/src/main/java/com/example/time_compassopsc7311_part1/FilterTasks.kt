package com.example.time_compassopsc7311_part1

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import com.example.time_compassopsc7311_part1.databinding.ActivityFilterTasksBinding
import com.example.time_compassopsc7311_part1.databinding.ActivityHomeBinding
import java.util.Calendar

class FilterTasks : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityFilterTasksBinding
    private lateinit var popupMenu: PopupMenu
    private lateinit var searchBtn : Button
    private lateinit var categoryChoice : Spinner
    private lateinit var taskChoice : Spinner
    private lateinit var date : TextView
    private lateinit var startText : TextView
    private lateinit var endText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                    finish()
                    true
                }
                R.id.bell_icon -> {
                    navigateToStats()
                    finish()
                    true
                }
                R.id.controller_icon -> {
                    navigateToGame()
                    finish()
                    true
                }
                R.id.home_icon -> {
                    navigateToHome()
                    true
                }
                else -> false
            }
        }
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

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun datePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            // on below line we are passing context.
            this,
            { view, year, monthOfYear, dayOfMonth ->
                // on below line we are setting
                // date to our edit text.
                val dat = (dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                date.setText(dat)
            },
            // on below line we are passing year, month
            // and day for the selected date in our date picker.
            year,
            month,
            day
        )
        datePickerDialog.setOnShowListener{
            val dateLayout = it as DatePickerDialog
            val okButton = dateLayout.getButton(DatePickerDialog.BUTTON_POSITIVE)//had to set a colour the themes in android studio wont show my buttons
            okButton.setTextColor(Color.BLACK)
        }

        datePickerDialog.show()
    }

    private fun startTime(){
        val cal = Calendar.getInstance()
        val hourOfDay = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            // on below line we are passing context.
            this,
            { view, selectHour, selectMinute ->
                // on below line we are setting
                // date to our edit text.
                var selected = String.format("%02d:%02d", selectHour, selectMinute)
                startText.setText(selected)
            },
            // on below line we are passing year, month
            // and day for the selected date in our date picker.
            hourOfDay,
            minute,
            true
        )
        timePickerDialog.setOnShowListener {
            val timeLayout = it as TimePickerDialog
            val okButton = timeLayout.getButton(TimePickerDialog.BUTTON_POSITIVE)
            okButton.setTextColor(Color.BLACK)
        }
        timePickerDialog.show()
    }
    private fun endTime(){
        val cal = Calendar.getInstance()
        val hourOfDay = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            // on below line we are passing context.
            this,
            { view, selectHour, selectMinute ->
                // on below line we are setting
                // date to our edit text.
                var selected = String.format("%02d:%02d", selectHour, selectMinute)
                endText.setText(selected)
            },
            // on below line we are passing year, month
            // and day for the selected date in our date picker.
            hourOfDay,
            minute,
            true
        )
        timePickerDialog.setOnShowListener {
            val timeLayout = it as TimePickerDialog
            val okButton = timeLayout.getButton(TimePickerDialog.BUTTON_POSITIVE)
            okButton.setTextColor(Color.BLACK)
        }
        timePickerDialog.show()
    }

    private fun searchTask(){
        val category = categoryChoice.selectedItem.toString()
        val taskDate = date.text.toString()
        val startTime = startText.text.toString()
        val endTime = endText.text.toString()

        // Search 
    }
}