package viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import service.Authentication
import util.AuthStatus

class ResetPasswordViewModel:ViewModel() {
    private val _resetPasswordStatus = MutableLiveData<AuthStatus>()
    val resetPasswordStatus = _resetPasswordStatus as LiveData<AuthStatus>

    fun resetPassword(emailValue: String) {
        Authentication.resetPassword(emailValue) {
            _resetPasswordStatus.value = it
        }
    }

}