package com.example.fundoapp.service

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fundoapp.roomdb.Dao.NoteDao
import com.example.fundoapp.roomdb.Entity.NoteEntity
import com.example.fundoapp.roomdb.Dao.UserDao
import com.example.fundoapp.roomdb.Entity.UserEntity

@Database(entities = [NoteEntity::class, UserEntity::class],version = 5)
abstract class RoomDatabase:RoomDatabase() {
    abstract val noteDao: NoteDao
    abstract val userDao: UserDao
}