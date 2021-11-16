package com.example.fundoapp.service

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fundoapp.roomDB.dao.NoteDao
import com.example.fundoapp.roomDB.entity.NoteEntity
import com.example.fundoapp.roomDB.dao.UserDao
import com.example.fundoapp.roomDB.entity.UserEntity

@Database(entities = [NoteEntity::class, UserEntity::class],version = 5)
abstract class RoomDatabase:RoomDatabase() {
    abstract val noteDao: NoteDao
    abstract val userDao: UserDao
}