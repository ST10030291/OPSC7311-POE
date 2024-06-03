package com.example.time_compassopsc7311_part1

import android.app.LauncherActivity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class NotificationActivity : AppCompatActivity() {

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "com.example.notificationexam"
    private val description = "Test Noti"

    // Declare the button
    private lateinit var btnNotify: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_available)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Initialize the button using findViewById
        btnNotify = findViewById(R.id.btn_notify)

        btnNotify.setOnClickListener {
            Log.d("NotificationActivity", "Button clicked!")  // Log statement

            val intent = Intent(this, LauncherActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableVibration(false)
                notificationManager.createNotificationChannel(notificationChannel)

                builder = Notification.Builder(this, channelId)
                    .setContentTitle("CodeAndroid")
                    .setContentText("Test Noti")
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            this.resources,
                            R.mipmap.ic_launcher
                        )
                    )
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)

            } else {
                builder = Notification.Builder(this)
                    .setContentTitle("CodeAndroid")
                    .setContentText("Test Noti")
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            this.resources,
                            R.mipmap.ic_launcher
                        )
                    )
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
            }

            notificationManager.notify(1234, builder.build())
        }
    }
}

