package com.example.fundoapp.util

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserDetails")
data class UserEntity (
    @PrimaryKey
    @ColumnInfo(name = "uid")
    var uid:String,

    @ColumnInfo(name = "name")
    var name:String,

    @ColumnInfo(name = "age")
    var age:String,

    @ColumnInfo(name = "email")
    var email:String

        )