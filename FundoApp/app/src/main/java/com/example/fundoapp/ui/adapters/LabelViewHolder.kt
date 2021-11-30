package com.example.fundoapp.ui.adapters

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R
import com.example.fundoapp.service.model.Label
import com.example.fundoapp.viewModel.LabelViewModel

class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private val delete: ImageView = itemView.findViewById(R.id.deleteLabel)
    private val labelIcon: ImageView = itemView.findViewById(R.id.label_image)
    private val label: EditText = itemView.findViewById(R.id.edittextlabel)
    private val update: ImageView = itemView.findViewById(R.id.save_label)
    private val edit: ImageView = itemView.findViewById(R.id.editLabelBtn)

    fun bind(item: Label, labelViewModel: LabelViewModel, context: Context) {
        label.setText(item.labelName)
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
        delete.setOnClickListener {
            labelViewModel.deleteLabel(item, context)
        }
        update.setOnClickListener {
            val text = label.text.toString()
            labelViewModel.updateLabel(item, context, text)
            label.clearFocus()
        }
    }
}