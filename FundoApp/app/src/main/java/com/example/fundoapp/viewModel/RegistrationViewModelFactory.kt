package com.example.fundoapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RegistrationViewModelFactory:ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return RegistrationViewModel() as T
    }

}