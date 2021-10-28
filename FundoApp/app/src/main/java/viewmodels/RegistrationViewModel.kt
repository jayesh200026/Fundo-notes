package viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import service.Authentication
import util.AuthStatus

class RegistrationViewModel:ViewModel(){

    private val _registrationStatus = MutableLiveData<AuthStatus>()
    val registrationStatus = _registrationStatus as LiveData<AuthStatus>

    fun registerUser(email: String, password: String) {
        Authentication.registerUser(email, password) {
            _registrationStatus.value = it
        }

    }
}