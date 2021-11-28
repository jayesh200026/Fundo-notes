package com.example.fundoapp.ui

import android.app.Application
import androidx.core.app.NotificationManagerCompat
import com.example.fundoapp.R
import com.example.fundoapp.service.NotificationHelper

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            getString(R.string.app_name), "App notification channel.")



    }
}