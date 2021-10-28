package viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import service.Authentication
import util.AuthStatus
import util.SharedPref

class LoginViewModel:ViewModel(){
    private val _loginStatus = MutableLiveData<AuthStatus>()
    val loginStatus = _loginStatus as LiveData<AuthStatus>

    fun loginWithEmailNPassword(email: String, password: String) {
        Authentication.signIn(email, password) {
            _loginStatus.value = it
            if(it.status){
                SharedPref.addString("email",email)
            }
        }
    }
}