package com.example.fundoapp.ui

import android.app.Dialog
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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.ui.adapters.NoteAdapter
import com.example.fundoapp.util.Constants
import com.example.fundoapp.util.SharedPref
import com.squareup.picasso.Picasso
import com.example.fundoapp.viewModel.HomeViewModel
import com.example.fundoapp.viewModel.HomeViewModelFactory
import com.example.fundoapp.viewModel.SharedViewModel
import com.example.fundoapp.viewModel.SharedViewModelFactory
import com.example.fundoapp.util.Utillity
import com.google.android.material.navigation.NavigationView


class HomeFragment : Fragment(), SearchView.OnCloseListener {

    lateinit var dialog: Dialog
    lateinit var userIcon: ImageView
    lateinit var layout: ImageView
    lateinit var searchBar: TextView
    lateinit var searchview: SearchView
    lateinit var deleteBtn: ImageView
    lateinit var remainder: ImageView
    lateinit var archive: ImageView
    lateinit var dialogLogout: Button
    lateinit var dialogProfile: ImageView
    lateinit var dialogEdit: ImageView
    lateinit var dialogUsername: TextView
    lateinit var dialogEmail: TextView
    lateinit var dialogClose: ImageView
    lateinit var getImage: ActivityResultLauncher<String>
    lateinit var addNoteFAB: View
    lateinit var adapter: NoteAdapter
    lateinit var navMenu: NavigationView
    lateinit var progressBar: ProgressBar
    lateinit var gridrecyclerView: RecyclerView
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var profileViewModel: HomeViewModel

    var noteList = mutableListOf<NotesKey>()
    var tempList = mutableListOf<NotesKey>()
    var startTime = ""
    var offset = 0
    var noteListSize = 0
    var isLoading = false
    var currentItem: Int = 0
    var totalItem: Int = 0
    var scrolledOutItems: Int = 0
    var email: String? = null
    var fullName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        initializeViewModels()
        initializeVar(view)
        gridrecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapterListener()
        toolbarHandling()
        observe()
        getUserDetails()
        readNotes()
        loadAvatar(userIcon)
        initialiseDialog()
        checkLayout()
        takePhoto()
        onClickListeners()
        searchNote()
        searchview.setOnCloseListener(this)
        gridrecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (SharedPref.get(Constants.LAYOUT) == "" ||
                    SharedPref.get(Constants.LAYOUT) == Constants.GRID
                ) {
                    currentItem = (gridrecyclerView.layoutManager as GridLayoutManager).childCount
                    totalItem = (gridrecyclerView.layoutManager as GridLayoutManager).itemCount
                    scrolledOutItems = (gridrecyclerView.layoutManager as GridLayoutManager)
                        .findFirstVisibleItemPosition()
                    if (!isLoading) {
                        if ((currentItem + scrolledOutItems) >= totalItem && scrolledOutItems >= 0) {
                            if (startTime != "" && noteListSize == 0) {
                                isLoading = true
                                offset = 0
                                progressBar.visibility = View.VISIBLE
                                readNotesFromStart()
                            } else {
                                isLoading = true
                                progressBar.visibility = View.VISIBLE
                                readNotes()
                            }
                        }
                    }

                } else if (SharedPref.get(Constants.LAYOUT) == Constants.LINEAR) {
                    currentItem = (gridrecyclerView.layoutManager as LinearLayoutManager).childCount
                    totalItem = (gridrecyclerView.layoutManager as LinearLayoutManager).itemCount
                    scrolledOutItems = (gridrecyclerView.layoutManager as LinearLayoutManager)
                        .findFirstVisibleItemPosition()
                    if (!isLoading) {
                        if ((currentItem + scrolledOutItems) >= totalItem && scrolledOutItems >= 0) {
                            if (startTime != "" && noteListSize == 0) {
                                isLoading = true
                                offset = 0
                                progressBar.visibility = View.VISIBLE
                                readNotesFromStart()
                            } else {
                                isLoading = true
                                progressBar.visibility = View.VISIBLE
                                readNotes()
                            }
                        }
                    }
                }
            }
        })
        return view
    }

    private fun readNotesFromStart() {
        profileViewModel.readNotes("", offset, requireContext())
    }

    private fun readNotes() {
        profileViewModel.readNotes(startTime, offset, requireContext())
    }

    private fun initializeViewModels() {
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]

        profileViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory()
        )[HomeViewModel::class.java]
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
        remainder = requireActivity().findViewById(R.id.remainder)
        archive = requireActivity().findViewById(R.id.archiveImage)
        navMenu = requireActivity().findViewById(R.id.myNavMenu)
        addNoteFAB = view.findViewById(R.id.floatingButton)
        gridrecyclerView = view.findViewById(R.id.rvNotes)
        progressBar = view.findViewById(R.id.rvProgressBar)
        adapter = NoteAdapter(tempList)
    }

    private fun adapterListener() {
        adapter.setOnItemClickListner(object : OnItemClickListner{
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
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun setValuesForUpdation(position: Int) {
        SharedPref.setUpdateStatus("updateStatus", true)
        SharedPref.updateNotePosition("position", position + 1)
        SharedPref.addString("title", noteList[position].title)
        SharedPref.addString("note", noteList[position].note)
        SharedPref.addString("key", noteList[position].key)
        SharedPref.addString(Constants.IS_NEWNOTE, "false")
        SharedPref.addString(Constants.COLUMN_MODIFIEDTIME, noteList[position].mTime)
        SharedPref.addBoolean(Constants.COLUMN_DELETED, noteList[position].deleted)
        SharedPref.addBoolean(Constants.COLUMN_ARCHIVED, noteList[position].archived)
        SharedPref.addRemainder(Constants.COLUMN_REMAINDER, noteList[position].remainder)
    }

    private fun getUserNotes() {
        profileViewModel.readNotesFromDatabase(requireContext())
    }

    private fun checkLayout() {
        var count = SharedPref.get(Constants.LAYOUT)
        if (count == "") {
            gridrecyclerView.isVisible = false
            gridrecyclerView.adapter = adapter
            gridrecyclerView.isVisible = true

        } else if (count == Constants.LINEAR) {
            layout.setImageResource(R.drawable.ic_baseline_grid_on_24)
            gridrecyclerView.isVisible = false
            gridrecyclerView.layoutManager = LinearLayoutManager(requireContext())
            gridrecyclerView.adapter = adapter
            //gridrecyclerView.adapter = linearAdpater
            gridrecyclerView.isVisible = true

        } else if (count == Constants.GRID) {
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
        archive.isVisible = false
        remainder.isVisible = false
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            requireActivity().findViewById(R.id.drawerLayout),
            requireActivity().findViewById(R.id.myToolbar),
            R.string.open,
            R.string.close
        )
        navMenu.setCheckedItem(R.id.menuNotes)
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
    }


    fun observe() {
        profileViewModel.readNotesStatus.observe(viewLifecycleOwner) {
            noteListSize = it.size
            offset += it.size
            isLoading = false
            Log.d("Limited notes", it.size.toString())
            if (it.size == 0) {
                progressBar.visibility = View.GONE
                isLoading = false
            } else {
                for (i in 0 until it.size) {
                    if (!it[i].deleted && !it[i].archived) {
                        noteList.add(it[i])
                        tempList.add(it[i])
                        startTime = it[i].mTime
                        adapter.notifyItemInserted(tempList.size - 1)
                        progressBar.isVisible = false
                        gridrecyclerView.isVisible = true
                    }
                }
                isLoading = false
            }
        }

        profileViewModel.profilePhotoUploadStatus.observe(viewLifecycleOwner) {
            if (it) {
                profileViewModel.fetchProfile()
            }
        }

        profileViewModel.profilePhotoFetch.observe(viewLifecycleOwner) {
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
            layout.setImageResource(R.drawable.ic_linear_24)
            SharedPref.clearAll()
            dialog.dismiss()
            sharedViewModel.logout()
            profileViewModel.clearTables(requireContext())
            sharedViewModel.setGoToLoginPageStatus(true)
        }
        dialogClose.setOnClickListener {
            dialog.dismiss()
        }

        layout.setOnClickListener {
            Utillity.loadNotesInLayoutType(
                requireContext(),
                layout,
                gridrecyclerView,
                adapter
            )
        }

        addNoteFAB.setOnClickListener {
            SharedPref.addString(Constants.IS_NEWNOTE, "true")
            sharedViewModel.setGotoAddNotesPage(true)
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


