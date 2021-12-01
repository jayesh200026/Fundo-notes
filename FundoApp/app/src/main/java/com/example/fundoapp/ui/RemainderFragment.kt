package com.example.fundoapp.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
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
import com.example.fundoapp.ui.adapters.NoteAdapter
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
    lateinit var adapter: NoteAdapter
    lateinit var gridrecyclerView: RecyclerView
    lateinit var sharedViewModel: SharedViewModel
    lateinit var remainderViewModel: RemainderViewModel
    lateinit var progressBar: ProgressBar

    var noteList = mutableListOf<NotesKey>()
    var tempList = mutableListOf<NotesKey>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        archive = requireActivity().findViewById(R.id.archiveImage)
        addNoteFAB = view.findViewById(R.id.floatingButton)
        remainder = requireActivity().findViewById(R.id.remainder)
        gridrecyclerView = view.findViewById(R.id.rvNotes)
        progressBar = view.findViewById(R.id.rvProgressBar)
        adapter = NoteAdapter(tempList)
        gridrecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        addNoteFAB.isVisible = false

        toolbarHandling()
        getUserNotes()
        observe()
        adapterListener()
        layout.setOnClickListener {
            Utillity.loadNotesInLayoutType(
                requireContext(),
                layout,
                gridrecyclerView,
                adapter
            )
        }
        return view
    }

    private fun observe() {
        remainderViewModel.readNotesFromDatabaseStatus.observe(viewLifecycleOwner) {
            noteList.clear()
            tempList.clear()
            gridrecyclerView.adapter = adapter
            val currentTime = System.currentTimeMillis()
            for (i in 0 until it.size) {
                if (it[i].remainder > currentTime) {
                    noteList.add(it[i])
                    tempList.add(it[i])
                    adapter.notifyItemInserted(tempList.size - 1)
                    progressBar.isVisible = false
                    gridrecyclerView.isVisible = true
                }
            }
            progressBar.isVisible = false
        }
        remainderViewModel.removeRemainderStatus.observe(viewLifecycleOwner) {
            if (it) {
                getUserNotes()
            }
        }
    }


    private fun getUserNotes() {
        remainderViewModel.readNotesFromDatabase(requireContext())
    }

    private fun adapterListener() {
        adapter.setOnItemClickListner(object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                var alertDialog = AlertDialog.Builder(requireContext()).create()
                alertDialog.setTitle("Remove remainder from note?")
                alertDialog.setMessage(noteList[position].title + "\n" + noteList[position].note)
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Remove",
                    DialogInterface.OnClickListener { dialog, id ->
                        val userNote = NotesKey(
                            title = noteList[position].title,
                            note = noteList[position].note,
                            key = noteList[position].key,
                            deleted = noteList[position].deleted,
                            archived = noteList[position].archived,
                            mTime = noteList[position].mTime,
                            remainder = 0L
                        )
                        remainderViewModel.removeRemainder(
                            requireContext(), userNote
                        )
                        Toast.makeText(requireContext(), "Remove", Toast.LENGTH_SHORT).show()
                    })

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
                    })
                alertDialog.show()
            }
        })
    }

    private fun toolbarHandling() {
        userIcon.isVisible = true
        layout.isVisible = true
        searchBar.isVisible = false
        searchview.isVisible = false
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
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
    }
}