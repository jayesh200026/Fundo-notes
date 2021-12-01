package com.example.fundoapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R
import com.example.fundoapp.roomdb.entity.LabelEntity
import com.example.fundoapp.service.model.Label

class LabelToNoteAdapter(
    var labels: List<Label>, var list: List<String>
) : RecyclerView.Adapter<LabelToNoteAdapter.LabelViewHolder>() {
    inner class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var selectedLabels = mutableListOf<Label>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_label, parent, false)
        return LabelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        val label = holder.itemView.findViewById<TextView>(R.id.rvlabelText)
        val checkBox = holder.itemView.findViewById<CheckBox>(R.id.labelCB)
        checkBox.setOnClickListener {
            if (checkBox.isChecked && (labels[position] !in selectedLabels)) {
                selectedLabels.add(labels[position])
            } else {
                selectedLabels.remove(labels[position])
            }
        }

        holder.itemView.apply {
            label.text = labels[position].labelName
            if (labels[position].labelId in list) {
                checkBox.isChecked = true
                selectedLabels.add(labels[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return labels.size
    }

    fun getSelectedList(): MutableList<Label> {
        return selectedLabels
    }
}