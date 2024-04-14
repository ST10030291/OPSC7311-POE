package com.example.time_compassopsc7311_part1

import Task
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.time_compassopsc7311_part1.databinding.ActivityAddTaskBinding
import java.io.File
import java.util.Calendar

class AddTask : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private val taskList = mutableListOf<Task>()
    private lateinit var popupMenu: PopupMenu
    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var taskName : TextView
    private lateinit var description : TextView
    private lateinit var categoryChoice : Spinner
    private lateinit var date : TextView
    private lateinit var startText : TextView
    private lateinit var endText : TextView
    private lateinit var galleryImage : ImageView
    private lateinit var pickImageFromGalleryBtn : Button
    private lateinit var pickImageFromCameraBtn : Button
    private lateinit var imageUrl : Uri
    private lateinit var saveBtn : Button
    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){
        galleryImage.setImageURI(null)
        galleryImage.setImageURI(imageUrl)
    }
    private val imageContract = registerForActivityResult(ActivityResultContracts.GetContent()){
        galleryImage.setImageURI(it)
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //declaring the variables
        taskName = binding.taskNameText
        description = binding.descrptionText
        categoryChoice = binding.categoryOption
        //description = findViewById(R.id.descriptionText)
        //categoryChoice = findViewById(R.id.categoryOption)
        date = findViewById(R.id.dateText)
        startText = findViewById(R.id.startText)
        endText = findViewById(R.id.endText)
        galleryImage = findViewById(R.id.imageView)
        pickImageFromGalleryBtn = findViewById(R.id.galleryButton)
        pickImageFromCameraBtn = findViewById(R.id.cameraButton)
        saveBtn = findViewById(R.id.savebutton)

        date.setOnClickListener{
            datePicker()//datepicker function
        }
        startText.setOnClickListener {
            startTime()//time picker function
        }
        endText.setOnClickListener {
            endTime()//time picker function
        }
//images from gallery
        pickImageFromGalleryBtn.setOnClickListener{
        imageContract.launch("image/*")
        }
        imageUrl = createImageUri()
//images from camera
        pickImageFromCameraBtn.setOnClickListener{
        contract.launch(imageUrl)
        }



        saveBtn.setOnClickListener{
            saveTask()
        }
        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray

        // Initialize PopupMenu
        popupMenu = PopupMenu(this, fabPopupTray)
        popupMenu.inflate(R.menu.popup_menu)

        // Set Listeners
        fabPopupTray.setOnClickListener(this)
        popupMenu.setOnMenuItemClickListener(this)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile_icon -> {
                    // Proceed to Profile page
                    navigateToProfile()
                    true
                }
                R.id.bell_icon -> {
                    // Proceed to notification page
                    navigateToNotifications()
                    true
                }
                R.id.home_icon -> {
                    // Proceed to home page
                    navigateToHome()
                    true
                }
                R.id.controller_icon -> {
                    // Proceed to game page
                    navigateToGame()
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
    private fun navigateToNotifications() {
        // Proceed to notifications page
        val intent = Intent(this, Notifications::class.java)
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

    private fun navigateToProfile() {
        // Proceed to profile page
        val intent = Intent(this, Profile::class.java)
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
    private fun createImageUri():Uri{
        val image = File(filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(this,
            "com.coding.captureimage.FileProvider",
            image)
    }
    private fun saveTask(){
        val taskName = taskName.text.toString()
        val description = description.text.toString()
        val category = categoryChoice.selectedItem.toString()
        val taskDate = date.text.toString()
        val startTime = startText.text.toString()
        val endTime = endText.text.toString()
        val taskImg = imageUrl

        val newTask = Task(taskName, description, category, taskDate, startTime, endTime, taskImg)
        taskList.add(newTask)
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

