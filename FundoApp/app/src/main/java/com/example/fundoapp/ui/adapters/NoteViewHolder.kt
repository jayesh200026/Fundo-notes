package com.example.fundoapp.ui.adapters

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.ui.OnItemClickListner
import com.example.fundoapp.util.Constants
import java.text.SimpleDateFormat
import java.util.*

class NoteViewHolder(view: View, listener: OnItemClickListner) :
    RecyclerView.ViewHolder(view) {
    private val title: TextView = view.findViewById(R.id.gridTitle)
    private val note: TextView = view.findViewById(R.id.gridNote)
    private val remainder: TextView = view.findViewById(R.id.gridRemainder)

    init {
        view.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }
    }

    fun bind(item: NotesKey) {
        title.text = item.title
        note.text = item.note

        if (item.remainder == 0L) {
            remainder.visibility = View.GONE
        } else if (item.remainder > 0) {
            remainder.isVisible = true
            remainder.text = millisToDate(item.remainder)
        }
    }

    fun millisToDate(millis: Long): String {
        return SimpleDateFormat(Constants.DATE_FORMAT, Locale.US).format(Date(millis))
    }

}