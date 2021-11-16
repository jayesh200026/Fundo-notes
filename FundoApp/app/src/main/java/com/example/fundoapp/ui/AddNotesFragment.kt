package com.example.fundoapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.fundoapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.fundoapp.service.Authentication
import com.example.fundoapp.util.SharedPref
import com.example.fundoapp.viewModel.AddNoteViewModel
import com.example.fundoapp.viewModel.AddNoteViewModelFactory
import com.example.fundoapp.viewModel.SharedViewModel
import com.example.fundoapp.viewModel.SharedViewModelFactory


class AddNotesFragment : Fragment(), View.OnClickListener {

    lateinit var toolbar: Toolbar
    lateinit var userIcon: ImageView
    lateinit var gridorLinear: ImageView
    lateinit var searchBar: TextView
    lateinit var searchview: SearchView
    lateinit var deleteBtn: ImageView
    lateinit var title: EditText
    lateinit var note: EditText
    lateinit var savetext: TextView
    lateinit var saveBtn: FloatingActionButton
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var addNoteViewModel: AddNoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_notes, container, false)

        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]

        addNoteViewModel = ViewModelProvider(
            this,
            AddNoteViewModelFactory()
        )[AddNoteViewModel::class.java]

        observe()
        initializeVar(view)
        handleToolbar()
        onClicks()
        checkIfUpdate()
        return view
    }

    private fun checkIfUpdate() {
        val updateStatus = SharedPref.getUpdateStatus("updateStatus")
        if (updateStatus) {
            deleteBtn.isVisible = true
            updateNote()
        }
    }

    private fun onClicks() {
        saveBtn.setOnClickListener(this)
        savetext.setOnClickListener(this)
        deleteBtn.setOnClickListener(this)
    }

    private fun handleToolbar() {
        userIcon.isVisible = false
        gridorLinear.isVisible = false
        searchBar.isVisible = false
        searchview.isVisible = false
        deleteBtn.isVisible = false

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun initializeVar(view: View) {
        userIcon = requireActivity().findViewById(R.id.userProfile)
        gridorLinear = requireActivity().findViewById(R.id.notesLayout)
        searchBar = requireActivity().findViewById(R.id.searchNotes)
        searchview = requireActivity().findViewById(R.id.searchView)
        deleteBtn = requireActivity().findViewById(R.id.deleteButton)
        title = view.findViewById(R.id.noteTitle)
        note = view.findViewById(R.id.userNote)
        saveBtn = view.findViewById(R.id.saveFAB)
        savetext = view.findViewById(R.id.saveText)
        toolbar = requireActivity().findViewById(R.id.myToolbar)

    }

    private fun updateNote() {
        val noteTitle = SharedPref.get("title")
        val noteContent = SharedPref.get("note")
        title.setText(noteTitle)
        note.setText(noteContent)


    }

    private fun observe() {
        addNoteViewModel.databaseNoteAddedStatus.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.setGotoHomePageStatus(true)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.noteStoringFailed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        addNoteViewModel.databaseNoteUpdateStatus.observe(viewLifecycleOwner) {
            SharedPref.setUpdateStatus("updateStatus", false)
            if (it) {
                sharedViewModel.setGotoHomePageStatus(true)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.noteUpdationFailed),
                    Toast.LENGTH_SHORT
                ).show()

            }

        }
        addNoteViewModel.databaseNoteDeletionStatus.observe(viewLifecycleOwner) {
            SharedPref.setUpdateStatus("updateStatus", false)
            if (it) {
                sharedViewModel.setGotoHomePageStatus(true)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.noteDeletionFailed),
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.saveText, R.id.saveFAB -> {
                if (SharedPref.getUpdateStatus("updateStatus")) {
                    updateNoteToDatabase()
                } else {
                    storeToDatabase()
                }
            }
            R.id.deleteButton -> {
                deleteNote()
            }
        }
    }

    private fun deleteNote() {
        val context=requireContext()
        val titleText = title.text.toString()
        val noteText = note.text.toString()
        val key = SharedPref.get("key")
        if (titleText.isNotEmpty() && noteText.isNotEmpty()) {
           addNoteViewModel.deleteNote(titleText,noteText,key!!,context)

        }


    }

    private fun updateNoteToDatabase() {
        val context = requireContext()
        val titleText = title.text.toString()
        val noteText = note.text.toString()
        val key = SharedPref.get("key")
        addNoteViewModel.updateNote(key!!, titleText, noteText, context)

    }

    private fun storeToDatabase() {
        val context = requireContext()
        val titleText = title.text.toString()
        val noteText = note.text.toString()
        val uid = Authentication.getCurrentUid()
        if(titleText!="" || noteText!=""){
            if(uid!=null) {
                addNoteViewModel.addNote(uid, titleText, noteText, context)
            }
        }


    }


}