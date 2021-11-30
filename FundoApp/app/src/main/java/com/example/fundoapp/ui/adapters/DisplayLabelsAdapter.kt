package com.example.fundoapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R
import com.example.fundoapp.service.model.Label
import com.example.fundoapp.viewModel.LabelViewModel

class DisplayLabelsAdapter(
    var labels: List<Label>, var labelViewModel: LabelViewModel, var context: Context
) : RecyclerView.Adapter<LabelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.display_labels, parent, false)
        return LabelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        val item = labels[position]
        holder.bind(item, labelViewModel, context)
    }

    override fun getItemCount(): Int {
        return labels.size
    }
}