package com.example.fundoapp.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundoapp.ui.FacebookFragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth
import com.example.fundoapp.service.Authentication
import com.example.fundoapp.service.DBService
import com.example.fundoapp.ui.MainActivity
import com.example.fundoapp.util.AuthStatus
import com.example.fundoapp.util.FirebaseUserToken
import com.example.fundoapp.util.SharedPref
import kotlinx.coroutines.launch
import util.UserLoginResult

class LoginViewModel:ViewModel(){
    private val _loginStatus = MutableLiveData<AuthStatus>()
    val loginStatus = _loginStatus as LiveData<AuthStatus>

    private val _callbackStatus = MutableLiveData<UserLoginResult>()
    val callbackStatus = _callbackStatus as LiveData<UserLoginResult>

    private val _facebookLoginStatus = MutableLiveData<FirebaseUserToken>()
    val facebookLoginStatus = _facebookLoginStatus as LiveData<FirebaseUserToken>

    fun getCallBackManager(): CallbackManager {
        return Authentication.getCallBackManager()
    }

    fun getAuth(): FirebaseAuth {
        return Authentication.getAuth()
    }

    fun loginWithReadPermision(facebookFragment: FacebookFragment) {
        Authentication.loginWithReadPermision(facebookFragment)
    }

    fun registerCallBack(callbackManager: CallbackManager) {
        Authentication.registerCallBack(callbackManager) {
            _callbackStatus.value = it
        }
    }

    fun handleToken(token: AccessToken, auth: FirebaseAuth) {
        Authentication.handletoken(token, auth) {
            _facebookLoginStatus.value = it
        }
    }

    fun loginWithEmailNPassword(email: String, password: String) {
        Authentication.signIn(email, password) {
            _loginStatus.value = it
            if(it.status){
                SharedPref.addString("email",email)
            }
        }
    }

    fun fillRoomdB(context: Context) {
        viewModelScope.launch {
            val dbService=DBService(MainActivity.roomDBClass,context)
            dbService.fillToRoomDB()

        }

    }
}