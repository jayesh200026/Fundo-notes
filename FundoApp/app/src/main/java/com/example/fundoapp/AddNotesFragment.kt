package com.example.fundoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import util.Notes
import viewmodels.AddNoteViewModel
import viewmodels.AddNoteViewModelFactory
import viewmodels.SharedViewModel
import viewmodels.SharedViewModelFactory


class AddNotesFragment : Fragment(), View.OnClickListener {

    lateinit var toolbar:Toolbar
    lateinit var userIcon: ImageView
    lateinit var gridorLinear:ImageView
    lateinit var searchBar:TextView
    lateinit var title:EditText
    lateinit var note:EditText
    lateinit var savetext:TextView
    lateinit var saveBtn:FloatingActionButton
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var addNoteViewModel:AddNoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_add_notes, container, false)

        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]

        addNoteViewModel=ViewModelProvider(
            requireActivity(),
            AddNoteViewModelFactory()
        )[AddNoteViewModel::class.java]

        observe()

        userIcon = requireActivity().findViewById(R.id.userProfile)
        gridorLinear=requireActivity().findViewById(R.id.notesLayout)
        searchBar=requireActivity().findViewById(R.id.searchNotes)
        title=view.findViewById(R.id.noteTitle)
        note=view.findViewById(R.id.userNote)
        saveBtn=view.findViewById(R.id.saveFAB)
        savetext=view.findViewById(R.id.saveText)
        userIcon.isVisible=false
        gridorLinear.isVisible=false
        searchBar.isVisible=false
        toolbar=requireActivity().findViewById(R.id.myToolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        saveBtn.setOnClickListener(this)
        savetext.setOnClickListener(this)
//        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        return view
    }

    private fun observe() {
    addNoteViewModel.databaseNoteAddedStatus.observe(viewLifecycleOwner){
        if(it){
            sharedViewModel.setGotoHomePageStatus(true)
        }
        else{
            Toast.makeText(requireContext(),"database storing failed",Toast.LENGTH_SHORT).show()
        }
    }
    }


    override fun onClick(view: View?) {
        when(view?.id){
            R.id.saveText,R.id.saveFAB->{
                storeToDatabase()
            }
        }
    }

    private fun storeToDatabase() {
        val titleText=title.text.toString()
        val noteText=note.text.toString()

        val note=Notes(titleText,noteText)
        addNoteViewModel.addNotesToDatabase(note)

    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                requireActivity().onBackPressed()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }




}