package com.example.fundoapp.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
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
import com.example.fundoapp.viewModel.LabelViewModel
import com.example.fundoapp.viewModel.LabelViewModelFactory
import com.example.fundoapp.viewModel.SharedViewModel
import com.example.fundoapp.viewModel.SharedViewModelFactory


class CreateLabelFragment : Fragment() {

    lateinit var labelName: EditText
    lateinit var saveLabel: ImageView
    lateinit var labelViewModel: LabelViewModel
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: DisplayLabelsAdapter
    lateinit var toolbar: Toolbar
    lateinit var userIcon: ImageView
    lateinit var gridorLinear: ImageView
    lateinit var searchBar: TextView
    lateinit var searchview: SearchView
    lateinit var deleteBtn: ImageView

    var list = mutableListOf<Label>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_label, container, false)
        labelViewModel = ViewModelProvider(
            this,
            LabelViewModelFactory()
        )[LabelViewModel::class.java]

        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        labelName = view.findViewById(R.id.labelName)
        saveLabel = view.findViewById(R.id.labelSaveBtn)
        recyclerView = view.findViewById(R.id.rvAllLabels)
        adapter = DisplayLabelsAdapter(list, labelViewModel, requireContext())

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter


        saveLabel.setOnClickListener {
            hideKeyboard()
            if (labelName.text.toString().isNotEmpty()) {
                labelViewModel.createLabel(labelName.text.toString(), requireContext())
                labelName.setText("")
                labelName.requestFocus()
            }
        }
        handleToolbar()
        labelViewModel.createLabelStatus.observe(viewLifecycleOwner) {
            list.add(it)
            val pos = list.indexOf(it)
            adapter.notifyItemInserted(pos)
        }

        labelViewModel.readLabelsFromDatabaseStatus.observe(viewLifecycleOwner) {
            list.clear()
            for (i in 0..it.size - 1) {
                list.add(it[i])
                adapter.notifyItemInserted(list.size - 1)
            }
        }
        labelViewModel.deleteLabelStatus.observe(viewLifecycleOwner) {
            val pos = list.indexOf(it)
            list.remove(it)
            adapter.notifyItemRemoved(pos)
        }
        labelViewModel.updateLabelStatus.observe(viewLifecycleOwner) {
            list.forEachIndexed { index, label ->
                if(it.labelId == label.labelId){
                    label.labelName = it.labelName
                    adapter.notifyItemChanged(index)
                }
            }
        }

        readLabels()
        return view
    }

    private fun readLabels() {
        labelViewModel.readLabels(requireContext())
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
            sharedViewModel.setGotoHomePageStatus(true)
        }
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}