package com.example.fundoapp.service

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fundoapp.ui.MainActivity
import com.example.fundoapp.service.model.NotesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import service.FirebaseDatabase

class SyncWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        CoroutineScope(Dispatchers.IO).launch {
            val uid = Authentication.getCurrentUid()
            if (uid != null) {
                Log.d("syncing", "going to sync ")

                autoSync(uid)
            }
        }
        return Result.success()
    }

    private suspend fun autoSync(uid: String) {

        val fbNotes = FirebaseDatabase.readNotes()
        val fbKeys = mutableListOf<String>()
        val roomKeys = mutableListOf<String>()
        val roomNotes = MainActivity.roomDBClass.noteDao.readNotes(uid)

        Log.d("syncing", "Syncing to database " + fbNotes.size + " " + roomNotes.size)

        for (i in fbNotes) {
            fbKeys.add(i.key)
        }
        for (i in roomNotes) {
            roomKeys.add(i.fid)
        }

        for (i in 0 until fbNotes.size) {
            if (fbNotes[i].key in roomKeys) {
                val j = roomKeys.indexOf(fbNotes[i].key)
                Log.d("syncing", fbNotes[i].mTime + " " + roomNotes[j].modifiedTime)
                if (fbNotes[i].mTime < roomNotes[j].modifiedTime) {
                    val updatedNotes = NotesKey(
                        title = roomNotes[j].title,
                        note = roomNotes[j].note,
                        key = roomNotes[j].fid,
                        deleted = roomNotes[j].deleted,
                        mTime = roomNotes[j].modifiedTime
                    )

                    FirebaseDatabase.updateNote(updatedNotes)
                }

            } else {
                FirebaseDatabase.deleteNote(fbNotes[i].key)
            }
        }

        for (i in 0 until roomNotes.size) {
            if (roomNotes[i].fid !in fbKeys) {
                val newlyAddedNote = NotesKey(
                    title = roomNotes[i].title,
                    note = roomNotes[i].note,
                    key = roomNotes[i].fid,
                    deleted = roomNotes[i].deleted,
                    mTime = roomNotes[i].modifiedTime
                )
                FirebaseDatabase.addNote(newlyAddedNote)
            }
        }
    }
}