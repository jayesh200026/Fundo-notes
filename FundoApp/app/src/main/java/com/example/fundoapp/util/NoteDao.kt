package com.example.fundoapp.util

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NoteDao {

    @Insert
    fun insertNote(note: NoteEntity): Long

    @Query("Select * from Notes where uid=:uid")
    fun readNotes(uid: String): MutableList<NoteEntity>

    @Query("Delete from Notes where uid=:uid and fid=:key")
    fun deleteNote(uid: String, key: String): Int

    @Query("Update Notes set title=:title,note=:note where fid=:key")
    fun updateNote(title: String, note: String, key: String): Int
}