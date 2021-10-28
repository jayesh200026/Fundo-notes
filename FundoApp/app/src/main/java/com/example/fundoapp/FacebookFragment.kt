package com.example.fundoapp


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.facebook.AccessToken
import com.facebook.FacebookException

import com.facebook.login.LoginResult

import com.facebook.FacebookCallback

import com.facebook.login.LoginManager

import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import service.Authentication
import service.Firebasedatabase
import util.User
import viewmodels.FacebookViewModel
import viewmodels.FacebookViewModelFactory
import viewmodels.SharedViewModel
import viewmodels.SharedViewModelFactory
import java.net.URL


class FacebookFragment : Fragment() {

    lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth
    lateinit var sharedViewModel: SharedViewModel
    lateinit var facebookViewModel: FacebookViewModel
    lateinit var progessBar: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]

        facebookViewModel=ViewModelProvider(this,FacebookViewModelFactory())[FacebookViewModel::class.java]
        var sharePref = requireActivity().getSharedPreferences("Mypref", Context.MODE_PRIVATE)
        var editor = sharePref.edit()

        callbackManager = facebookViewModel.getCallBackManager()
        auth = facebookViewModel.getAuth()
        progessBar = view.findViewById(R.id.progressBar)
        facebookViewModel.loginWithReadPermision(this@FacebookFragment)

        facebookViewModel.registerCallBack(callbackManager)

        facebookViewModel.callbackStatus.observe(viewLifecycleOwner) {
            if (!it.status) {
                sharedViewModel.setGoToLoginPageStatus(true)
            } else {
                handleFacebookAccessToken(it.loginResult!!.accessToken)
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        facebookViewModel.handleToken(token, auth)

        facebookViewModel.facebookLoginStatus.observe(viewLifecycleOwner) {
            if (it.status) {
                updateUI(it.user)
            } else {
                Toast.makeText(
                    requireContext(), "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
                sharedViewModel.setGoToLoginPageStatus(true)
            }
        }


    }

    private fun updateUI(user: FirebaseUser?) {

        progessBar.visibility = View.VISIBLE
        if (user != null) {
            var name: String? = null
            var email: String? = null
            var uid:String?=null


            for (profile: UserInfo in user.providerData) {
                name = profile.displayName
                email = profile.email
                uid=profile.uid

            }
            val userObj = User(fullName = name, email = email)
            sharedViewModel.addUserToDatabase(userObj)

            //sharedViewModel.uploadProfile(uid,bitmap)

            sharedViewModel.databaseRegistrationStatus.observe(viewLifecycleOwner) {
                if (it) {
                    progessBar.visibility = View.INVISIBLE
                    sharedViewModel.setGotoHomePageStatus(true)
                } else {
                    progessBar.visibility == View.INVISIBLE
                    Toast.makeText(
                        requireContext(), "Database registration failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    sharedViewModel.setGoToLoginPageStatus(true)
                }
            }

//            sharedViewModel.profilePhotoUploadStatus.observe(viewLifecycleOwner){
//                if(it){
//                    Toast.makeText(
//                        requireContext(), "Profile pic uploaded",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    sharedViewModel.setGotoHomePageStatus(true)
//                }
//                else{
//                    Toast.makeText(
//                        requireContext(), "Profile pic uploading failed",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    sharedViewModel.setGoToLoginPageStatus(true)
//                }
//            }

        }
    }


}