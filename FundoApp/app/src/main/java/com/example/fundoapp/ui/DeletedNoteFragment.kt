package com.example.fundoapp.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R
import com.example.fundoapp.util.NotesKey
import com.example.fundoapp.util.TodoAdapter
import com.example.fundoapp.util.TodoAdpaterLinear
import com.example.fundoapp.viewModel.*
import util.Utillity


class DeletedNoteFragment : Fragment() {
    lateinit var userIcon: ImageView
    lateinit var layout: ImageView
    lateinit var searchBar: TextView
    lateinit var searchview: SearchView
    lateinit var deleteBtn: ImageView
    lateinit var addNoteFAB: View
    lateinit var adapter: TodoAdapter
    lateinit var linearAdpater: TodoAdpaterLinear
    lateinit var gridrecyclerView: RecyclerView
    lateinit var deleteViewModel: DeleteNoteViewModel
    lateinit var sharedViewModel: SharedViewModel

    var noteList = mutableListOf<NotesKey>()
    var tempList = mutableListOf<NotesKey>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        deleteViewModel = ViewModelProvider(
            this,
            DeleteNoteViewModelFactory()
        )[DeleteNoteViewModel::class.java]

        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]

        userIcon = requireActivity().findViewById(R.id.userProfile)
        layout = requireActivity().findViewById(R.id.notesLayout)

        searchBar = requireActivity().findViewById(R.id.searchNotes)
        searchview = requireActivity().findViewById(R.id.searchView)
        deleteBtn = requireActivity().findViewById(R.id.deleteButton)
        addNoteFAB = view.findViewById(R.id.floatingButton)
        gridrecyclerView = view.findViewById(R.id.rvNotes)
        adapter = TodoAdapter(tempList)
        linearAdpater = TodoAdpaterLinear(tempList)
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
                linearAdpater,
                adapter
            )
        }


        return view
    }

    private fun observe() {
        deleteViewModel.readNotesFromDatabaseStatus.observe(viewLifecycleOwner) {
            noteList.clear()
            tempList.clear()
            gridrecyclerView.adapter = adapter
            for (i in 0..it.size - 1) {
                if (it[i].deleted) {
                    noteList.add(it[i])
                }
            }
            tempList.addAll(noteList)
            gridrecyclerView.adapter?.notifyDataSetChanged()
        }

        deleteViewModel.restoreNoteStatus.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.setGoToDeletedNotePageStatus(true)
            }
        }
        deleteViewModel.deleteForeverNoteStatus.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.setGoToDeletedNotePageStatus(true)
            }
        }
    }

    private fun getUserNotes() {
        deleteViewModel.readNotesFromDatabase(requireContext())
    }

    private fun adapterListener() {
        adapter.setOnItemClickListner(object : TodoAdapter.onItemClickListner {
            override fun onItemClick(position: Int) {
                var alertDialog = AlertDialog.Builder(requireContext()).create()

                alertDialog.setTitle(noteList[position].title)

                alertDialog.setMessage(noteList[position].note)

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Restore",
                    DialogInterface.OnClickListener { dialog, id ->
                        deleteViewModel.restoreNote(
                            requireContext(), noteList[position].title,
                            noteList[position].note, noteList[position].key
                        )
                        Toast.makeText(requireContext(), "Restore", Toast.LENGTH_SHORT).show()
                    })

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Delete",
                    DialogInterface.OnClickListener { dialog, id ->
                        deleteViewModel.deleteForever(requireContext(), noteList[position].key)
                        Toast.makeText(requireContext(), "Delete", Toast.LENGTH_SHORT).show()
                    })

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
                    })

                alertDialog.show()
            }

        })

        linearAdpater.setOnItemClickListner(object : TodoAdpaterLinear.onItemClickListner {
            override fun onItemClick(position: Int) {
                //setValuesForUpdation(position)
                Toast.makeText(
                    requireContext(),
                    "You clicked item ${position + 1}",
                    Toast.LENGTH_SHORT
                ).show()
                //sharedViewModel.setGotoAddNotesPage(true)
            }

        })

    }

    private fun toolbarHandling() {
        userIcon.isVisible = true
        layout.isVisible = true
        searchBar.isVisible = false
        searchview.isVisible = false
        deleteBtn.isVisible = false
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