package com.example.fundoapp.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundoapp.roomdb.entity.LabelEntity
import com.example.fundoapp.service.DBService
import com.example.fundoapp.service.model.Label
import com.example.fundoapp.ui.MainActivity
import kotlinx.coroutines.launch

class LabelViewModel : ViewModel() {

    private val _createLabelStatus = MutableLiveData<Label>()
    var createLabelStatus = _createLabelStatus as LiveData<Label>

    private val _deleteLabelStatus = MutableLiveData<Label>()
    var deleteLabelStatus = _deleteLabelStatus as LiveData<Label>

    private val _updateLabelStatus = MutableLiveData<Label>()
    var updateLabelStatus = _updateLabelStatus as LiveData<Label>

    private val _readLabelsFromDatabaseStatus = MutableLiveData<MutableList<Label>>()
    var readLabelsFromDatabaseStatus =
        _readLabelsFromDatabaseStatus as LiveData<MutableList<Label>>

    fun createLabel(label: String, context: Context) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val status = dbService.createLabel(label)
            _createLabelStatus.value = status
        }

    }

    fun readLabels(context: Context) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val list = dbService.readLabels()
            _readLabelsFromDatabaseStatus.value = list as MutableList<Label>
        }
    }

    fun deleteLabel(labelEntity: Label,context: Context) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val status=dbService.deleteLabel(labelEntity)
            if(status){
                _deleteLabelStatus.value=labelEntity
            }

        }


    }

    fun updateLabel(labelEntity: Label, context: Context,newLabel:String) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val status=dbService.updateLabel(labelEntity,newLabel)
            if(status){
                _updateLabelStatus.value= Label(labelId = labelEntity.labelId,labelName = newLabel)
            }

        }

    }

}