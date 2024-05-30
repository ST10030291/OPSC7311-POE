package com.example.time_compassopsc7311_part1

import DailyGoal
import DailyGoalList.dailyGoalList
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.example.time_compassopsc7311_part1.databinding.ActivityProfileBinding
import com.example.time_compassopsc7311_part1.databinding.ActivityStatisticsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Statistics : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var popupMenu: PopupMenu
    private lateinit var databaseReference: FirebaseDatabase
    private lateinit var lineChart : LineChart
    private lateinit var barChart : BarChart
    private val xValues = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialiseLineChart()
        initialiseBarChart()

        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray

        // This makes the nav bar show what page we are on.
        bottomNavigationView.menu.findItem(R.id.bell_icon)?.isChecked = true

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
                    // Stay on Page
                    true
                }
                R.id.home_icon -> {
                    // Proceed to home page
                    navigateToHome()
                    finish()
                    true
                }
                R.id.controller_icon -> {
                    // Proceed to game page
                    navigateToGame()
                    finish()
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
    private fun initialiseLineChart() {
        lineChart = findViewById(R.id.lineChart1)

        val description = Description()
        description.text = "Min vs Max hours graph"
        description.textColor = Color.WHITE
        description.textSize = 20f
        description.setPosition(800f, 35f)

        lineChart.description = description
        lineChart.axisRight.setDrawLabels(false)

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        xAxis.setLabelCount(7)
        xAxis.granularity = 1f
        xAxis.axisLineColor = Color.WHITE
        xAxis.textColor = Color.WHITE
        xAxis.setDrawGridLines(false)

        val yAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 180f
        yAxis.axisLineWidth = 2f
        yAxis.axisLineColor = Color.WHITE
        yAxis.labelCount = 10
        yAxis.textColor = Color.WHITE
        yAxis.setDrawGridLines(false)

        addEntriesForLineChart()
    }

    private fun addEntriesForLineChart() {
        val entries1 = ArrayList<Entry>()
        entries1.add(Entry(0f, 10f))
        entries1.add(Entry(1f, 20f))
        entries1.add(Entry(2f, 30f))
        entries1.add(Entry(3f, 40f))
        entries1.add(Entry(4f, 50f))

        val entries2 = ArrayList<Entry>()
        entries2.add(Entry(0f, 5f))
        entries2.add(Entry(1f, 15f))
        entries2.add(Entry(2f, 25f))
        entries2.add(Entry(3f, 35f))
        entries2.add(Entry(4f, 45f))

//        val entries3 = ArrayList<Entry>()
//        entries3.add(Entry(0f, 16f))
//        entries3.add(Entry(1f, 32f))
//        entries3.add(Entry(2f, 48f))
//        entries3.add(Entry(3f, 64f))
//        entries3.add(Entry(4f, 80f))

        setLineChartDataSet(entries1, entries2)
    }

    private fun setLineChartDataSet(entries1 : ArrayList<Entry>, entries2 : ArrayList<Entry>) {
        val dataSet1 = LineDataSet(entries1, "Minimum Hours")
        dataSet1.setColor(Color.MAGENTA)
        dataSet1.valueTextColor = Color.WHITE
        dataSet1.valueTextSize = 16f

        val dataSet2 = LineDataSet(entries2, "Maximum Hours")
        dataSet2.setColor(Color.YELLOW)
        dataSet2.valueTextColor = Color.WHITE
        dataSet2.valueTextSize = 16f

        val lineData = LineData(dataSet1, dataSet2)

        lineChart.setNoDataText("No min and max hours recorded! Please set them first")
        lineChart.data = lineData
        lineChart.legend.textColor = Color.WHITE


        lineChart.invalidate()
    }

    private fun initialiseBarChart() {
        barChart = findViewById(R.id.barChart1)
        barChart.axisRight.setDrawLabels(false)

        addEntriesForBarChart()

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        xAxis.setLabelCount(7)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.axisLineColor = Color.WHITE
        xAxis.textColor = Color.WHITE
        xAxis.setDrawGridLines(false)

        val yAxis = barChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 180f
        yAxis.axisLineWidth = 2f
        yAxis.axisLineColor = Color.WHITE
        yAxis.labelCount = 10
        yAxis.textColor = Color.WHITE
        yAxis.setDrawGridLines(false)
    }

    private fun addEntriesForBarChart() {
        val entries1 = ArrayList<BarEntry>()
        entries1.add(BarEntry(0f, 43f))
        entries1.add(BarEntry(1f, 99f))
        entries1.add(BarEntry(2f, 61f))
        entries1.add(BarEntry(3f, 12f))
        entries1.add(BarEntry(4f, 28f))
        entries1.add(BarEntry(5f, 34f))
        entries1.add(BarEntry(6f, 70f))

        setBarChartDataSet(entries1)
    }

    private fun setBarChartDataSet(entries1 : ArrayList<BarEntry>){
        val dataSet1 = BarDataSet(entries1, "Daily hours spent in app")
        dataSet1.colors = createGraphColorsArray()
        dataSet1.valueTextColor = Color.WHITE
        dataSet1.valueTextSize = 16f

        val barData = BarData(dataSet1)

        barChart.data = barData

        barChart.description.isEnabled = false

        barChart.setNoDataText("Daily hours spent not recorded! Please use the app for at least an hour")
        barChart.legend.textColor = Color.WHITE
        barChart.legend.textSize = 16f

        barChart.invalidate()
    }

    private fun createGraphColorsArray() : ArrayList<Int> {
        val colors = ArrayList<Int>()

        colors.add(ContextCompat.getColor(this, R.color.blue))
        colors.add(ContextCompat.getColor(this, R.color.red))
        colors.add(ContextCompat.getColor(this, R.color.green))
        colors.add(ContextCompat.getColor(this, R.color.orange))
        colors.add(ContextCompat.getColor(this, R.color.purple))
        colors.add(ContextCompat.getColor(this, R.color.pink))
        colors.add(ContextCompat.getColor(this, R.color.yellow))

        return colors
    }

    // Not yet working, using dummy data above
    private fun getDailyGoalsFromDb() {
        val firebaseAuth = FirebaseAuth.getInstance().currentUser
        val userID = firebaseAuth?.uid.toString()
        databaseReference = FirebaseDatabase.getInstance()

        val dailyGoalRef = databaseReference.getReference("DailyGoals").orderByChild("userID").equalTo(userID)
        dailyGoalList.clear()

        dailyGoalRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dailyGoalShot in snapshot.children) {
                    val dailyGoal = dailyGoalShot.getValue(DailyGoal::class.java)
                    if (dailyGoal != null) {
//                        val minValue = dailyGoal.minValue.toFloat()
//                        val maxValue = dailyGoal.maxValue.toFloat()
                        dailyGoalList.add(dailyGoal)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}
