package com.example.fundoapp.service


import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fundoapp.R
import com.example.fundoapp.ui.MainActivity
import com.example.fundoapp.util.Constants
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

open class FbMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("SellerFirebaseService ", "Refreshed token :: $token")
        sendRegistrationToServer(token)
    }
    private fun sendRegistrationToServer(token: String) {
        // TODO : send token to tour server
    }


    override fun onMessageReceived(message: RemoteMessage) {
        Log.i("notification","title ="+message.notification?.title)
        super.onMessageReceived(message)
        pushNotification(message.notification?.title,message.notification?.body)
    }

    fun pushNotification(title: String?, body: String?) {
        if (title != null && body != null) {
            val channelId = Constants.CHANNEL_ID
            val intent = Intent(this, MainActivity::class.java)
            val bundle = Bundle().apply {
                putString("Destination","home")
            }
            intent.putExtras(bundle)
            val pendingIntent = PendingIntent.getActivity(
                this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
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