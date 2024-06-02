package com.example.time_compassopsc7311_part1

import DailyGoal
import DailyGoalList.dailyGoalList
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.time_compassopsc7311_part1.databinding.ActivityStatisticsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Statistics : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var popupMenu: PopupMenu
    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var startDateTv : TextView
    private lateinit var endDateTv : TextView
    private lateinit var databaseReference: FirebaseDatabase
    private lateinit var lineChart : LineChart
    private lateinit var barChart : BarChart
    private val xValues = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        getDailyGoalsFromDb()

        startDateTv = binding.startDate
        endDateTv = binding.endDate
        startDateTv.setOnClickListener(this)
        endDateTv.setOnClickListener(this)

        binding.filterIcon.setOnClickListener {
            if (startDateTv.text.isEmpty() || endDateTv.text.isEmpty()) {
                Toast.makeText(this, "Please select a date range for both fields", Toast.LENGTH_SHORT).show()
            }
            else {
                getFilteredDataFromDb()
            }
        }

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

        bottomNavigationView.setOnItemSelectedListener { item ->
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
            R.id.startDate -> {
                datePicker(startDateTv)
            }
            R.id.endDate -> {
                datePicker(endDateTv)
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
    private fun initialiseLineChart(entryList: List<DailyGoal>) {
        lineChart = binding.lineChart1

        lineChart.description.isEnabled = false
        lineChart.axisRight.setDrawLabels(false)

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.setLabelCount(7)
        xAxis.granularity = 1f
        xAxis.axisLineColor = Color.WHITE
        xAxis.textColor = Color.WHITE
        xAxis.setDrawGridLines(false)

        val yAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisLineWidth = 2f
        yAxis.axisLineColor = Color.WHITE
        yAxis.labelCount = 10
        yAxis.textColor = Color.WHITE
        yAxis.setDrawGridLines(false)

        setGraphEntriesUsingDb(entryList)
    }

    private fun setGraphEntriesUsingDb(entryList: List<DailyGoal>){
        val entries1 = ArrayList<Entry>()
        val entries2 = ArrayList<Entry>()
        var positionInGraph = 0f
        val entries3 = ArrayList<Entry>()

        for (element in entryList){
            entries1.add(Entry(positionInGraph, element.minValue.toFloat()))
            entries2.add(Entry(positionInGraph, element.maxValue.toFloat()))
            entries3.add(Entry(positionInGraph, splitAppUsageIntoHours(element.appUsageTime)))

            positionInGraph++
        }
        setLineChartDataSet(entries1, entries2, entries3)
    }

    private fun setLineChartDataSet(entries1 : ArrayList<Entry>, entries2 : ArrayList<Entry>, entries3 : ArrayList<Entry>) {
        val dataSet1 = LineDataSet(entries1, "Minimum daily goal in Hours")
        dataSet1.setColor(Color.RED)
        dataSet1.valueTextColor = Color.WHITE
        dataSet1.valueTextSize = 16f

        val dataSet2 = LineDataSet(entries2, "Maximum daily goal in hours")
        dataSet2.setColor(Color.MAGENTA)
        dataSet2.valueTextColor = Color.WHITE
        dataSet2.valueTextSize = 16f

        val dataSet3 = LineDataSet(entries3, "Daily app usage in Hours")
        dataSet3.setColor(Color.CYAN)
        dataSet3.valueTextColor = Color.WHITE
        dataSet3.valueTextSize = 16f

        val lineData = LineData(dataSet1, dataSet2, dataSet3)

        lineChart.setNoDataText("Not enough information available to create graph!")
        lineChart.data = lineData
        lineChart.legend.textColor = Color.WHITE

        lineChart.animateX(300)

        lineChart.invalidate()
    }

    private fun initialiseBarChart(entryList: List<DailyGoal>) {
        barChart = binding.barChart1
        barChart.axisRight.setDrawLabels(false)

        addEntriesForBarChart(entryList)

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
        yAxis.axisLineWidth = 2f
        yAxis.axisLineColor = Color.WHITE
        yAxis.labelCount = 10
        yAxis.textColor = Color.WHITE
        yAxis.setDrawGridLines(false)
    }

    private fun addEntriesForBarChart(entryList: List<DailyGoal>) {
        val entries1 = ArrayList<BarEntry>()
        var positionInGraph = 0f

        for (element in entryList){
            entries1.add(BarEntry(positionInGraph, splitAppUsageIntoHours(element.appUsageTime)))

            positionInGraph++
        }

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

        barChart.animateY(2000)

        barChart.invalidate()
    }

    private fun createGraphColorsArray() : ArrayList<Int> {
        val colors = ArrayList<Int>()

        colors.add(ContextCompat.getColor(this, R.color.red))
        colors.add(ContextCompat.getColor(this, R.color.purple))
        colors.add(ContextCompat.getColor(this, R.color.blue))

        return colors
    }

    private fun getFilteredDataFromDb() {
        //use this code when you can select the start and end dates
        val startDate = binding.startDate.text.toString()
        val endDate = binding.endDate.text.toString()

        // Check if start date and end date are not empty
        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            val startDateMillis = getDateInMillis(startDate)
            val endDateMillis = getDateInMillis(endDate)

            // Check if start date is before end date
            if (startDateMillis <= endDateMillis) {
                //setting the display
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
                                dailyGoalList.add(dailyGoal)

                            }
                        }

                        val filteredDailyGoals = dailyGoalList.filter { daily ->
                            val dailyGoalDateMillis = getDateInMillis(daily.currentDate)
                            dailyGoalDateMillis in startDateMillis..endDateMillis
                        }

                        if (filteredDailyGoals.isEmpty()) {
                            Toast.makeText(this@Statistics, "No daily goals with this data range available, charts not updated", Toast.LENGTH_LONG).show()
                        }
//                        else if(filteredDailyGoals.any {it.appUsageTime == "00:00:00"})
//                        {
//                            Toast.makeText(this@Statistics, "No app usage recorded! Please use the app for longer", Toast.LENGTH_LONG).show()
//                        }
                        else {
                            initialiseLineChart(filteredDailyGoals)
                            initialiseBarChart(filteredDailyGoals)
                            Toast.makeText(this@Statistics, "Charts updated", Toast.LENGTH_LONG).show()
                        }

                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@Statistics, "Failed to retrieve daily goals.", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            else {
                Toast.makeText(this@Statistics, "Start date cannot be after end date", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(this@Statistics, "Please select start and end dates", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getDateInMillis(dateString: String): Long {
        val pattern = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return try {
            val date = sdf.parse(dateString)
            date?.time ?: 0
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }

    private fun splitAppUsageIntoHours (appUsage: String) : Float{
        val parts = appUsage.split(":")
        val hours = parts[0].toFloat()
        val minutes = parts[1].toFloat() / 60
        val seconds = parts[2].toFloat() / 3600
        val total = hours + minutes + seconds
        return total
    }

    private fun datePicker(textView: TextView) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val date = "$year-${monthOfYear + 1}-$dayOfMonth"
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

//    private fun getDailyGoalsFromDb() {
//        val firebaseAuth = FirebaseAuth.getInstance().currentUser
//        val userID = firebaseAuth?.uid.toString()
//        databaseReference = FirebaseDatabase.getInstance()
//
//        val dailyGoalRef = databaseReference.getReference("DailyGoals").orderByChild("userID").equalTo(userID)
//        dailyGoalList.clear()
//
//        dailyGoalRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (dailyGoalShot in snapshot.children) {
//                    val dailyGoal = dailyGoalShot.getValue(DailyGoal::class.java)
//                    if (dailyGoal != null) {
//                        dailyGoalList.add(dailyGoal)
//                    }
//                }
//                initialiseLineChart(dailyGoalList)
//                initialiseBarChart(dailyGoalList)
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@Statistics, "Failed to retrieve daily goals.", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
}
