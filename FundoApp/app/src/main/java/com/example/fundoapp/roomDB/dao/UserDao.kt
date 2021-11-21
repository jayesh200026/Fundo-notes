package com.example.fundoapp.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.fundoapp.roomdb.entity.UserEntity

@Dao
interface UserDao {

    @Insert
    fun registerUser(user: UserEntity):Long

}