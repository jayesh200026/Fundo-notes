package com.example.fundoapp.roomDB.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.fundoapp.roomDB.entity.UserEntity

@Dao
interface UserDao {

    @Insert
    fun registerUser(user: UserEntity):Long

}