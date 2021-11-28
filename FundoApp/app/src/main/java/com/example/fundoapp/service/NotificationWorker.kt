package com.example.fundoapp.service

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fundoapp.service.model.NotesKey

class NotificationWorker(val context: Context, val workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val noteTitle = inputData.getString("noteTitle")
        val noteContent = inputData.getString("noteContent")
        val key = inputData.getString("noteKey")
        val deleted = inputData.getBoolean("isDeleted", false)
        val archived = inputData.getBoolean("isArchived", false)
        val modifiedTime = inputData.getString("modifiedTime")
        val reminder = inputData.getLong("reminder", 0L)
        Log.d("notification", "$noteTitle $noteContent")
        val note = NotesKey(noteTitle!!,noteContent!!,key!!,deleted,archived,modifiedTime!!,reminder)
        NotificationHelper.createSampleDataNotification(context,note)
        return Result.success()
    }
}