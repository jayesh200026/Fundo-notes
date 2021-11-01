package util

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

    fun getNoteSize(key: String): Int = sharedPreferences.getInt(key, 0)

    fun get(key: String): String? = sharedPreferences.getString(key, "")

    fun clearAll() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

}