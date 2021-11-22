package com.example.fundoapp.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundoapp.service.DBService
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.ui.MainActivity
import kotlinx.coroutines.launch

class RemainderViewModel : ViewModel() {

    private val _readNotesFromDatabaseStatus = MutableLiveData<MutableList<NotesKey>>()
    var readNotesFromDatabaseStatus =
        _readNotesFromDatabaseStatus as LiveData<MutableList<NotesKey>>

    private val _removeRemainderStatus=MutableLiveData<Boolean>()
    var removeRemainderStatus=_removeRemainderStatus as LiveData<Boolean>

    fun readNotesFromDatabase(context: Context) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val noteList = dbService.readNotes()
            Log.d("listsize", noteList.size.toString())
            _readNotesFromDatabaseStatus.value = noteList
        }

    }

    fun removeRemainder(context: Context, userNote: NotesKey) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val status = dbService.removeRemainder(userNote)
            _removeRemainderStatus.value = status
        }

    }


}