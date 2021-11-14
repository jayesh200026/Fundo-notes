package com.example.fundoapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.fundoapp.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.example.fundoapp.viewModel.SharedViewModel
import com.example.fundoapp.viewModel.SharedViewModelFactory
import com.example.fundoapp.service.Authentication
import com.example.fundoapp.service.RoomDatabase
import com.example.fundoapp.util.SharedPref

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        lateinit var roomDBClass: RoomDatabase
    }

    var firebaseAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    lateinit var splashFragment: SplashFragment
    lateinit var profileIcon: ImageView
    lateinit var navMenu: NavigationView
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        profileIcon = findViewById(R.id.userProfile)
        toolbar = findViewById(R.id.myToolbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navMenu = findViewById(R.id.myNavMenu)
        roomDBClass = Room.databaseBuilder(applicationContext, RoomDatabase::class.java, "myDB")
            .fallbackToDestructiveMigration().allowMainThreadQueries().build()
        setSupportActionBar(toolbar)

        val toggle =
            ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
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
                if (it) {
                    gotoAddNotesPage()
                }
            })
        sharedViewModel.gotoDeletedNotePageStatus.observe(this@MainActivity,
            {
                if(it){
                    gotoDeletedNotePage()
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



    private fun gotoDeletedNotePage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, DeletedNoteFragment())
            commit()
        }

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
            replace(R.id.flFragment, HomeFragment())
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
        when (item.itemId) {
            R.id.menuAddNotes -> {

            }
            R.id.menuReminder -> {
                Toast.makeText(applicationContext, "Clicked reminder menu", Toast.LENGTH_LONG)
                    .show()
            }
            R.id.menuSettings -> {
                Toast.makeText(applicationContext, "Clicked settings menu", Toast.LENGTH_LONG)
                    .show()
            }
            R.id.menuNotes -> {
                sharedViewModel.setGotoHomePageStatus(true)
            }
            R.id.menuDeleted->{
                sharedViewModel.setGoToDeletedNotePageStatus(true)
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
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