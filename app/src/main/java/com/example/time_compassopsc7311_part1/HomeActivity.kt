package com.example.time_compassopsc7311_part1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.time_compassopsc7311_part1.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}