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
import com.example.fundoapp.util.SharedPref
import com.example.fundoapp.viewModel.AddLabelViewModel
import com.example.fundoapp.viewModel.AddLabelViewModelFactory


class AddLabelFragment : Fragment() {
    lateinit var toolbar: Toolbar
    lateinit var userIcon: ImageView
    lateinit var gridorLinear: ImageView
    lateinit var searchBar: TextView
    lateinit var searchview: SearchView
    lateinit var deleteBtn: ImageView

    lateinit var labelRV: RecyclerView
    lateinit var labelAdapter: LabelAdapter
    lateinit var fabBtn: View
    lateinit var addLabelViewModel: AddLabelViewModel
    var labelsList = mutableListOf<LabelEntity>()
    var selectedLabels = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_label, container, false)
        labelRV = view.findViewById(R.id.rvLabels)
        labelAdapter = LabelAdapter(labelsList, selectedLabels)
        labelRV.layoutManager = LinearLayoutManager(requireContext())
        labelRV.adapter = labelAdapter
        addLabelViewModel =
            ViewModelProvider(this, AddLabelViewModelFactory())[AddLabelViewModel::class.java]
handleToolbar()
        readLabels()
        readNoteslabelRelation()
        fabBtn = view.findViewById(R.id.saveLabelFab)
        fabBtn.setOnClickListener {
            val list = labelAdapter.getSelectedList()
            addLabelViewModel.addLables(list, requireContext())
        }
        addLabelViewModel.readLabelsFromDatabaseStatus.observe(viewLifecycleOwner) {
            labelsList.clear()
            for (i in 0..it.size - 1) {
                labelsList.add(it[i])
                Log.d("size", it.size.toString())
                labelAdapter.notifyItemInserted(labelsList.size - 1)
            }
        }
        addLabelViewModel.readNotesLabelsFromDatabaseStatus.observe(viewLifecycleOwner) {
            var key = SharedPref.get("key")
            for (i in 0 until it.size) {
                if (it[i].noteID == key!!) {
                    selectedLabels.add(it[i].labelId)
                }
            }
            //labelAdapter.notifyDataSetChanged()
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
            activity?.onBackPressed()
        }
    }


}