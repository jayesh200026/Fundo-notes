package com.example.fundoapp.util

import android.content.Context
import android.content.SharedPreferences

object SharedPref {
    private lateinit var sharedPreferences: SharedPreferences

    fun initSharedPref(context: Context) {
        sharedPreferences =
            context.getSharedPreferences("FundoSharedPreference", Context.MODE_PRIVATE)
    }

    fun addString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun addNoteSize(key: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun setUpdateStatus(key:String,value:Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean(key,value)
        editor.apply()
    }

    fun updateNotePosition(key: String, value: Int){
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getUpdateNotePosition(key: String):Int= sharedPreferences.getInt(key,0)

    fun getUpdateStatus(key: String):Boolean= sharedPreferences.getBoolean(key,false)

    fun getNoteSize(key: String): Int = sharedPreferences.getInt(key, 0)

    fun get(key: String): String? = sharedPreferences.getString(key, "")

    fun clearAll() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun addBoolean(key: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun addRemainder(key : String, value : Long)
    {
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getRemainder(key : String):Long = sharedPreferences.getLong(key ,0L)

    fun getBoolean(key:String):Boolean = sharedPreferences.getBoolean(key,false)



}