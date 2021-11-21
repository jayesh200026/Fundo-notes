package com.example.fundoapp.roomdb.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "NoteLabel", foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["fid"],
            childColumns = ["noteID"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = LabelEntity::class,
            parentColumns = ["labelId"],
            childColumns = ["labelId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
    primaryKeys = ["noteID", "labelId"]
)
class NoteLabelEntity (
    @ColumnInfo
    var noteID:String,

    @ColumnInfo
    var labelId:String
        )





