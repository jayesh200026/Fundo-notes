package com.example.fundoapp.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import service.FirebaseDatabase
import com.example.fundoapp.roomdb.entity.NoteEntity
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.roomdb.entity.UserEntity
import com.example.fundoapp.roomdb.entity.LabelEntity
import com.example.fundoapp.roomdb.entity.NoteLabelEntity
import com.example.fundoapp.service.model.Label
import com.example.fundoapp.service.model.NoteLabels
import com.example.fundoapp.util.NetworkHandler
import com.example.fundoapp.service.model.User
import com.example.fundoapp.util.SharedPref
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
        val insertStatus = roomDB.noteDao.insertNote(sqlNote)
        return insertStatus > 0
    }

    suspend fun readNotes(): MutableList<NotesKey> {
        return withContext(Dispatchers.IO) {
            var tempList = mutableListOf<NotesKey>()
            val uid = Authentication.getCurrentUid()
            if (uid != null) {
                if (NetworkHandler.checkForInternet(context)) {
                    try {
                        val fblist = FirebaseDatabase.readNotes()
                        tempList.addAll(fblist)
                    } catch (e: Exception) {
                        print(e)
                    }
                } else {
                    val roomList = roomDB.noteDao.readNotes(uid)
                    for (i in roomList) {
                        if (!i.deletedForever) {
                            val note = NotesKey(
                                i.title,
                                i.note,
                                i.fid,
                                i.deleted,
                                i.archived,
                                i.modifiedTime,
                                i.remainder

                            )
                            tempList.add(note)
                        }
                    }
                }
            }
            tempList
        }
    }

    fun addNotesFromFBtoRoom(i: NotesKey, uid: String) {
        val title = i.title
        val note = i.note
        val fid = i.key
        val deleted = i.deleted
        val archived = i.archived
        val modifiedTime = i.mTime
        val remainder = i.remainder
        val roomNote = NoteEntity(
            uid = uid,
            fid = fid,
            title = title,
            note = note,
            deleted = deleted,
            archived = archived,
            modifiedTime = modifiedTime,
            remainder = remainder

        )
        roomDB.noteDao.insertNote(roomNote)
    }


    suspend fun updateNote(note: NotesKey, context: Context): Boolean {
        val networkStatus = NetworkHandler.checkForInternet(context)
        val time = System.currentTimeMillis().toString()
        val uid = Authentication.getCurrentUid()

        return withContext(Dispatchers.IO) {
            var roomStatus = 0
            if (uid != null) {
                if (networkStatus) {
                    val userNote = NotesKey(
                        title = note.title,
                        note = note.note,
                        key = note.key,
                        deleted = note.deleted,
                        archived = note.archived,
                        mTime = time,
                        remainder = note.remainder
                    )
                    FirebaseDatabase.updateNote(userNote)
                    val noteEntity = NoteEntity(
                        title = note.title,
                        note = note.note,
                        uid = uid,
                        fid = note.key,
                        deleted = note.deleted,
                        archived = note.archived,
                        modifiedTime = time,
                        deletedForever = false,
                        remainder = note.remainder
                    )
                    roomStatus = roomDB.noteDao.update(noteEntity)
                } else {
                    val noteEntity = NoteEntity(
                        title = note.title,
                        note = note.note,
                        uid = uid,
                        fid = note.key,
                        deleted = note.deleted,
                        archived = note.archived,
                        modifiedTime = time,
                        deletedForever = false,
                        remainder = note.remainder
                    )
                    roomStatus = roomDB.noteDao.update(noteEntity)
                }
            }
            roomStatus > 0
        }

    }

    suspend fun archiveNote(note: NotesKey): Boolean {
        val networkStatus = NetworkHandler.checkForInternet(context)
        val time = System.currentTimeMillis().toString()
        return withContext(Dispatchers.IO) {
            if (networkStatus) {
                val userNote = NotesKey(
                    title = note.title, note = note.note, key = note.key, archived = true,
                    mTime = time, deleted = note.deleted, remainder = note.remainder
                )
                FirebaseDatabase.updateNote(userNote) && roomDB.noteDao.updateArchive(
                    note.key,
                    true,
                    time
                ) > 0
            } else {
                roomDB.noteDao.updateArchive(note.key, true, time) > 0
            }
        }
    }

    suspend fun unArchive(note: NotesKey): Boolean {
        val networkStatus = NetworkHandler.checkForInternet(context)
        val time = System.currentTimeMillis().toString()
        return withContext(Dispatchers.IO) {
            if (networkStatus) {
                val userNote = NotesKey(
                    title = note.title, note = note.note, key = note.key, archived = false,
                    mTime = time, deleted = note.deleted, remainder = note.remainder
                )
                FirebaseDatabase.updateNote(userNote) && roomDB.noteDao.updateArchive(
                    note.key,
                    false,
                    time
                ) > 0
            } else {
                roomDB.noteDao.updateArchive(note.key, false, time) > 0
            }
        }
    }

    suspend fun deleteNote(note: NotesKey): Boolean {

        val time = System.currentTimeMillis().toString()
        val networkStatus = NetworkHandler.checkForInternet(context)
        return withContext(Dispatchers.IO) {
            var roomStatus = 0

            if (networkStatus) {
                val userNote =
                    NotesKey(
                        title = note.title, note = note.note, key = note.key, deleted = true,
                        archived = note.archived, mTime = time, remainder = note.remainder
                    )
                FirebaseDatabase.updateNote(userNote)
                roomStatus = roomDB.noteDao.deleteNote(note.key, true, time)
            } else {
                roomStatus = roomDB.noteDao.deleteNote(note.key, true, time)
            }
            roomStatus > 0
        }
    }

    suspend fun restoreNote(note: NotesKey): Boolean {
        val time = System.currentTimeMillis().toString()
        val networkStatus = NetworkHandler.checkForInternet(context)
        return withContext(Dispatchers.IO) {
            var restoreStatus = 0
            if (networkStatus) {
                val userNote =
                    NotesKey(
                        title = note.title,
                        note = note.note,
                        key = note.key,
                        deleted = false,
                        archived = note.archived,
                        mTime = time,
                        remainder = note.remainder
                    )
                FirebaseDatabase.updateNote(userNote)
                restoreStatus = roomDB.noteDao.restoreNote(note.key, false, time)
            } else {
                restoreStatus = roomDB.noteDao.restoreNote(note.key, false, time)
            }
            restoreStatus > 0
        }
    }

    suspend fun deleteForever(key: String): Boolean {
        val time = System.currentTimeMillis().toString()
        val networkStatus = NetworkHandler.checkForInternet(context)
        return withContext(Dispatchers.IO) {
            var deleteStatus = 0
            if (networkStatus) {
                FirebaseDatabase.deleteNote(key)
                deleteStatus = roomDB.noteDao.deleteForever(key, true, time)
            } else {
                deleteStatus = roomDB.noteDao.deleteForever(key, true, time)
            }
            deleteStatus > 0
        }
    }

    suspend fun registerUser(name: String, age: String, email: String): Boolean {
        val networkStatus = NetworkHandler.checkForInternet(context)
        return withContext(Dispatchers.IO) {
            if (networkStatus) {
                var roomStatus = 0L
                val fbStatus = FirebaseDatabase.addUser(User(name, age, email))
                val uid = Authentication.getCurrentUid()
                if (uid != null) {
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

    suspend fun createLabel(label: String): Label {
        val time = System.currentTimeMillis().toString()
        return withContext(Dispatchers.IO) {
            val fbStatus = FirebaseDatabase.createLabel(label, time)
            fbStatus
        }
    }

    suspend fun readLabels(): List<Label> {
        return withContext(Dispatchers.IO) {
            val labelList = FirebaseDatabase.readLabels()
            labelList
        }
    }

    suspend fun deleteLabel(labelEntity: Label): Boolean {
        return withContext(Dispatchers.IO) {
            val deletelabelStatus = FirebaseDatabase.deleteLabel(labelEntity.labelId)
            if(deletelabelStatus){
                FirebaseDatabase.deleteNoteLabel(labelEntity.labelId)
            }
            deletelabelStatus
        }
    }

    suspend fun updateLabel(labelEntity: Label, newLabel: String): Boolean {
        val networkStatus = NetworkHandler.checkForInternet(context)
        return withContext(Dispatchers.IO) {
            FirebaseDatabase.updateLabel(labelEntity.labelId, newLabel)
        }
    }


    suspend fun addLabelsToNotes(list: MutableList<Label>): Boolean {
        return withContext(Dispatchers.IO) {
            val key = SharedPref.get("key")
            val time = System.currentTimeMillis().toString()
            if (key != null) {
//                    val labelList = FirebaseDatabase.getLabelsOfNote(key)
//                for(label in labelList){
//                    if(! (label in list)){
//
//                    }
//                }
                for (i in 0 until list.size) {
                    val note = NoteLabels(key, list[i].labelId)
                    val fbStatus = FirebaseDatabase.addLabelsToNote(note, time)
                    fbStatus
                }
            }
            false
        }
    }

    suspend fun clearTables() {
        return withContext(Dispatchers.IO) {
            roomDB.noteDao.clearTable()
            roomDB.labelDao.clearTable()
            roomDB.noteLabelDao.clearTable()
        }
    }

    suspend fun fillNoteLabel() {

        //return withContext(Dispatchers.IO){
        val list = FirebaseDatabase.getNoteLabelRel()
        for (i in list) {
            roomDB.noteLabelDao.addNoteLabelRelationship(i)
        }
        // }

    }


    suspend fun fillNotes() {
        runBlocking {
            val uid = Authentication.getCurrentUid()
            if (uid != null) {
                try {
                    val list = FirebaseDatabase.readNotes()
                    Log.d("Filling", list.size.toString())
                    for (i in list) {

                        addNotesFromFBtoRoom(i, uid)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun fillToRoomDB() {
        return withContext(Dispatchers.IO) {
            Log.d("Filling", "going to fill")
            //fillNotes()
            //fillLabel()
            //fillNoteLabel()
        }
    }

    suspend fun readNotesLabel(): MutableList<NoteLabelEntity> {
        return withContext(Dispatchers.IO) {
            FirebaseDatabase.getNoteLabelRel()
        }
    }

    suspend fun addRemainder(note: NotesKey, remainderTime: Long): Boolean {
        val time = System.currentTimeMillis().toString()
        val networkStatus = NetworkHandler.checkForInternet(context)
        return withContext(Dispatchers.IO) {
            if (networkStatus) {
                val noteToUpdate = NotesKey(
                    note.title,
                    note.note,
                    note.key,
                    note.deleted,
                    note.archived,
                    time,
                    remainderTime
                )
                FirebaseDatabase.updateNote(noteToUpdate) && roomDB.noteDao.updateRemainder(
                    note.key,
                    remainderTime,
                    time
                ) > 0
            } else {
                roomDB.noteDao.updateRemainder(
                    note.key,
                    remainderTime,
                    time
                ) > 0
            }
        }

    }

    suspend fun removeRemainder(note: NotesKey): Boolean {

        val time = System.currentTimeMillis().toString()
        val networkStatus = NetworkHandler.checkForInternet(context)
        return withContext(Dispatchers.IO) {
            var restoreStatus = 0
            if (networkStatus) {
                val userNote =
                    NotesKey(
                        title = note.title,
                        note = note.note,
                        key = note.key,
                        deleted = note.deleted,
                        archived = note.archived,
                        mTime = time,
                        remainder = note.remainder
                    )
                FirebaseDatabase.updateNote(userNote) &&
                        roomDB.noteDao.removeRemainder(note.key, 0L, time) > 0
            } else {
                roomDB.noteDao.removeRemainder(note.key, 0L, time) > 0
            }

        }
    }

    suspend fun readLimitedNotes(key: String, offset: Int): MutableList<NotesKey> {
        val networkStatus = NetworkHandler.checkForInternet(context)
        val uid = Authentication.getCurrentUid()
        var list = mutableListOf<NotesKey>()
        var roomNotesList = mutableListOf<NotesKey>()
        var roomList = mutableListOf<NoteEntity>()
        return withContext(Dispatchers.IO) {
            if (networkStatus && uid != null) {
                roomList = roomDB.noteDao.readLimitedNotes(offset, 10)
                Log.d("roomlist", roomList.size.toString() + " offset= " + offset)
                if (roomList.isEmpty()) {
                    list = FirebaseDatabase.readLimitedNotes(key)
                    Log.d("roomlist", "Firebase size " + list.size)
                    for (i in list.size - 1 downTo 0) {
                        addNotesFromFBtoRoom(list[i], uid)
                    }
                    roomList = roomDB.noteDao.readLimitedNotes(offset, 10)
                }
                roomNotesList = convertToNotes(roomList)
            } else if (!networkStatus) {
                roomList = roomDB.noteDao.readLimitedNotes(offset, 10)
                roomNotesList = convertToNotes(roomList)
            }
            roomNotesList
        }
    }

    private fun convertToNotes(roomList: MutableList<NoteEntity>): MutableList<NotesKey> {
        val tempList = mutableListOf<NotesKey>()
        for (i in roomList) {
            if (!i.deletedForever) {
                val note = NotesKey(
                    i.title,
                    i.note,
                    i.fid,
                    i.deleted,
                    i.archived,
                    i.modifiedTime,
                    i.remainder

                )
                tempList.add(note)
            }
        }
        return tempList
    }
}