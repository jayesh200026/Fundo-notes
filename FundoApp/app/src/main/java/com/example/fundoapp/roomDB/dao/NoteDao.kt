package com.example.fundoapp.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fundoapp.roomdb.entity.NoteEntity

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

     @Query("Update Notes set Permanently_Deleted=:status,modifiedTime=:time where fid=:key")
    fun deleteForever(key: String,status:Boolean,time:String): Int

    @Query("Delete from notes where fid=:key")
    fun deletePermanently(key: String)

    @Query("Delete from Notes")
    fun clearTable()

    @Query("Update Notes set archived=:value,modifiedTime=:time where fid=:key")
     fun updateArchive(key: String, value: Boolean,time:String):Int

     @Query("Update Notes set Remainder=:value,modifiedTime=:time where fid=:key")
     fun updateRemainder(key : String, value :Long,time:String):Int

     @Update
     fun update(noteEntity : NoteEntity): Int

     @Query("Update notes set Remainder=:l,modifiedTime=:time where fid=:key")
     fun removeRemainder(key: String, l: Long,time:String): Int
}