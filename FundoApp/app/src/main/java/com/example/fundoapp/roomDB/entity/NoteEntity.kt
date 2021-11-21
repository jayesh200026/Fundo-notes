package com.example.fundoapp.roomdb.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Notes")
data class NoteEntity(

    @ColumnInfo
    val uid: String,

    @PrimaryKey
    @ColumnInfo(name = "fid")
    val fid: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "note")
    val note: String,

    @ColumnInfo(name="deleted")
    val deleted:Boolean=false,

    @ColumnInfo(name="archived")
    val archived:Boolean=false,

    @ColumnInfo(name="modifiedTime")
    val modifiedTime:String,

    @ColumnInfo(name="Permanently_Deleted")
    val deletedForever:Boolean=false,

    @ColumnInfo(name="Remainder")
    val remainder:Long=0L




)