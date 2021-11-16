package com.example.fundoapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundoapp.service.Authentication
import com.example.fundoapp.util.AuthStatus

class ResetPasswordViewModel:ViewModel() {
    private val _resetPasswordStatus = MutableLiveData<AuthStatus>()
    val resetPasswordStatus = _resetPasswordStatus as LiveData<AuthStatus>

    fun resetPassword(emailValue: String) {
        Authentication.resetPassword(emailValue) {
            _resetPasswordStatus.value = it
        }
    }

}