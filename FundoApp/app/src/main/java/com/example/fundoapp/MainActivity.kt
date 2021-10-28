package com.example.fundoapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import viewmodels.SharedViewModel
import viewmodels.SharedViewModelFactory
import service.Authentication
import util.SharedPref

class MainActivity : AppCompatActivity() {
    var firebaseAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    lateinit var splashFragment: SplashFragment
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setSupportActionBar(findViewById(R.id.myToolbar))

        SharedPref.initSharedPref(this)
        sharedViewModel = ViewModelProvider(
            this@MainActivity,
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        observeNavigation()
        val loggedIn = checkIfLoggedIn()
        if (loggedIn) {
            gotoHomePage()
        } else {
            gotoSplashScreen()
        }
        //gotoLoginPage()


    }



    private fun checkIfLoggedIn(): Boolean {
        if (Authentication.getCurrentUser() != null) {
            return true
        }
        return false
    }

    private fun observeNavigation() {
        sharedViewModel.gotoHomePageStatus.observe(this@MainActivity, {
            if (it) {
                gotoHomePage()
            }
        })

        sharedViewModel.gotoLoginPageStatus.observe(this@MainActivity, {
            if (it) {
                gotoLoginPage()
            }
        })

        sharedViewModel.gotoRegistrationPageStatus.observe(this@MainActivity,
            {
                if (it) {
                    gotoRegistrationPage()
                }
            })
        sharedViewModel.gotoFacebookLoginPageStatus.observe(this@MainActivity,
            {
                if (it) {
                    gotoFacebookFragment()
                }
            })
        sharedViewModel.gotoResetPasswordPageStatus.observe(this@MainActivity,
            {
                if (it) {
                    gotoResetPasswordFragment()
                }
            })
//        sharedViewModel.loginStatus.observe(this@MainActivity, {
//            if (it.status) {
//                Toast.makeText(
//                    this,
//                    it.message,
//                    Toast.LENGTH_LONG
//                ).show()
//
//                sharedViewModel.setGotoHomePageStatus(true)
//            } else {
//                Toast.makeText(
//                    this,
//                    it.message,
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//        )

    }


    private fun gotoSplashScreen() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, SplashFragment())
            commit()
        }
    }

    private fun gotoLoginPage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, LoginFragment())
            commit()
        }

    }

    private fun gotoRegistrationPage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, RegistrationFragment())
            commit()
        }
    }

    private fun gotoHomePage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, ProfileFragment())
            commit()
        }

    }

    private fun gotoFacebookFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, FacebookFragment())
            commit()
        }
    }

    private fun gotoResetPasswordFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, ResetPasswordFragment())
            commit()
        }
    }


}