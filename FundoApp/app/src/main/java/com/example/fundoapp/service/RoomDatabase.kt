package com.example.fundoapp.service

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fundoapp.roomdb.entity.NoteEntity

import com.example.fundoapp.roomdb.entity.UserEntity
import com.example.fundoapp.roomdb.dao.LabelDao
import com.example.fundoapp.roomdb.dao.NoteDao
import com.example.fundoapp.roomdb.dao.NoteLabelDao
import com.example.fundoapp.roomdb.dao.UserDao
import com.example.fundoapp.roomdb.entity.LabelEntity
import com.example.fundoapp.roomdb.entity.NoteLabelEntity

@Database(
    entities = [NoteEntity::class, UserEntity::class, LabelEntity::class, NoteLabelEntity::class],
    version = 11
)
abstract class RoomDatabase : RoomDatabase() {
    abstract val noteDao: NoteDao
    abstract val userDao: UserDao
    abstract val labelDao: LabelDao
    abstract val noteLabelDao:NoteLabelDao
}