package com.example.fundoapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.util.Constants
import com.example.fundoapp.util.SharedPref
import com.example.fundoapp.util.Utillity
import com.example.fundoapp.viewModel.*


class RemainderFragment : Fragment() {
    lateinit var userIcon: ImageView
    lateinit var layout: ImageView
    lateinit var searchBar: TextView
    lateinit var searchview: SearchView
    lateinit var deleteBtn: ImageView
    lateinit var archive: ImageView
    lateinit var remainder: ImageView
    lateinit var addNoteFAB: View
    lateinit var adapter: RemainderAdapter
    lateinit var gridrecyclerView: RecyclerView
    lateinit var sharedViewModel: SharedViewModel
    lateinit var remainderViewModel:RemainderViewModel


    var noteList = mutableListOf<NotesKey>()
    var tempList = mutableListOf<NotesKey>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]

        remainderViewModel = ViewModelProvider(
            this,
            RemainderViewModelFactory()
        )[RemainderViewModel::class.java]


        userIcon = requireActivity().findViewById(R.id.userProfile)
        layout = requireActivity().findViewById(R.id.notesLayout)

        searchBar = requireActivity().findViewById(R.id.searchNotes)
        searchview = requireActivity().findViewById(R.id.searchView)
        deleteBtn = requireActivity().findViewById(R.id.deleteButton)
        archive=requireActivity().findViewById(R.id.archiveImage)
        addNoteFAB = view.findViewById(R.id.floatingButton)
        remainder = requireActivity().findViewById(R.id.remainder)
        gridrecyclerView = view.findViewById(R.id.rvNotes)
        adapter = RemainderAdapter(tempList)
        gridrecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        addNoteFAB.isVisible = false

        toolbarHandling()
        getUserNotes()
        observe()
        adapterListener()
        layout.setOnClickListener {
            Utillity.loadRemainderNotesInLayout(
                requireContext(),
                layout,
                gridrecyclerView,
                adapter
            )
        }
        return view
    }

    private fun observe() {
        remainderViewModel.readNotesFromDatabaseStatus.observe(viewLifecycleOwner){
            noteList.clear()
            tempList.clear()
            gridrecyclerView.adapter=adapter
            for (i in 0 until it.size) {
                if (it[i].remainder > System.currentTimeMillis()) {
                    noteList.add(it[i])
                }
            }
            tempList.addAll(noteList)
            gridrecyclerView.adapter?.notifyDataSetChanged()
        }
        }


    private fun getUserNotes() {
        remainderViewModel.readNotesFromDatabase(requireContext())
    }

    private fun adapterListener() {
        adapter.setOnItemClickListner(object : RemainderAdapter.onItemClickListner {
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

    private fun setValuesForUpdation(position: Int) {
        SharedPref.setUpdateStatus("updateStatus", true)
        SharedPref.updateNotePosition("position", position + 1)
        SharedPref.addString("title", noteList[position].title)
        SharedPref.addString("note", noteList[position].note)
        SharedPref.addString("key", noteList[position].key)
        SharedPref.addString(Constants.IS_ARCHIVED,noteList[position].archived.toString())

    }

    private fun toolbarHandling() {
        userIcon.isVisible = true
        layout.isVisible = true
        searchBar.isVisible = false
        searchview.isVisible = false
        deleteBtn.isVisible = false
        archive.isVisible=false
        remainder.isVisible = false
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            requireActivity().findViewById(R.id.drawerLayout),
            requireActivity().findViewById(R.id.myToolbar),
            R.string.open,
            R.string.close
        )
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
    }

}