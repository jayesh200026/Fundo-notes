package com.example.fundoapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.fundoapp.R
import com.example.fundoapp.service.Authentication
import com.example.fundoapp.viewModel.SharedViewModel
import com.example.fundoapp.viewModel.SharedViewModelFactory

class SplashFragment : Fragment() {

    lateinit var sharedViewModel: SharedViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        Handler().postDelayed(Runnable { /* Create an Intent that will start the Menu-Activity. */
            if(Authentication.getCurrentUser() == null) {
                sharedViewModel.setGoToLoginPageStatus(true)
            }else{
                sharedViewModel.setGotoHomePageStatus(true)
            }
        }, 1500)
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


}