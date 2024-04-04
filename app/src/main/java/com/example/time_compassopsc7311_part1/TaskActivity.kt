package com.example.time_compassopsc7311_part1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.time_compassopsc7311_part1.databinding.ActivityHomeBinding

class TaskActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.fabPopupTray -> {
                    val popUpMenu = PopupMenu(this@TaskActivity, v)
                    popUpMenu.inflate(R.menu.popup_menu)
                    popUpMenu.show()
                }
            }
        }
    }
}
