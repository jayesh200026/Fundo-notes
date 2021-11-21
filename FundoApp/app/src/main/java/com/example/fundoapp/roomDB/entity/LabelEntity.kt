package com.example.fundoapp.roomdb.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Label")
data class LabelEntity (
    @PrimaryKey()
    @ColumnInfo
    var labelId:String,

    @ColumnInfo(name="label")
    var label:String
        )
