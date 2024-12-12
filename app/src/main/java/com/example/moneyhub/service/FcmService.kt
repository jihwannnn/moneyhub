package com.example.moneyhub.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.moneyhub.R
import com.example.moneyhub.activity.main.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FcmService : FirebaseMessagingService() {
    private val functions = Firebase.functions

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateFcmToken(token)
    }

    private fun updateFcmToken(token: String) {
        val data = hashMapOf("token" to token)
        functions
            .getHttpsCallable("updateFcmToken")
            .call(data)
            .addOnFailureListener { e ->
                Log.e("FCM", "Error updating FCM token", e)
            }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notification = message.notification
        val data = message.data

        showNotification(
            notification?.title ?: "새로운 알림",
            notification?.body ?: "",
            data
        )
    }

    private fun showNotification(title: String, content: String, data: Map<String, String>) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            data.forEach { (key, value) -> putExtra(key, value) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "transaction_notification")
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_logo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}