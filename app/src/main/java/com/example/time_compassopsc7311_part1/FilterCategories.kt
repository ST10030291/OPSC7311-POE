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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray
        searchBtn = binding.savebutton
        categoryChoice = binding.categoryOption

        //displayTotalHours = findViewById(R.id.displayHours)
        val categoryName = CategoryList.categoryList.map { it.categoryName }.toTypedArray()
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
            if (startDate.text.isEmpty() || endDate.text.isEmpty()){
                Toast.makeText(this, "Please enter all required details.", Toast.LENGTH_SHORT).show()
            }
            else{
            filterCategories()
            }
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

        // Filter tasks based on category and date range
        val filteredTasks = taskList.filter { task ->
            task.category == category &&
                    getDateInMillis(task.endTime) >= getDateInMillis(startTime) &&
                    getDateInMillis(task.startTime) <= getDateInMillis(endTime)
        }

        // Calculate total duration for filtered tasks using sumByDouble
        val totalDuration = filteredTasks.sumByDouble { task ->
            val startMillis = getDateInMillis(task.startTime)
            val endMillis = getDateInMillis(task.endTime)
            (endMillis - startMillis) / (1000 * 60 * 60).toDouble() // Convert milliseconds to hours
        }

        // Update the TextView with total duration
        binding.displayTotalCategoryHours.text = String.format(Locale.getDefault(), "Total: %.2f hours", totalDuration)
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