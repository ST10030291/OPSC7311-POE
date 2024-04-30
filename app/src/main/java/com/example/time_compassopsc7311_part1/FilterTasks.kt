package com.example.time_compassopsc7311_part1

import CategoryList
import TaskList
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.time_compassopsc7311_part1.databinding.ActivityFilterTasksBinding
import com.example.time_compassopsc7311_part1.databinding.ActivityHomeBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FilterTasks : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityFilterTasksBinding
    private lateinit var popupMenu: PopupMenu
    private lateinit var searchBtn : Button
    private lateinit var categoryChoice : Spinner
    private lateinit var startDate : TextView
    private lateinit var endDate : TextView
    private lateinit var displayTotalHours : TextView
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskAdapter: FilterTasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = FilterTasksAdapter(emptyList()) // Initialize with empty list
        tasksRecyclerView.adapter = taskAdapter

        //for category spinner
        categoryChoice = findViewById(R.id.categoryOption)
        displayTotalHours = findViewById(R.id.displayHours)
        val categoryName = CategoryList.categoryList.map { it.categoryName }.toTypedArray()

        //for total Hours

        //val categoryColor = CategoryList.categoryList.map { it.color }.toTypedArray()
        val arrayAdapterCat = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categoryName)
        categoryChoice.adapter = arrayAdapterCat
        categoryChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //categoryChoice.setBackgroundColor(categoryColor[position].toColorInt())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }


        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray
        searchBtn = binding.savebutton

        // Initialize PopupMenu
        popupMenu = PopupMenu(this, fabPopupTray)
        popupMenu.inflate(R.menu.popup_menu)

        // Set Listeners
        fabPopupTray.setOnClickListener(this)
        popupMenu.setOnMenuItemClickListener(this)
        startDate = binding.startDate
        endDate = binding.endDate
        startDate.setOnClickListener(this)
        endDate.setOnClickListener(this)
        searchBtn.setOnClickListener {
            filterTask()
        }

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
            R.id.startDate -> {
                datePicker(startDate)
            }
            R.id.endDate -> {
                datePicker(endDate)
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

    private fun datePicker(textView: TextView) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val date = "$dayOfMonth/${monthOfYear + 1}/$year"
                textView.text = date
            },
            year,
            month,
            day
        )
        datePickerDialog.setOnShowListener {
            val dateLayout = it as DatePickerDialog
            val okButton = dateLayout.getButton(DatePickerDialog.BUTTON_POSITIVE)
            okButton.setTextColor(Color.BLACK)
        }

        datePickerDialog.show()
    }

    private fun filterTask() {
        val category = categoryChoice.selectedItem.toString()
        val startTime = startDate.text.toString()
        val endTime = endDate.text.toString()

        // Check if start date and end date are not empty
        if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
            val startDateMillis = getDateInMillis(startTime)
            val endDateMillis = getDateInMillis(endTime)
            var timeDifferenceHours = totalHours(categoryChoice.selectedItem.toString(), startTime, endTime)
            displayTotalHours.setText("Total Hours: " + (timeDifferenceHours/3600000).toString())
            // Check if start date is before end date
            if (startDateMillis <= endDateMillis) {
                val filteredTasks = TaskList.taskList.filter { task ->
                    val taskDateMillis = getDateInMillis(task.taskDate)
                    taskDateMillis in startDateMillis..endDateMillis && task.category ==categoryChoice.selectedItem.toString()
                }

                // Update RecyclerView with filtered tasks
                taskAdapter.updateFilteredTasks(filteredTasks)
            } else {
                showToast("Start date cannot be after end date")
            }
        } else {
            showToast("Please select start and end dates")
        }
    }

    private fun getDateInMillis(dateString: String): Long {
        val pattern = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return try {
            val date = sdf.parse(dateString)
            date?.time ?: 0
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }
    private fun totalHours(categoryName: String, startDateString: String, endDateString: String): Int{
        /*val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate = formatter.parse(startDateString)
        val endDate = formatter.parse(endDateString)*/
        val filterByCategory = TaskList.taskList.filter { it.category == categoryName && it.taskDate >= startDateString && it.taskDate <= endDateString}
        var total = 0
        for(taskEntry in filterByCategory){
            total += taskEntry.timeDifferenceSeconds.toInt()
        }
        return total
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}