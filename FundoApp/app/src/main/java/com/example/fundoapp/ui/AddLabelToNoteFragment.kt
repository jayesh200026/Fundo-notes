package com.example.fundoapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R
import com.example.fundoapp.roomdb.entity.LabelEntity
import com.example.fundoapp.service.model.Label
import com.example.fundoapp.ui.adapters.LabelToNoteAdapter
import com.example.fundoapp.util.SharedPref
import com.example.fundoapp.viewModel.AddLabelViewModel
import com.example.fundoapp.viewModel.AddLabelViewModelFactory
import com.example.fundoapp.viewModel.SharedViewModel
import com.example.fundoapp.viewModel.SharedViewModelFactory


class AddLabelToNoteFragment : Fragment() {
    lateinit var toolbar: Toolbar
    lateinit var userIcon: ImageView
    lateinit var gridorLinear: ImageView
    lateinit var searchBar: TextView
    lateinit var searchview: SearchView
    lateinit var deleteBtn: ImageView
    lateinit var labelRV: RecyclerView
    lateinit var labelToNoteAdapter: LabelToNoteAdapter
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var fabBtn: View
    lateinit var addLabelViewModel: AddLabelViewModel
    var labelsList = mutableListOf<Label>()
    var selectedLabels = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_label, container, false)
        labelRV = view.findViewById(R.id.rvLabels)
        labelToNoteAdapter = LabelToNoteAdapter(labelsList, selectedLabels)
        labelRV.layoutManager = LinearLayoutManager(requireContext())
        labelRV.adapter = labelToNoteAdapter
        addLabelViewModel = ViewModelProvider(this,
            AddLabelViewModelFactory())[AddLabelViewModel::class.java]
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        handleToolbar()
        readLabels()
        readNoteslabelRelation()
        fabBtn = view.findViewById(R.id.saveLabelFab)
        fabBtn.setOnClickListener {
            val list = labelToNoteAdapter.getSelectedList()
            addLabelViewModel.addLables(list, requireContext())
        }
        addLabelViewModel.readLabelsFromDatabaseStatus.observe(viewLifecycleOwner) {
            labelsList.clear()
            for (i in 0..it.size - 1) {
                labelsList.add(it[i])
                Log.d("size", it.size.toString())
                labelToNoteAdapter.notifyItemInserted(labelsList.size - 1)
            }
        }
        addLabelViewModel.readNotesLabelsFromDatabaseStatus.observe(viewLifecycleOwner) {
            var key = SharedPref.get("key")
            for (i in 0 until it.size) {
                if (it[i].noteID == key!!) {
                    selectedLabels.add(it[i].labelId)
                }
            }
        }
        return view
    }

    private fun readNoteslabelRelation() {
        addLabelViewModel.readNoteLabel(requireContext())
    }

    private fun readLabels() {
        addLabelViewModel.readLabels(requireContext())
    }

    private fun handleToolbar() {
        userIcon = requireActivity().findViewById(R.id.userProfile)
        gridorLinear = requireActivity().findViewById(R.id.notesLayout)
        searchBar = requireActivity().findViewById(R.id.searchNotes)
        searchview = requireActivity().findViewById(R.id.searchView)
        deleteBtn = requireActivity().findViewById(R.id.deleteButton)
        toolbar = requireActivity().findViewById(R.id.myToolbar)
        userIcon.isVisible = false
        gridorLinear.isVisible = false
        searchBar.isVisible = false
        searchview.isVisible = false
        deleteBtn.isVisible = false
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            SharedPref.setUpdateStatus("updateStatus", false)
            sharedViewModel.setGotoHomePageStatus(true)
        }
    }
}