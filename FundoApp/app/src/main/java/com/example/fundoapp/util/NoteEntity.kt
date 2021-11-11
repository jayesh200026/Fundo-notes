package com.example.fundoapp.util

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Notes")
data class NoteEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val nid: Long = 0L,

    @ColumnInfo
    val uid: String,

    @ColumnInfo(name = "fid")
    val fid: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "note")
    val note: String

)