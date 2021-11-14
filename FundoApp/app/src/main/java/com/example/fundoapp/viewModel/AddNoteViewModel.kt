package com.example.fundoapp.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundoapp.ui.MainActivity
import kotlinx.coroutines.launch
import com.example.fundoapp.service.DBService

class AddNoteViewModel : ViewModel() {
    private val _databaseNoteAddedStatus = MutableLiveData<Boolean>()
    var databaseNoteAddedStatus = _databaseNoteAddedStatus as LiveData<Boolean>

    private val _databaseNoteUpdateStatus = MutableLiveData<Boolean>()
    var databaseNoteUpdateStatus = _databaseNoteUpdateStatus as LiveData<Boolean>

    private val _databaseNoteDeletionStatus = MutableLiveData<Boolean>()
    var databaseNoteDeletionStatus = _databaseNoteDeletionStatus as LiveData<Boolean>

    fun addNote(uid: String, title: String, note: String, context: Context) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val status = dbService.addNote(uid, title, note)
            _databaseNoteAddedStatus.value = status
        }
    }


    fun updateNote(key: String, title: String, note: String, context: Context) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val status = dbService.updateNote(key, title, note, context)
            _databaseNoteUpdateStatus.value = status
        }
    }

    fun deleteNote(title: String, note: String, key: String, context: Context) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val status = dbService.deleteNote(title, note, key)
            _databaseNoteDeletionStatus.value = status
        }
    }

}