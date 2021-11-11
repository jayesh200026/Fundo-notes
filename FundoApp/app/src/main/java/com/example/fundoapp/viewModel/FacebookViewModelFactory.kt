package com.example.fundoapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FacebookViewModelFactory:ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FacebookViewModel() as T
    }

}