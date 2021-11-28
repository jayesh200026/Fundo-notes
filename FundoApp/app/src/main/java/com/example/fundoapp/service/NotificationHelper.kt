package com.example.fundoapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fundoapp.R
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.ui.MainActivity
import com.example.fundoapp.util.Constants

object NotificationHelper {

    fun createNotificationChannel(
        context: Context,
        importance: Int,
        showBadge: Boolean,
        name: String,
        description: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelId = Constants.CHANNEL_ID
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createSampleDataNotification(context: Context, note :NotesKey) {

        val channelId = Constants.CHANNEL_ID

        val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_baseline_notifications)
            setContentTitle(note.title)
            setContentText(note.note)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setAutoCancel(true)

            val bundle = Bundle().apply {
                putString("Destination","userNote")
                putSerializable("reminderNote",note)
            }

            val intent = Intent(context, MainActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            intent.putExtras(bundle)

            //intent.putExtra("Destination","userNote")
//            intent.putExtra("reminderNote",note)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            setContentIntent(pendingIntent)

        }
        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(1001, notificationBuilder.build())
    }



}