package com.example.fundoapp.service.model

import java.io.Serializable

class NotesKey(
    val title: String,
    val note: String,
    val key: String,
    val deleted: Boolean = false,
    val archived: Boolean = false,
    val mTime: String,
    val remainder: Long = 0L
) :Serializable{
}