package com.example.time_compassopsc7311_part1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import com.example.time_compassopsc7311_part1.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHomeBinding
    lateinit var addTaskButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addTaskButton = findViewById(R.id.addTask)

        addTaskButton.setOnClickListener {
            val intent = Intent(this, TaskActivity::class.java)
            startActivity(intent)
        }

        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray

        fabPopupTray.setOnClickListener(this)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile_icon -> {
                    // Proceed to profile page
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    true
                }
                R.id.bell_icon -> {
                    // Proceed to notifications page
                    val intent = Intent(this, Notifications::class.java)
                    startActivity(intent)
                    true
                }
                R.id.home_icon -> {
                    // Proceed to home page
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.controller_icon -> {
                    // Proceed to game page
                    val intent = Intent(this, Game::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.fabPopupTray -> {
                    val popUpMenu = PopupMenu(this@HomeActivity, v)
                    popUpMenu.inflate(R.menu.popup_menu)
                    popUpMenu.show()
                }
            }
        }
    }
}

