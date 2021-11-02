package com.example.fundoapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import viewmodels.SharedViewModel
import viewmodels.SharedViewModelFactory
import service.Authentication
import util.SharedPref

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var firebaseAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    lateinit var splashFragment: SplashFragment
    lateinit var profileIcon:ImageView
    lateinit var navMenu:NavigationView
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var drawerLayout:DrawerLayout
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        profileIcon=findViewById(R.id.userProfile)
        toolbar=findViewById(R.id.myToolbar)
        drawerLayout=findViewById(R.id.drawerLayout)
        navMenu=findViewById(R.id.myNavMenu)
        setSupportActionBar(toolbar)

        val toggle=ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close)
        toggle.isDrawerIndicatorEnabled=true
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navMenu.setNavigationItemSelectedListener(this)



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
        sharedViewModel.gotoAddNotePageStatus.observe(this@MainActivity,
            {
                if(it){
                    gotoAddNotesPage()
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

    private fun gotoAddNotesPage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, AddNotesFragment())
            addToBackStack(null)
            commit()
        }
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
            addToBackStack(null)
            commit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        when(item.itemId){
            R.id.menuAddNotes->{
                Toast.makeText(applicationContext,"Clicked add notes menu",Toast.LENGTH_LONG).show()
            }
            R.id.menuReminder->{ Toast.makeText(applicationContext,"Clicked reminder menu",Toast.LENGTH_LONG).show()
            }
            R.id.menuSettings->{
                Toast.makeText(applicationContext,"Clicked settings menu",Toast.LENGTH_LONG).show()
            }
            R.id.menuNotes->{
                Toast.makeText(applicationContext,"Clicked on notes",Toast.LENGTH_LONG).show()
            }
        }
        return true
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}