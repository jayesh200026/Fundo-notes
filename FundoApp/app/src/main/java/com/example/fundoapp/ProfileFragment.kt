package com.example.fundoapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.squareup.picasso.Picasso
import service.Authentication
import service.Firebasedatabase
import util.Notes
import util.SharedPref
import util.TodoAdapter
import util.TodoAdpaterLinear
import viewmodels.ProfileViewModel
import viewmodels.ProfileViewModelFactory
import viewmodels.SharedViewModel
import viewmodels.SharedViewModelFactory


class ProfileFragment : Fragment() {

    lateinit var dialog: Dialog
    lateinit var userIcon: ImageView
    lateinit var layout: ImageView
    lateinit var searchBar: TextView
    lateinit var dailog_logout: Button
    lateinit var dialog_profile: ImageView
    lateinit var dailog_edit: ImageView
    lateinit var dailog_username: TextView
    lateinit var dailog_email: TextView
    lateinit var dialogClose: ImageView
    lateinit var getImage: ActivityResultLauncher<String>
    lateinit var addNoteFAB: View
    lateinit var adapter: TodoAdapter
    lateinit var linearAdpater: TodoAdpaterLinear

    //lateinit var recyclerView:RecyclerView
    lateinit var gridrecyclerView: RecyclerView
    var noteList = mutableListOf<Notes>()

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var profileViewModel: ProfileViewModel
    var email: String? = null
    var fullName: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        //(requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        var profilePhot: Uri? = null

        userIcon = requireActivity().findViewById(R.id.userProfile)
        layout = requireActivity().findViewById(R.id.notesLayout)
        searchBar = requireActivity().findViewById(R.id.searchNotes)
        addNoteFAB = view.findViewById(R.id.floatingButton)
        adapter = TodoAdapter(noteList)
        linearAdpater = TodoAdpaterLinear(noteList)

        //recyclerView=view.findViewById(R.id.rvNotes)
        gridrecyclerView = view.findViewById(R.id.rvNotes)
        //recyclerView.adapter=adapter
        //recyclerView.layoutManager=LinearLayoutManager(requireContext())
        gridrecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        //gridrecyclerView.adapter=adapter

        toolbarHandling()


        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]

        profileViewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory()
        )[ProfileViewModel::class.java]

        observe()

        getUserDetails()
        getUserNotes()


        loadAvatar(userIcon)
        if (SharedPref.get("uri") == "") {
            profileViewModel.fetchProfile()
        }

        initialiseDialog()

        checkLayout()

        getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                dialog_profile.setImageURI(it)
                userIcon.setImageURI(it)
                val uid = sharedViewModel.getCurrentUid()
                profileViewModel.uploadProfile(uid, it)
            }
        )

        onClickListeners()

        return view
    }

    private fun getUserNotes() {
        profileViewModel.readNotesFromDatabase()
    }

    private fun checkLayout() {
        var count = SharedPref.get("counter")
        if (count == "") {
            // recyclerView.isVisible=false
            //recyclerView.adapter=adapter
            gridrecyclerView.adapter = adapter
            gridrecyclerView.isVisible = true

        } else if (count == "true") {
            layout.setImageResource(R.drawable.ic_baseline_grid_on_24)
            gridrecyclerView.isVisible = false
            //recyclerView.adapter=linearAdpater
            //recyclerView.isVisible=true

        } else if (count == "false") {
            layout.setImageResource(R.drawable.ic_linear_24)
            // recyclerView.isVisible=false
            // recyclerView.adapter=adapter
            gridrecyclerView.adapter = adapter
            gridrecyclerView.isVisible = true
        }
    }

    private fun toolbarHandling() {
        userIcon.isVisible = true
        layout.isVisible = true
        searchBar.isVisible = true
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            requireActivity().findViewById(R.id.drawerLayout),
            requireActivity().findViewById(R.id.myToolbar),
            R.string.open,
            R.string.close
        )
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
    }


    fun observe() {
        profileViewModel.profilePhotoUploadStatus.observe(viewLifecycleOwner) {
            if (it) {
                profileViewModel.fetchProfile()
            }
        }

        profileViewModel.profilePhotoFetch.observe(viewLifecycleOwner) {
            // profilePhot = it

            if (it != null) {
                SharedPref.addString("uri", it.toString())
                Picasso.get().load(it).into(userIcon)
                Picasso.get().load(it).into(dialog_profile)
            }
        }

        profileViewModel.databaseReadingStatus.observe(viewLifecycleOwner) {
            email = it.email
            fullName = it.fullName
            SharedPref.addString("email", email!!)
            SharedPref.addString("name", fullName!!)
            dailog_email.text = email
            dailog_username.text = fullName
        }
        profileViewModel.readNotesFromDatabaseStatus.observe(viewLifecycleOwner) {
            noteList.clear()
//            gridrecyclerView.adapter=adapter
            //recyclerView.adapter=linearAdpater
            gridrecyclerView.isVisible = false
            //recyclerView.isVisible=false
            for (i in 0..it.size - 1) {
                noteList.add(it[i])
            }
            SharedPref.addNoteSize("noteSize", noteList.size)

            if (SharedPref.get("counter") == "") {
                //recyclerView.isVisible=false
                gridrecyclerView.adapter = adapter
                adapter.notifyItemInserted(noteList.size - 1)
                gridrecyclerView.isVisible = true
            } else if (SharedPref.get("counter") == "true") {
                gridrecyclerView.isVisible = false
                //recyclerView.adapter=linearAdpater
                //recyclerView.isVisible=true
                linearAdpater.notifyItemInserted(noteList.size - 1)
                //recyclerView.isVisible=true
            } else if (SharedPref.get("counter") == "false") {
                //recyclerView.isVisible=false
                gridrecyclerView.adapter = adapter
                adapter.notifyItemInserted(noteList.size - 1)
                gridrecyclerView.isVisible = true
            }
            Log.d("reading notes", "Size of note  list is" + noteList.size)

        }

    }


    private fun onClickListeners() {
        userIcon.setOnClickListener {
            dialog.show()
        }

        dailog_edit.setOnClickListener {
            getImage.launch("image/*")
        }

        dailog_logout.setOnClickListener {
            userIcon.setImageResource(R.drawable.man)
            dialog_profile.setImageResource(R.drawable.man)
            SharedPref.clearAll()
            dialog.dismiss()
            sharedViewModel.logout()
            sharedViewModel.setGoToLoginPageStatus(true)
        }
        dialogClose.setOnClickListener {
            dialog.dismiss()
        }

        layout.setOnClickListener {

            loadNotesInLayoutType()
        }

        addNoteFAB.setOnClickListener {
            sharedViewModel.setGotoAddNotesPage(true)
        }
    }

    private fun loadNotesInLayoutType() {

        var flag: Boolean
        var count = SharedPref.get("counter")
        if (count == "") {
            flag = true
        } else if (count == "true") {
            flag = false
        } else {
            flag = true
        }

        if (flag) {
            layout.setImageResource(R.drawable.ic_baseline_grid_on_24)
            Toast.makeText(requireContext(), "linear notes will be loaded ", Toast.LENGTH_SHORT)
                .show()
            gridrecyclerView.isVisible = false
            gridrecyclerView.layoutManager = LinearLayoutManager(requireContext())
            gridrecyclerView.adapter = linearAdpater
            gridrecyclerView.isVisible = true
            //recyclerView.adapter=linearAdpater
            //recyclerView.isVisible=true
            SharedPref.addString("counter", "true")

        } else {
            layout.setImageResource(R.drawable.ic_linear_24)
            Toast.makeText(requireContext(), "grid notes will be loaded ", Toast.LENGTH_SHORT)
                .show()
            //recyclerView.adapter=adapter
            //recyclerView.isVisible=false
            gridrecyclerView.isVisible = false
            gridrecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            gridrecyclerView.adapter = adapter
            gridrecyclerView.isVisible = true
            SharedPref.addString("counter", "false")

        }


    }

    private fun initialiseDialog() {
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dailogue)
        dailog_logout = dialog.findViewById(R.id.dailogueLogout)
        dialog_profile = dialog.findViewById(R.id.dialogProfile)
        dailog_edit = dialog.findViewById(R.id.editProfile)
        dailog_email = dialog.findViewById(R.id.dailogueEmail)
        dailog_username = dialog.findViewById(R.id.dailogueuserName)
        dialogClose = dialog.findViewById(R.id.dialogClose)
        val sharePrefName = SharedPref.get("name")
        val sharePrefEmail = SharedPref.get("email")
        val sharePrefUriString = SharedPref.get("uri")
        val photoUri = sharePrefUriString?.toUri()

        dailog_username.text = sharePrefName
        dailog_email.text = sharePrefEmail
        if (sharePrefUriString == "") {
            dialog_profile.setImageResource(R.drawable.man)
        } else {
            Picasso.get().load(photoUri).into(dialog_profile)
        }


        dialog.window?.setBackgroundDrawable(
            getDrawable(
                requireContext(),
                R.drawable.custom_dialog_background
            )
        )
        dialog.window
            ?.setLayout(800, 700)
        dialog.setCancelable(false) //Optional
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }


    private fun loadAvatar(userIcon: ImageView?) {
        val sharePrefUriString = SharedPref.get("uri")
        val photoUri = sharePrefUriString?.toUri()
        if (sharePrefUriString == "") {
            userIcon?.setImageResource(R.drawable.man)
        } else {
            Picasso.get().load(photoUri).into(userIcon)
        }
//        userIcon?.setImageResource(R.drawable.man)
    }

    private fun getUserDetails() {

        profileViewModel.readUserFRomDatabase()


    }


}