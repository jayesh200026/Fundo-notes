package com.example.fundoapp.ui.adapters

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R

class LabelToNoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val label:TextView = itemView.findViewById(R.id.rvlabelText)
    private val checkBox:CheckBox = itemView.findViewById(R.id.labelCB)
}