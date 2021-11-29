package com.example.fundoapp.service

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fundoapp.R
import com.example.fundoapp.ui.MainActivity
import com.example.fundoapp.util.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("push","inside on message received")
        pushNotification(
            remoteMessage.notification?.title, remoteMessage.notification?.body
        )
    }

    fun pushNotification(title: String?, body: String?) {
        if (title != null && body != null) {
            val channelId = Constants.CHANNEL_ID
            val intent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT
            )

            val notificationBuilder = NotificationCompat.Builder(this, channelId).apply {
                setSmallIcon(R.drawable.ic_baseline_notifications)
                setContentTitle(title)
                setContentText(body)
                priority = NotificationCompat.PRIORITY_DEFAULT
                setAutoCancel(true)
                setContentIntent(pendingIntent)
            }
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(1001, notificationBuilder.build())
        }
    }

}