package com.example.fundoapp.service

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import service.Firebasedatabase
import com.example.fundoapp.util.NoteEntity
import util.Notes
import com.example.fundoapp.util.NotesKey
import java.util.*


class DBService(val roomDB: RoomDatabase, val context: Context) {
    suspend fun addNote(uid: String, title: String, note: String): Boolean {
        //val fbNote = Notes(title, note)
        var key = ""
        var status = false
        var roomAdditionStatus = false

        val networkStatus = NetworkHandler.checkForInternet(context)
        return withContext(Dispatchers.IO) {
            val id = UUID.randomUUID().toString()
            if (networkStatus) {
                val fbNote = NotesKey(title, note, id)
                status = Firebasedatabase.addNote(fbNote)
                val sqlNote = NoteEntity(uid = uid, fid = id, title = title, note = note)
                roomAdditionStatus = roomAddNote(sqlNote)
                status && roomAdditionStatus

            } else {
                val sqlNote = NoteEntity(uid = uid, fid = id, title = title, note = note)
                roomAdditionStatus = roomAddNote(sqlNote)
                roomAdditionStatus
            }
        }
    }


    fun roomAddNote(sqlNote: NoteEntity): Boolean {
//        roomDB.noteDao.insertNote(sqlNote)
        val insertStatus = roomDB.noteDao.insertNote(sqlNote)
        return insertStatus > 0
    }

    suspend fun readNotes(): MutableList<NotesKey> {
        var tempList = mutableListOf<NotesKey>()
        var listFromRoom = mutableListOf<NoteEntity>()

        return withContext(Dispatchers.IO) {
            listFromRoom =
                roomDB.noteDao.readNotes(Authentication.getCurrentUid()!!)
            Log.d("roomNoteSize", listFromRoom.size.toString())

            for (i in listFromRoom) {
                val note = NotesKey(i.title, i.note, i.fid)
                tempList.add(note)
            }
            tempList
        }
    }


    suspend fun updateNote(key: String, title: String, note: String, context: Context): Boolean {
        val networkStatus = NetworkHandler.checkForInternet(context)
        return withContext(Dispatchers.IO) {
            var roomStatus = 0
            if (networkStatus) {
                val userNote = NotesKey(title, note,key)
                Firebasedatabase.updateNote(userNote)
                roomStatus = roomDB.noteDao.updateNote(title, note, key)
            } else {
                roomStatus = roomDB.noteDao.updateNote(title, note, key)
            }
            roomStatus > 0
        }
    }

    suspend fun deleteNote(uid: String, key: String, context: Context): Boolean {

        return withContext(Dispatchers.IO) {
            var roomStatus = 0
            val networkStatus = NetworkHandler.checkForInternet(context)
            if (networkStatus) {
                Firebasedatabase.deleteNote(key)
                roomStatus = roomDB.noteDao.deleteNote(uid, key)
            } else {
                roomStatus = roomDB.noteDao.deleteNote(uid, key)
            }
            roomStatus > 0
        }
    }


}