package com.example.fundoapp.service

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fundoapp.roomdb.entity.NoteEntity
import com.example.fundoapp.ui.MainActivity
import com.example.fundoapp.service.model.NotesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import service.FirebaseDatabase

class SyncWorker(val context: Context, val workerParams: WorkerParameters) :
    Worker(context, workerParams) {
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

        val fbNotes = FirebaseDatabase.readNotes().map {
            Pair<String, NotesKey>(it.key, it)
        }.toMap()

        val roomNotes = MainActivity.roomDBClass.noteDao.readNotes(uid).map {
            Pair<String, NoteEntity>(it.fid, it)
        }.toMap()

        Log.d("syncing", "Syncing to database " + fbNotes.size + " " + roomNotes.size)


        if (roomNotes.isEmpty()) {
//            val dbService = DBService(MainActivity.roomDBClass,context = context)
//            dbService.fillNotes()
        } else {
            for (i in fbNotes.keys) {
                if (i in roomNotes.keys) {
                    val tempRoom = roomNotes.getValue(i)
                    val tempFB = fbNotes.getValue(i)
                    if (tempFB.mTime < tempRoom.modifiedTime) {
                        if (tempRoom.deletedForever) {
                            FirebaseDatabase.deleteNote(i)
                            MainActivity.roomDBClass.noteDao.deletePermanently(i)
                            //delete from room
                        } else {
                            val updatedNotes = NotesKey(
                                title = tempRoom.title,
                                note = tempRoom.note,
                                key = tempRoom.fid,
                                deleted = tempRoom.deleted,
                                mTime = tempRoom.modifiedTime
                            )

                            FirebaseDatabase.updateNote(updatedNotes)
                        }
                    }
                }
            }
            for (i in roomNotes.keys) {
                if (i !in fbNotes.keys) {
                    val tempRoom = roomNotes.getValue(i)
                    if (!tempRoom.deletedForever) {
                        val newlyAddedNote = NotesKey(
                            title = roomNotes.getValue(i).title,
                            note = roomNotes.getValue(i).note,
                            key = roomNotes.getValue(i).fid,
                            deleted = roomNotes.getValue(i).deleted,
                            mTime = roomNotes.getValue(i).modifiedTime
                        )
                        FirebaseDatabase.addNote(newlyAddedNote)
                    } else {
                        MainActivity.roomDBClass.noteDao.deletePermanently(i)
                        //delete from room now
                    }
                }
            }
        }
    }
}