package com.example.fundoapp.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R
import com.example.fundoapp.roomdb.entity.LabelEntity
import com.example.fundoapp.service.model.Label
import com.example.fundoapp.viewModel.LabelViewModel

class DisplayLabelsAdapter(
    var labels: List<Label>, var labelViewModel: LabelViewModel, var context: Context
) : RecyclerView.Adapter<DisplayLabelsAdapter.LabelViewHolder>() {

    inner class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.display_labels, parent, false)
        return LabelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        val delete = holder.itemView.findViewById<ImageView>(R.id.deleteLabel)
        val labelIcon = holder.itemView.findViewById<ImageView>(R.id.label_image)
        val label = holder.itemView.findViewById<EditText>(R.id.edittextlabel)
        val update = holder.itemView.findViewById<ImageView>(R.id.save_label)
        val edit = holder.itemView.findViewById<ImageView>(R.id.editLabelBtn)

        labelIcon.setOnClickListener {
            label.requestFocus()
        }


        edit.setOnClickListener {
            edit.isVisible = false
            labelIcon.isVisible = false
            delete.isVisible = true
            update.isVisible = true
            label.requestFocus()
        }
        label.setOnFocusChangeListener { view, b ->
            if(b){
                edit.isVisible = false
                labelIcon.isVisible = false
                delete.isVisible = true
                update.isVisible = true
            }
            else{
                edit.isVisible = true
                labelIcon.isVisible = true
                delete.isVisible = false
                update.isVisible = false
            }
        }




        holder.itemView.apply {
            label.setText(labels[position].labelName)
        }

        delete.setOnClickListener {
            Log.d("label", "will delete " + labels[position].labelName)
            labelViewModel.deleteLabel(labels[position], context)
        }

        update.setOnClickListener {
            val text = label.text.toString()
            labelViewModel.updateLabel(labels[position], context, text)
            label.clearFocus()
        }
    }


    override fun getItemCount(): Int {
        return labels.size
    }

}