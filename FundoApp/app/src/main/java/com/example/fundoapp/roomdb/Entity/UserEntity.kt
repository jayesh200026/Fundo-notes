package com.example.fundoapp.roomdb.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserDetails")
data class UserEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "SrNo")
    var SrNo: Long = 0L,

    @ColumnInfo(name = "uid")
    var uid: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "age")
    var age: String,

    @ColumnInfo(name = "email")
    var email: String

)