package com.example.fundoapp.service.model

class Notes(val title: String, val note: String, val deleted: Boolean = false, val archived: Boolean = false,val mTime: String,val remainder:Long=0L)