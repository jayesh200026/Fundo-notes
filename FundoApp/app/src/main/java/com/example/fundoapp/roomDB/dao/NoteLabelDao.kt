package com.example.fundoapp.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fundoapp.roomdb.entity.NoteLabelEntity

@Dao
interface NoteLabelDao {

    @Insert
    fun addNoteLabelRelationship(row: NoteLabelEntity):Long

    @Query("delete from NoteLabel")
    fun clearTable()
}