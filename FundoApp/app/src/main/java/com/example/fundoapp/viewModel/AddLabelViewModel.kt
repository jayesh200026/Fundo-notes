package com.example.fundoapp.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundoapp.roomdb.entity.LabelEntity
import com.example.fundoapp.roomdb.entity.NoteLabelEntity
import com.example.fundoapp.service.DBService
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.ui.MainActivity
import kotlinx.coroutines.launch

class AddLabelViewModel : ViewModel() {
    private val _readLabelsFromDatabaseStatus = MutableLiveData<MutableList<LabelEntity>>()
    var readLabelsFromDatabaseStatus =
        _readLabelsFromDatabaseStatus as LiveData<MutableList<LabelEntity>>

    private val _readNotesLabelsFromDatabaseStatus = MutableLiveData<MutableList<NoteLabelEntity>>()
    var readNotesLabelsFromDatabaseStatus =
        _readNotesLabelsFromDatabaseStatus as LiveData<MutableList<NoteLabelEntity>>

    private val _addLabelsToNotesStatus = MutableLiveData<Boolean>()
    var addLabelsToNotesStatus = _addLabelsToNotesStatus as LiveData<Boolean>

    fun readLabels(context: Context) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val list = dbService.readLabels()
            _readLabelsFromDatabaseStatus.value = list as MutableList<LabelEntity>
        }
    }

    fun addLables(list: MutableList<LabelEntity>, context: Context) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val status = dbService.addLabelsToNotes(list)
            _addLabelsToNotesStatus.value = status
        }

    }

    fun readNoteLabel(context: Context) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val status=dbService.readNotesLabel()
            _readNotesLabelsFromDatabaseStatus.value=status
        }

    }
}