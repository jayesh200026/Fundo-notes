package com.example.fundoapp.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.work.*
import com.example.fundoapp.*
import com.example.fundoapp.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.example.fundoapp.viewModel.SharedViewModel
import com.example.fundoapp.viewModel.SharedViewModelFactory
import com.example.fundoapp.service.Authentication
import com.example.fundoapp.service.RoomDatabase
import com.example.fundoapp.util.SharedPref
import com.example.fundoapp.service.SyncWorker


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
//        val loggedIn = checkIfLoggedIn()
//        if (loggedIn) {
//            gotoHomePage()
//        } else {
//            gotoSplashScreen()
//        }
        if(savedInstanceState == null){
            gotoSplashScreen()
        }
        worker()

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
                if (it) {
                    gotoDeletedNotePage()
                }
            })
        sharedViewModel.gotoLabelPageStatus.observe(this@MainActivity,
            {
                if (it) {
                    Log.d("label","going to label page")
                    gotoLabelPage()
                }
            })

        sharedViewModel.gotoAddLablesPageStatus.observe(this@MainActivity,
            {
                if(it){
                    gotoAddLabelPage()
                }
            })
        sharedViewModel.gotoArchivedNotesPageStatus.observe(this@MainActivity,
            {
                if(it){
                    gotoArchivedNotesPage()
                }
            })

        sharedViewModel.gotoRemainderPageStatus.observe(this@MainActivity,
            {
                if(it){
                    gotoRemainderPage()
                }
            })


    }

    private fun gotoRemainderPage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, RemainderFragment())
            commit()
        }
    }

    private fun gotoArchivedNotesPage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, ArchivedNotesFragment())
            commit()
        }
    }

    private fun gotoAddLabelPage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, AddLabelFragment())
            commit()
        }
    }

    private fun gotoLabelPage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, CreateLabelFragment())
            commit()
        }
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
            //addToBackStack(null)
            commit()
        }

    }

    private fun gotoRegistrationPage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, RegistrationFragment())
            addToBackStack(null)
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
                Log.d("label","clicked create label")
                    sharedViewModel.setGotoLabelPageStatus(true)
            }
            R.id.menuReminder -> {
                sharedViewModel.setGotoRemainderPageStatus(true)
            }
            R.id.menuSettings -> {
                Toast.makeText(applicationContext, "Clicked settings menu", Toast.LENGTH_LONG)
                    .show()
            }
            R.id.menuNotes -> {
                sharedViewModel.setGotoHomePageStatus(true)
            }
            R.id.menuDeleted -> {
                sharedViewModel.setGoToDeletedNotePageStatus(true)
            }
            R.id.menuArchives -> {
                sharedViewModel.setGotoArchivedNotesPageStatus(true)
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


    private fun worker() {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(object :ConnectivityManager.NetworkCallback(){
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    val constraints: Constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()

                    val syncWorkRequest: WorkRequest =
                        OneTimeWorkRequestBuilder<SyncWorker>()
                            .setConstraints(constraints)
                            .build()

                    WorkManager.getInstance(this@MainActivity).enqueue(syncWorkRequest)
                }

            })
        }
    }
}