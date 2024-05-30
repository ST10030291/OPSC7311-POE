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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Game : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener  {

    private lateinit var popupMenu: PopupMenu
    private lateinit var fireBaseAuth : FirebaseAuth
    private lateinit var databaseReference: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        //update or set the points for the user
        getTotalCounts()


        binding.textView2.setOnClickListener{
            val pointsList = mutableListOf<Points>()
            databaseReference = FirebaseDatabase.getInstance()
            val pointRef = databaseReference.getReference("Points")
            var recyclerView: RecyclerView = findViewById(R.id.gameRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            pointRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(pointShot in snapshot.children){
                        val point = pointShot.getValue(Points::class.java)
                        if(point != null){
                            pointsList.add(point)
                        }
                    }
                    val adapter = GameAdapter(pointsList)
                    recyclerView.adapter = adapter


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
            )
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile_icon -> {
                    // Proceed to profile page
                    navigateToProfile()
                    finish()
                    true
                }
                R.id.bell_icon -> {
                    // Proceed to notifications page
                    navigateToStats()
                    finish()
                    true
                }
                R.id.home_icon -> {
                    // Proceed to home page
                    navigateToHome()
                    finish()
                    true
                }
                R.id.controller_icon -> {
                    // Stay on page
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
    private fun navigateToProfile() {
        // Proceed to profile page
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }

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
    private fun getTotalCounts() {
        val databaseReference = FirebaseDatabase.getInstance().reference
        fireBaseAuth = FirebaseAuth.getInstance()
        val userId = fireBaseAuth.currentUser?.uid
        var email = ""
        var username = ""
        var totalPoints = 0

        // Retrieve total number of tasks for the current user
        val tasksRef = databaseReference.child("Tasks").orderByChild("userID").equalTo(userId)
        tasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalTasks = dataSnapshot.childrenCount
                totalPoints = totalTasks.toInt() * 100
                //binding.totalTasksMade.text = totalTasks.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                 Toast.makeText(this@Game, "Failed to retrieve tasks count.", Toast.LENGTH_SHORT).show()
            }
        })

        // Retrieve total number of categories for the current user
        val categoriesRef = databaseReference.child("Categories").orderByChild("userID").equalTo(userId)
        categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalCategories = dataSnapshot.childrenCount
                totalPoints = (totalCategories.toInt() * 50) + totalPoints
                // binding.totalCategoriesMade.text = totalCategories.toString()
                val fireBaseAuth = FirebaseAuth.getInstance()
                val user = fireBaseAuth.currentUser

                if (user != null) {
                    email = user.email.toString()
                }
                username = email.substringBefore('@')

                val firebaseAuth = FirebaseAuth.getInstance().currentUser
                val userID = firebaseAuth?.uid.toString()
                val FirebaseReference = FirebaseDatabase.getInstance().getReference("Points")
                val points = Points(userID, username, totalPoints)
                FirebaseReference.child(userID).setValue(points)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Game, "Failed to retrieve categories count.", Toast.LENGTH_SHORT).show()
            }
        })

        //var totalPoints = (totalCategoryPoints) + (totalTaskPoints)

    }
}
