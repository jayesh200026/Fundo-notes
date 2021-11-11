package com.example.fundoapp.service

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fundoapp.util.NoteDao
import com.example.fundoapp.util.NoteEntity

@Database(entities = [NoteEntity::class],version = 3)
abstract class RoomDatabase:RoomDatabase() {
    abstract val noteDao: NoteDao
}