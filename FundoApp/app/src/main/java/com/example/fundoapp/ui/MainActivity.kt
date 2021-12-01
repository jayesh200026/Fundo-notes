package com.example.fundoapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.work.*
import com.example.fundoapp.*
import com.example.fundoapp.R
import com.example.fundoapp.service.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.example.fundoapp.viewModel.SharedViewModel
import com.example.fundoapp.viewModel.SharedViewModelFactory
import com.example.fundoapp.util.SharedPref
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.util.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit
import com.google.firebase.messaging.ktx.messaging
import com.google.android.gms.tasks.OnCompleteListener

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
            .fallbackToDestructiveMigration().build()
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
        val bundle = intent.extras
        observeNavigation()
        if (savedInstanceState == null && bundle == null) {
            gotoSplashScreen()
        }
        NotificationHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            "reminder notes", "App notification channel."
        )
        readNotes()
        getFirebaseMessagingToken()
        subscribe()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val bundle = intent?.extras
        if (bundle != null) {
            Toast.makeText(this, "Inside if block", Toast.LENGTH_SHORT).show()
            if (bundle.getString("Destination") == "userNote") {
                Toast.makeText(this, "destination is usernote", Toast.LENGTH_SHORT).show()
                val note = bundle.getSerializable("reminderNote") as NotesKey
                loadNotifiedNote(note)
            } else if (bundle.getString("Destination") == "home") {
                Toast.makeText(this, "destination is home", Toast.LENGTH_SHORT).show()
                gotoHomePage()
            }
        }
    }


    private fun subscribe() {
        Firebase.messaging.subscribeToTopic("weather")
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this, "Failed subscribing", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, " subscribed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loadNotifiedNote(note: NotesKey) {
        SharedPref.setUpdateStatus("updateStatus", true)
        SharedPref.addString("title", note.title)
        SharedPref.addString("note", note.note)
        SharedPref.addString("key", note.key)
        SharedPref.addString(Constants.COLUMN_MODIFIEDTIME, note.mTime)
        SharedPref.addBoolean(Constants.COLUMN_DELETED, note.deleted)
        SharedPref.addBoolean(Constants.COLUMN_ARCHIVED, note.archived)
        SharedPref.addRemainder(Constants.COLUMN_REMAINDER, note.remainder)
        //sharedViewModel.setGotoAddNotesPage(true)
        gotoAddNotesPage()
    }

    private fun readNotes() {
        sharedViewModel.readNotes(this)
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
                    Log.d("label", "going to label page")
                    gotoLabelPage()
                }
            })

        sharedViewModel.gotoAddLablesPageStatus.observe(this@MainActivity,
            {
                if (it) {
                    gotoAddLabelPage()
                }
            })
        sharedViewModel.gotoArchivedNotesPageStatus.observe(this@MainActivity,
            {
                if (it) {
                    gotoArchivedNotesPage()
                }
            })

        sharedViewModel.gotoRemainderPageStatus.observe(this@MainActivity,
            {
                if (it) {
                    gotoRemainderPage()
                }
            })
        sharedViewModel.readNotesStatus.observe(this, {
            scheduleNotification(it)
        })
    }

    @SuppressLint("RestrictedApi")
    private fun scheduleNotification(it: MutableList<NotesKey>) {
        val currentTime = System.currentTimeMillis()
        for (i in it) {
            val reminder = i.remainder
            val delay = reminder - currentTime
            if (delay > 0) {
                val data = Data.Builder()
                data.putString("noteTitle", i.title)
                data.putString("noteContent", i.note)
                data.putString("noteKey", i.key)
                data.putBoolean("isDeleted", i.deleted)
                data.putBoolean("isArchived", i.archived)
                data.putString("modifiedTime", i.mTime)
                data.putLong("reminder", i.remainder)
                Toast.makeText(
                    this,
                    "set remainder in " + TimeUnit.MILLISECONDS.toMinutes(delay) + " minutes",
                    Toast.LENGTH_SHORT
                ).show()
                val request = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                    .setInputData(data.build())
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .build()

                //WorkManager.getInstance(this).enqueue(request)
                WorkManager.getInstance(this).enqueueUniqueWork(
                    i.key,
                    ExistingWorkPolicy.REPLACE,
                    request
                )
            }
        }
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
            replace(R.id.flFragment, AddLabelToNoteFragment())
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
                Log.d("label", "clicked create label")
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
            connectivityManager.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
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

    fun getFirebaseMessagingToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("firebasemessaging", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("firebasemessaging", token.toString())
        })
    }
}