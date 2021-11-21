package com.example.fundoapp.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundoapp.roomdb.entity.LabelEntity
import com.google.firebase.auth.FirebaseAuth
import com.example.fundoapp.service.Authentication
import com.example.fundoapp.service.DBService
import com.example.fundoapp.ui.MainActivity
import kotlinx.coroutines.launch
import com.example.fundoapp.service.model.User

class SharedViewModel : ViewModel() {

    private val _gotoLabelPageStatus = MutableLiveData<Boolean>()
    val gotoLabelPageStatus = _gotoLabelPageStatus as LiveData<Boolean>

    private val _gotoRemainderPageStatus = MutableLiveData<Boolean>()
    val gotoRemainderPageStatus = _gotoRemainderPageStatus as LiveData<Boolean>

    private val _gotoHomePageStatus = MutableLiveData<Boolean>()
    val gotoHomePageStatus = _gotoHomePageStatus as LiveData<Boolean>

    private val _gotoArchivedNotesPageStatus = MutableLiveData<Boolean>()
    val gotoArchivedNotesPageStatus = _gotoArchivedNotesPageStatus as LiveData<Boolean>

    private val _gotoLoginPageStatus = MutableLiveData<Boolean>()
    val gotoLoginPageStatus = _gotoLoginPageStatus as LiveData<Boolean>

    private val _gotoRegistrationPageStatus = MutableLiveData<Boolean>()
    val gotoRegistrationPageStatus = _gotoRegistrationPageStatus as LiveData<Boolean>

    private val _gotoResetPasswordPageStatus = MutableLiveData<Boolean>()
    val gotoResetPasswordPageStatus = _gotoResetPasswordPageStatus as LiveData<Boolean>

    private val _gotoFacebookLoginPageStatus = MutableLiveData<Boolean>()
    val gotoFacebookLoginPageStatus = _gotoFacebookLoginPageStatus as LiveData<Boolean>

    private val _databaseRegistrationStatus = MutableLiveData<Boolean>()
    val databaseRegistrationStatus = _databaseRegistrationStatus as LiveData<Boolean>


    private val _gotoAddNotePageStatus = MutableLiveData<Boolean>()
    val gotoAddNotePageStatus = _gotoAddNotePageStatus as LiveData<Boolean>

    private val _gotoDeletedNotePageStatus = MutableLiveData<Boolean>()
    val gotoDeletedNotePageStatus = _gotoDeletedNotePageStatus as LiveData<Boolean>

    private val _gotoAddLablesPageStatus = MutableLiveData<Boolean>()
    val gotoAddLablesPageStatus = _gotoAddLablesPageStatus as LiveData<Boolean>

    private val _readLabelsFromDatabaseStatus = MutableLiveData<MutableList<LabelEntity>>()
    var readLabelsFromDatabaseStatus =
        _readLabelsFromDatabaseStatus as LiveData<MutableList<LabelEntity>>

    fun setGotoLabelPageStatus(status: Boolean) {
        _gotoLabelPageStatus.value = status
    }

    fun setGotoHomePageStatus(status: Boolean) {
        _gotoHomePageStatus.value = status
    }

    fun setGoToLoginPageStatus(status: Boolean) {
        _gotoLoginPageStatus.value = status
    }

    fun setGoToRegisterPageStatus(status: Boolean) {
        _gotoRegistrationPageStatus.value = status
    }

    fun setGoToResetPasswordPageStatus(status: Boolean) {
        _gotoResetPasswordPageStatus.value = status
    }

    fun setGoToFacebookLoginPageStatus(status: Boolean) {
        _gotoFacebookLoginPageStatus.value = status
    }

    fun setGoToDeletedNotePageStatus(status: Boolean) {
        _gotoDeletedNotePageStatus.value = status
    }

    fun addUserToDatabase(user: User, context: Context) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val status = dbService.registerUser(user.fullName!!, user.age!!, user.email!!)
            _databaseRegistrationStatus.value = status
        }
    }


    fun getAuth(): FirebaseAuth {
        return Authentication.getAuth()
    }

    fun logout() {
        Authentication.logOut()
    }


    fun getCurrentUid(): String? {
        return Authentication.getCurrentUid()
    }

    fun setGotoAddNotesPage(status: Boolean) {
        _gotoAddNotePageStatus.value = status
    }

    fun setGotoAddLabelsPagesStatus(status: Boolean) {
        _gotoAddLablesPageStatus.value = status
    }


    fun readLabels(context: Context) {
        viewModelScope.launch {
            val dbService = DBService(MainActivity.roomDBClass, context)
            val list = dbService.readLabels()
            _readLabelsFromDatabaseStatus.value = list as MutableList<LabelEntity>
        }
    }

    fun setGotoArchivedNotesPageStatus(status: Boolean) {
        _gotoArchivedNotesPageStatus.value = status
    }

    fun setGotoRemainderPageStatus(status : Boolean){
        _gotoRemainderPageStatus.value = status
    }

}