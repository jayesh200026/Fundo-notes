package com.example.fundoapp.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundoapp.service.DBService
import com.example.fundoapp.ui.MainActivity
import com.example.fundoapp.service.model.NotesKey
import kotlinx.coroutines.launch

class DeleteNoteViewModel:ViewModel() {

    private val _readNotesFromDatabaseStatus = MutableLiveData<MutableList<NotesKey>>()
    var readNotesFromDatabaseStatus=_readNotesFromDatabaseStatus as LiveData<MutableList<NotesKey>>

    private val _restoreNoteStatus=MutableLiveData<Boolean>()
    var restoreNoteStatus=_restoreNoteStatus as LiveData<Boolean>

    private val _deleteForeverNoteStatus=MutableLiveData<Boolean>()
    var deleteForeverNoteStatus=_deleteForeverNoteStatus as LiveData<Boolean>

    fun readNotesFromDatabase(context: Context){
        viewModelScope.launch {
            val dbService= DBService(MainActivity.roomDBClass,context)
            val noteList=dbService.readNotes()
            Log.d("listsize",noteList.size.toString())
            _readNotesFromDatabaseStatus.value=noteList
        }

    }

    fun restoreNote(context: Context, title: String, note: String, key: String) {
        viewModelScope.launch {
            val dbService= DBService(MainActivity.roomDBClass,context)
            val status=dbService.restoreNote(title,note,key)
            _restoreNoteStatus.value=status
        }

    }

    fun deleteForever(context: Context, key: String) {
        viewModelScope.launch {
            val dbService= DBService(MainActivity.roomDBClass,context)
            val status=dbService.deleteForever(key)
            _deleteForeverNoteStatus.value=status
        }

    }
}