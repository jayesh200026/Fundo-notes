package viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import service.Firebasedatabase
import util.Notes

class AddNoteViewModel:ViewModel(){
    private val _databaseNoteAddedStatus=MutableLiveData<Boolean>()
    var databaseNoteAddedStatus=_databaseNoteAddedStatus as LiveData<Boolean>

    private val _databaseNoteUpdateStatus=MutableLiveData<Boolean>()
    var databaseNoteUpdateStatus=_databaseNoteUpdateStatus as LiveData<Boolean>

    fun addNotesToDatabase(note:Notes){
        Firebasedatabase.addNote(note){
            _databaseNoteAddedStatus.value=it
        }
    }

    fun updateNoteIndatabse(note: Notes) {
        Firebasedatabase.updateNote(note){
            _databaseNoteUpdateStatus.value=it
        }

    }

}