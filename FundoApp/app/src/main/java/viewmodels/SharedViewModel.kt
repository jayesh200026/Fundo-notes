package viewmodels

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundoapp.FacebookFragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth
import service.Authentication
import service.FirebaseStorage
import service.Firebasedatabase
import util.AuthStatus
import util.FirebaseUserToken
import util.User
import util.UserLoginResult
import java.lang.Exception

class SharedViewModel : ViewModel() {
    private val _gotoHomePageStatus = MutableLiveData<Boolean>()
    val gotoHomePageStatus = _gotoHomePageStatus as LiveData<Boolean>

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


    private val _gotoAddNotePageStatus=MutableLiveData<Boolean>()
    val gotoAddNotePageStatus=_gotoAddNotePageStatus as LiveData<Boolean>





    fun setGotoHomePageStatus(status: Boolean) {
        Log.d("loginStatus", "Home page involked")
        _gotoHomePageStatus.value = status
    }

    fun setGoToLoginPageStatus(status: Boolean) {
        Log.d("loginStatus", "login page involked")
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

    fun addUserToDatabase(user: User) {
        Firebasedatabase.addUser(user) {
            _databaseRegistrationStatus.value = it
        }
    }

    fun getAuth(): FirebaseAuth {
        return Authentication.getAuth()
    }

    fun logout() {
        Authentication.logOut()
    }


    fun getCurrentUid():String{
        return Authentication.getCurrentUid()
    }

    fun setGotoAddNotesPage(status: Boolean){
        _gotoAddNotePageStatus.value=status
    }

}