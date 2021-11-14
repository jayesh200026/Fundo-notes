package com.example.fundoapp.roomdb.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fundoapp.roomdb.Entity.NoteEntity

@Dao
interface NoteDao {

    @Insert
    fun insertNote(note: NoteEntity): Long

    @Query("Select * from Notes where uid=:uid")
    fun readNotes(uid: String): MutableList<NoteEntity>

    @Query("Update Notes set deleted=:status,modifiedTime=:time where fid=:key")
    fun deleteNote( key: String,status:Boolean,time:String): Int

    @Query("Update Notes set title=:title,note=:note,modifiedTime=:time where fid=:key")
    fun updateNote(title: String, note: String, key: String,time:String): Int

    @Query("Update Notes set deleted=:status,modifiedTime=:time where fid=:key")
     fun restoreNote(key: String,status:Boolean,time:String):Int

     @Query("Delete from Notes where fid=:key")
    fun deleteForever(key: String): Int
}