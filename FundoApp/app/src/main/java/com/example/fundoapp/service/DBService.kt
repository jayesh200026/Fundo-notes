package com.example.fundoapp.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import service.FirebaseDatabase
import com.example.fundoapp.roomDB.entity.NoteEntity
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.roomDB.entity.UserEntity
import com.example.fundoapp.util.NetworkHandler
import com.example.fundoapp.service.model.User
import java.util.*


class DBService(val roomDB: RoomDatabase, val context: Context) {
    suspend fun addNote(uid: String, title: String, note: String): Boolean {

        var status = false
        var roomAdditionStatus = false

        val networkStatus = NetworkHandler.checkForInternet(context)
        val time = System.currentTimeMillis().toString()
        return withContext(Dispatchers.IO) {
            val id = UUID.randomUUID().toString()
            if (networkStatus) {
                val fbNote = NotesKey(title, note, id, mTime = time)
                status = FirebaseDatabase.addNote(fbNote)
                val sqlNote =
                    NoteEntity(uid = uid, fid = id, title = title, note = note, modifiedTime = time)
                roomAdditionStatus = roomAddNote(sqlNote)
                status && roomAdditionStatus

            } else {
                val sqlNote =
                    NoteEntity(uid = uid, fid = id, title = title, note = note, modifiedTime = time)
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
            val uid=Authentication.getCurrentUid()
            if(uid !=null) {
                listFromRoom =
                    roomDB.noteDao.readNotes(uid)
                Log.d("roomNoteSize", listFromRoom.size.toString())

                for (i in listFromRoom) {
                    val note = NotesKey(i.title, i.note, i.fid, i.deleted, i.modifiedTime)
                    tempList.add(note)
                }
            }
            tempList
        }
    }


    suspend fun updateNote(key: String, title: String, note: String, context: Context): Boolean {
        val networkStatus = NetworkHandler.checkForInternet(context)
        val time = System.currentTimeMillis().toString()
        return withContext(Dispatchers.IO) {
            var roomStatus = 0
            if (networkStatus) {
                val userNote = NotesKey(title, note, key, mTime = time)
                FirebaseDatabase.updateNote(userNote)
                roomStatus = roomDB.noteDao.updateNote(title, note, key, time)
            } else {
                roomStatus = roomDB.noteDao.updateNote(title, note, key, time)
            }
            roomStatus > 0
        }
    }

    suspend fun deleteNote(title: String, note: String, key: String): Boolean {

        val time = System.currentTimeMillis().toString()
        val networkStatus = NetworkHandler.checkForInternet(context)
        return withContext(Dispatchers.IO) {
            var roomStatus = 0

            if (networkStatus) {
                val userNote =
                    NotesKey(title = title, note = note, key = key, deleted = true, mTime = time)
                FirebaseDatabase.updateNote(userNote)
                roomStatus = roomDB.noteDao.deleteNote(key, true, time)
            } else {
                roomStatus = roomDB.noteDao.deleteNote(key, true, time)
            }
            roomStatus > 0
        }
    }

    suspend fun restoreNote(title: String, note: String, key: String): Boolean {
        val time = System.currentTimeMillis().toString()
        val networkStatus = NetworkHandler.checkForInternet(context)
        return withContext(Dispatchers.IO) {
            var restoreStatus = 0
            if (networkStatus) {
                val userNote =
                    NotesKey(title = title, note = note, key = key, deleted = false, mTime = time)
                FirebaseDatabase.updateNote(userNote)
                restoreStatus = roomDB.noteDao.restoreNote(key, false, time)
            } else {
                restoreStatus = roomDB.noteDao.restoreNote(key, false, time)
            }
            restoreStatus > 0
        }
    }

    suspend fun deleteForever(key: String): Boolean {
        val networkStatus = NetworkHandler.checkForInternet(context)
        return withContext(Dispatchers.IO) {
            var deleteStatus = 0
            if (networkStatus) {
                FirebaseDatabase.deleteNote(key)
                deleteStatus = roomDB.noteDao.deleteForever(key)
            } else {
                deleteStatus = roomDB.noteDao.deleteForever(key)
            }
            deleteStatus > 0
        }
    }

    suspend fun registerUser(name: String, age: String, email: String): Boolean {
        val networkStatus = NetworkHandler.checkForInternet(context)
        return withContext(Dispatchers.IO) {
            if (networkStatus) {
                var roomStatus=0L
                val fbStatus = FirebaseDatabase.addUser(User(name, age, email))
                val uid = Authentication.getCurrentUid()
                if(uid!=null) {
                     roomStatus = roomDB.userDao.registerUser(
                        UserEntity(
                            uid = uid,
                            name = name,
                            age = age,
                            email = email
                        )
                    )
                }
                fbStatus && roomStatus > 0
            } else {
                Toast.makeText(context, "Check your connection", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }
}