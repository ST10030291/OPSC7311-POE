package com.example.time_compassopsc7311_part1

import Points
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.time_compassopsc7311_part1.databinding.ActivityGameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Game : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var popupMenu: PopupMenu
    private lateinit var fireBaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and Database Reference
        fireBaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray

        // This makes the nav bar show what page we are on.
        bottomNavigationView.menu.findItem(R.id.controller_icon)?.isChecked = true

        // Initialize PopupMenu
        popupMenu = PopupMenu(this, fabPopupTray)
        popupMenu.inflate(R.menu.popup_menu)

        // Set Listeners
        fabPopupTray.setOnClickListener(this)
        popupMenu.setOnMenuItemClickListener(this)

        // Update or set the points for the user
        getTotalCounts()

        // Set up RecyclerView and Firebase data fetching
        setupRecyclerView()

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
                R.id.home_icon -> {
                    navigateToHome()
                    finish()
                    true
                }
                R.id.controller_icon -> true
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        val pointsList = mutableListOf<Points>()
        val pointRef = databaseReference.child("Points")
        val recyclerView: RecyclerView = binding.gameRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        pointRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pointsList.clear()
                for (pointShot in snapshot.children) {
                    val point = pointShot.getValue(Points::class.java)
                    if (point != null) {
                        pointsList.add(point)
                    }
                }
                val adapter = GameAdapter(pointsList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Game, "Failed to retrieve points.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabPopupTray -> popupMenu.show()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.addTask -> {
                startActivity(Intent(this, AddTask::class.java))
                true
            }
            R.id.addCategory -> {
                startActivity(Intent(this, AddCategory::class.java))
                true
            }
            else -> false
        }
    }

    private fun navigateToProfile() {
        startActivity(Intent(this, Profile::class.java))
    }

    private fun navigateToStats() {
        startActivity(Intent(this, Statistics::class.java))
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    private fun getTotalCounts() {
        val userId = fireBaseAuth.currentUser?.uid ?: return
        var totalPoints = 0

        // Retrieve total number of tasks for the current user
        val tasksRef = databaseReference.child("Tasks").orderByChild("userID").equalTo(userId)
        tasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val totalTasks = dataSnapshot.childrenCount.toInt()
                totalPoints += totalTasks * 100

                // Retrieve total number of categories for the current user
                val categoriesRef = databaseReference.child("Categories").orderByChild("userID").equalTo(userId)
                categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val totalCategories = dataSnapshot.childrenCount.toInt()
                        totalPoints += totalCategories * 50

                        val email = fireBaseAuth.currentUser?.email ?: return
                        val username = email.substringBefore('@')

                        val points = Points(userId, username, totalPoints)
                        databaseReference.child("Points").child(userId).setValue(points)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(this@Game, "Failed to retrieve categories count.", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Game, "Failed to retrieve tasks count.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}