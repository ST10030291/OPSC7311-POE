package com.example.time_compassopsc7311_part1

import Task
import TaskList.taskList
import android.app.DatePickerDialog
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
import com.example.time_compassopsc7311_part1.databinding.ActivityFilterCategoriesBinding
import com.example.time_compassopsc7311_part1.databinding.ActivityFilterTasksBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FilterCategories : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityFilterCategoriesBinding
    private lateinit var popupMenu: PopupMenu
    private lateinit var searchBtn: Button
    private lateinit var categoryChoice: Spinner
    private lateinit var startDate: TextView
    private lateinit var endDate: TextView
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var categoriesAdapter: FilterCategoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        categoriesAdapter = FilterCategoriesAdapter(emptyList(), TaskList.taskList, this)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)
        categoriesRecyclerView.adapter = categoriesAdapter

        //for category spinner
        categoryChoice = findViewById(R.id.categoryOption)
        val categoryName = TaskList.taskList.map { it.category }.distinct().toTypedArray()
        val arrayAdapterCat = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categoryName)
        categoryChoice.adapter = arrayAdapterCat

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
            filterCategories()
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

    fun filterCategories() {
        val category = categoryChoice.selectedItem.toString()
        val startTime = startDate.text.toString()
        val endTime = endDate.text.toString()

        val filteredTasks = taskList.filter { task ->
            task.category == category &&
                    getDateInMillis(task.startTime) >= getDateInMillis(startTime) &&
                    getDateInMillis(task.endTime) <= getDateInMillis(endTime)
        }

        // Update the totalHoursByCategory list with the filtered tasks
        val totalHoursByCategory = filteredTasks.map { task ->
            val startTaskTimeMillis = getDateInMillis(task.startTime)
            val endTaskTimeMillis = getDateInMillis(task.endTime)
            val totalTaskTime =
                (endTaskTimeMillis - startTaskTimeMillis) / 3600000.0 // convert milliseconds to hours
            totalTaskTime
        }

        // Update the adapter with the filtered tasks
        categoriesAdapter.updateCategoriesTasks(totalHoursByCategory)
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

}