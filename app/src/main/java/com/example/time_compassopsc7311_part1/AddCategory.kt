package com.example.time_compassopsc7311_part1

import Category
import CategoryList
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import com.example.time_compassopsc7311_part1.databinding.ActivityAddCategoryBinding
import com.example.time_compassopsc7311_part1.databinding.ActivityHomeBinding

class AddCategory : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener{

    private lateinit var binding: ActivityAddCategoryBinding
    private lateinit var popupMenu: PopupMenu
    private lateinit var colorOptn : Spinner
   // private val categroyList = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //select color functionality
        colorOptn = findViewById(R.id.colorOption)
        colorOptn.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val colourSelected = colorOptn.selectedItem.toString()
                colorChange(colourSelected)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        //save Category
        binding.saveButton.setOnClickListener {
            saveCategory()
        }

        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray

        // Initialize PopupMenu
        fabPopupTray.setOnClickListener(this)
        popupMenu = PopupMenu(this, fabPopupTray)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.setOnMenuItemClickListener(this) // Set the listener

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile_icon -> {
                    // Proceed to Profile page
                    navigateToProfile()
                    true
                }
                R.id.bell_icon -> {
                    // Proceed to notification page
                    navigateToStats()
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

    private fun navigateToProfile() {
        // Proceed to profile page
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }
    private fun colorChange(x:String){
        if (x.equals("Dark Blue")){
            colorOptn.setBackgroundColor(Color.parseColor("#393E46"))
        }else if (x.equals("Light Blue")){
            colorOptn.setBackgroundColor(Color.parseColor("#00ADB5"))
        }else if (x.equals("Grey")){
            colorOptn.setBackgroundColor(Color.parseColor("#EEEEEE"))
        }else if (x.equals("Orange")){
            colorOptn.setBackgroundColor(Color.parseColor("#F8B400"))
        }else if (x.equals("Purple")){
            colorOptn.setBackgroundColor(Color.parseColor("#7209B7"))
        }else if (x.equals("Black")){
            colorOptn.setBackgroundColor(Color.parseColor("#FF000000"))
        }else if (x.equals("White")){
            colorOptn.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
        }else if(x.equals("Select Color")){
            colorOptn.setBackgroundColor(Color.parseColor("#D9D9D9"))
        }
    }
    private fun saveCategory(){
        val categoryName = binding.categoryNameText.text.toString()
        val categoryColor = colorOptn.selectedItem.toString()
        val newCategory = Category(categoryName, categoryColor)
        CategoryList.categoryList.add(newCategory)
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}