package com.example.fundoapp

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.SearchView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import service.DBHandler
import util.Notes
import util.SharedPref
import util.TodoAdapter
import util.TodoAdpaterLinear
import viewmodels.ProfileViewModel
import viewmodels.ProfileViewModelFactory
import viewmodels.SharedViewModel
import viewmodels.SharedViewModelFactory
import java.util.*


class ProfileFragment : Fragment(), SearchView.OnCloseListener {

    lateinit var dialog: Dialog
    lateinit var userIcon: ImageView
    lateinit var layout: ImageView
    lateinit var searchBar: TextView
    lateinit var searchview: SearchView
    lateinit var deleteBtn: ImageView
    lateinit var dialogLogout: Button
    lateinit var dialogProfile: ImageView
    lateinit var dialogEdit: ImageView
    lateinit var dialogUsername: TextView
    lateinit var dialogEmail: TextView
    lateinit var dialogClose: ImageView
    lateinit var getImage: ActivityResultLauncher<String>
    lateinit var addNoteFAB: View
    lateinit var adapter: TodoAdapter
    lateinit var linearAdpater: TodoAdpaterLinear
    lateinit var gridrecyclerView: RecyclerView

    var noteList = mutableListOf<Notes>()
    var tempList = mutableListOf<Notes>()


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

        initializeViewModels()
        initializeVar(view)
        gridrecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapterListener()
        toolbarHandling()
        observe()
        getUserDetails()
        //getUserNotes()
        getNotesFromSql()
        loadAvatar(userIcon)
        initialiseDialog()
        checkLayout()
        takePhoto()
        onClickListeners()
        searchNote()
        searchview.setOnCloseListener(this)

        return view
    }

    private fun initializeViewModels() {
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]

        profileViewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory()
        )[ProfileViewModel::class.java]
    }

    private fun takePhoto() {
        getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                dialogProfile.setImageURI(it)
                userIcon.setImageURI(it)
                val uid = sharedViewModel.getCurrentUid()
                profileViewModel.uploadProfile(uid, it)
            }
        )
    }

    private fun initializeVar(view: View) {
        userIcon = requireActivity().findViewById(R.id.userProfile)
        layout = requireActivity().findViewById(R.id.notesLayout)
        searchBar = requireActivity().findViewById(R.id.searchNotes)
        searchview = requireActivity().findViewById(R.id.searchView)
        deleteBtn = requireActivity().findViewById(R.id.deleteButton)
        addNoteFAB = view.findViewById(R.id.floatingButton)
        gridrecyclerView = view.findViewById(R.id.rvNotes)
        adapter = TodoAdapter(tempList)
        linearAdpater = TodoAdpaterLinear(tempList)
    }

    private fun adapterListener() {
        adapter.setOnItemClickListner(object : TodoAdapter.onItemClickListner {
            override fun onItemClick(position: Int) {

                setValuesForUpdation(position)
                Toast.makeText(
                    requireContext(),
                    "You clicked item ${position + 1}",
                    Toast.LENGTH_SHORT
                ).show()
                sharedViewModel.setGotoAddNotesPage(true)
            }

        })

        linearAdpater.setOnItemClickListner(object : TodoAdpaterLinear.onItemClickListner {
            override fun onItemClick(position: Int) {
                setValuesForUpdation(position)
                Toast.makeText(
                    requireContext(),
                    "You clicked item ${position + 1}",
                    Toast.LENGTH_SHORT
                ).show()
                sharedViewModel.setGotoAddNotesPage(true)
            }

        })

    }

    private fun getNotesFromSql() {
        val db = DBHandler(requireContext())
        val userNotes = db.getNotes(sharedViewModel.getCurrentUid())
        tempList.clear()
        noteList.clear()
        noteList.addAll(userNotes)
        tempList.addAll(userNotes)
        gridrecyclerView.adapter?.notifyDataSetChanged()
    }


    private fun searchNote() {
        searchview.setOnSearchClickListener {
            userIcon.isVisible = false
            layout.isVisible = false
            searchBar.isVisible = false
            searchview.maxWidth = Integer.MAX_VALUE

        }

        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                tempList.clear()

                val searchTxt = newText!!.toLowerCase(Locale.getDefault())
                if (searchTxt.isNotEmpty()) {
                    noteList.forEach {
                        if (it.title.toLowerCase(Locale.getDefault()).contains(searchTxt)) {
                            tempList.add(it)
                        }
                    }
                    gridrecyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    tempList.clear()
                    tempList.addAll(noteList)
                    gridrecyclerView.adapter!!.notifyDataSetChanged()
                }

                return false
            }

        })
    }

    private fun setValuesForUpdation(position: Int) {
        SharedPref.setUpdateStatus("updateStatus", true)
        SharedPref.updateNotePosition("position", position + 1)
        SharedPref.addString("title", noteList[position].title)
        SharedPref.addString("note", noteList[position].note)
    }

    private fun getUserNotes() {
        profileViewModel.readNotesFromDatabase()
    }

    private fun checkLayout() {
        var count = SharedPref.get("counter")
        if (count == "") {
            gridrecyclerView.isVisible = false
            gridrecyclerView.adapter = adapter
            gridrecyclerView.isVisible = true

        } else if (count == "true") {
            layout.setImageResource(R.drawable.ic_baseline_grid_on_24)
            gridrecyclerView.isVisible = false
            gridrecyclerView.layoutManager = LinearLayoutManager(requireContext())
            gridrecyclerView.adapter = linearAdpater
            gridrecyclerView.isVisible = true

        } else if (count == "false") {
            layout.setImageResource(R.drawable.ic_linear_24)
            gridrecyclerView.isVisible = false
            gridrecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            gridrecyclerView.adapter = adapter
            gridrecyclerView.isVisible = true
        }
    }

    private fun toolbarHandling() {
        userIcon.isVisible = true
        layout.isVisible = true
        searchBar.isVisible = true
        searchview.isVisible = true
        deleteBtn.isVisible = false
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
                Picasso.get().load(it).into(dialogProfile)
            }
        }

        profileViewModel.databaseReadingStatus.observe(viewLifecycleOwner) {
            email = it.email
            fullName = it.fullName
            SharedPref.addString("email", email!!)
            SharedPref.addString("name", fullName!!)
            dialogEmail.text = email
            dialogUsername.text = fullName
        }
        profileViewModel.readNotesFromDatabaseStatus.observe(viewLifecycleOwner) {
            noteList.clear()
            tempList.clear()
            gridrecyclerView.isVisible = false
            for (i in 0..it.size - 1) {
                noteList.add(it[i])
            }
            tempList.addAll(noteList)
            SharedPref.addNoteSize("noteSize", noteList.size)

            if (SharedPref.get("counter") == "") {
                //recyclerView.isVisible=false
                gridrecyclerView.adapter = adapter
                adapter.notifyItemInserted(noteList.size - 1)
                gridrecyclerView.isVisible = true
            } else if (SharedPref.get("counter") == "true") {
                gridrecyclerView.isVisible = false
                gridrecyclerView.layoutManager = LinearLayoutManager(requireContext())
                linearAdpater.notifyItemInserted(noteList.size - 1)
                gridrecyclerView.adapter = linearAdpater
                gridrecyclerView.isVisible = true
            } else if (SharedPref.get("counter") == "false") {
                gridrecyclerView.isVisible = false
                gridrecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                adapter.notifyItemInserted(noteList.size - 1)
                gridrecyclerView.adapter = adapter
                gridrecyclerView.isVisible = true
            }
            Log.d("reading notes", "Size of note  list is" + noteList.size)

        }

    }


    private fun onClickListeners() {
        userIcon.setOnClickListener {
            dialog.show()
        }

        dialogEdit.setOnClickListener {
            getImage.launch("image/*")
        }

        dialogLogout.setOnClickListener {
            userIcon.setImageResource(R.drawable.man)
            dialogProfile.setImageResource(R.drawable.man)
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
            SharedPref.addString("counter", "true")

        } else {
            layout.setImageResource(R.drawable.ic_linear_24)
            Toast.makeText(requireContext(), "grid notes will be loaded ", Toast.LENGTH_SHORT)
                .show()
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
        dialogLogout = dialog.findViewById(R.id.dailogueLogout)
        dialogProfile = dialog.findViewById(R.id.dialogProfile)
        dialogEdit = dialog.findViewById(R.id.editProfile)
        dialogEmail = dialog.findViewById(R.id.dailogueEmail)
        dialogUsername = dialog.findViewById(R.id.dailogueuserName)
        dialogClose = dialog.findViewById(R.id.dialogClose)
        val sharePrefName = SharedPref.get("name")
        val sharePrefEmail = SharedPref.get("email")
        val sharePrefUriString = SharedPref.get("uri")
        val photoUri = sharePrefUriString?.toUri()

        dialogUsername.text = sharePrefName
        dialogEmail.text = sharePrefEmail
        if (sharePrefUriString == "") {
            dialogProfile.setImageResource(R.drawable.man)
        } else {
            Picasso.get().load(photoUri).into(dialogProfile)
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
            profileViewModel.fetchProfile()
        } else {
            Picasso.get().load(photoUri).into(userIcon)
        }
//        userIcon?.setImageResource(R.drawable.man)
    }

    private fun getUserDetails() {

        profileViewModel.readUserFRomDatabase()


    }

    override fun onClose(): Boolean {
        userIcon.isVisible = true
        layout.isVisible = true
        searchBar.isVisible = true
        return false
    }


}


