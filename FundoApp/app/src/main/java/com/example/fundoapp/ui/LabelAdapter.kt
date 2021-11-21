package com.example.fundoapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R
import com.example.fundoapp.roomdb.entity.LabelEntity

class LabelAdapter(
    var labels: List<LabelEntity>,var list:List<String>
) : RecyclerView.Adapter<LabelAdapter.LabelViewHolder>() {
    inner class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var selectedLabels= mutableListOf<LabelEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_label, parent, false)
        return LabelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        val label = holder.itemView.findViewById<TextView>(R.id.rvlabelText)
        val checkBox=holder.itemView.findViewById<CheckBox>(R.id.labelCB)

        //val labeltext=labels[position].label
        checkBox.setOnClickListener {
            if(checkBox.isChecked){
                selectedLabels.add(labels[position])
            }
            else{
                selectedLabels.remove(labels[position])
            }
        }

        if(labels[position].labelId in list){
            checkBox.isChecked=true
        }
        holder.itemView.apply {
            label.text = labels[position].label
        }
    }

    override fun getItemCount(): Int {
        return labels.size
    }

    fun getSelectedList():MutableList<LabelEntity>{
        return selectedLabels
    }
}