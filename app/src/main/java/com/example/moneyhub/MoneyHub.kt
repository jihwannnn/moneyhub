package com.example.moneyhub

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MoneyHub : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        FirebaseApp.initializeApp(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "transaction_notification",
                "거래내역 알림",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "새로운 거래내역 알림"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}