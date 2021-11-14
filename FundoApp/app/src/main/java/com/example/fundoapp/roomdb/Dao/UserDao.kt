package com.example.fundoapp.roomdb.Dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.fundoapp.roomdb.Entity.UserEntity

@Dao
interface UserDao {

    @Insert
    fun registerUser(user: UserEntity):Long

}