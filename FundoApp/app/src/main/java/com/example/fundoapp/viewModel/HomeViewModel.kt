package com.example.fundoapp.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundoapp.ui.MainActivity
import kotlinx.coroutines.launch
import com.example.fundoapp.service.Authentication
import com.example.fundoapp.service.DBService
import service.FirebaseStorage
import service.FirebaseDatabase
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.service.model.User
import java.lang.Exception

class HomeViewModel : ViewModel() {
    private val _profilePhotoFetch = MutableLiveData<Uri>()
    val profilePhotoFetch = _profilePhotoFetch as LiveData<Uri>

    private val _profilePhotoUploadStatus = MutableLiveData<Boolean>()
    val profilePhotoUploadStatus = _profilePhotoUploadStatus as LiveData<Boolean>

    private val _databaseReadingStatus = MutableLiveData<User>()
    val databaseReadingStatus = _databaseReadingStatus as LiveData<User>

    private val _readNotesFromDatabaseStatus = MutableLiveData<MutableList<NotesKey>>()
    var readNotesFromDatabaseStatus =
        _readNotesFromDatabaseStatus as LiveData<MutableList<NotesKey>>



    fun fetchProfile() {
        try {
            FirebaseStorage.fetchPhoto(Authentication.getCurrentUid()) { status, uri ->
                _profilePhotoFetch.value = uri
            }
        } catch (e: Exception) {
        }
    }

    fun uploadProfile(uid: String?, imageUri: Uri) {
        FirebaseStorage.uploadImage(uid, imageUri) {
            _profilePhotoUploadStatus.value = it
        }
    }

    fun readUserFRomDatabase() {
        FirebaseDatabase.readUser {
            _databaseReadingStatus.value = it
        }
    }

    fun readNotesFromDatabase(context: Context) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val noteList = dbService.readNotes()
            Log.d("listsize", noteList.size.toString())
            _readNotesFromDatabaseStatus.value = noteList
        }

    }

    fun clearTables(context: Context) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            dbService.clearTables()
        }

    }


}