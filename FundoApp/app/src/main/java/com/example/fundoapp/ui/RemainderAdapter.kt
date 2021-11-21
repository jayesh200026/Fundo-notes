package com.example.fundoapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.util.Constants
import java.text.SimpleDateFormat
import java.util.*

class RemainderAdapter(var notes: List<NotesKey>) :
    RecyclerView.Adapter<RemainderAdapter.RemainderViewHolder>() {
    inner class RemainderViewHolder(itemView: View,listener:onItemClickListner) : RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }


    private lateinit var mListner: onItemClickListner

    interface onItemClickListner {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListner(listener: onItemClickListner) {
        mListner = listener

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RemainderAdapter.RemainderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.remainder_note, parent, false)
        return RemainderViewHolder(view,mListner)
    }

    override fun onBindViewHolder(holder: RemainderAdapter.RemainderViewHolder, position: Int) {
        val title = holder.itemView.findViewById<TextView>(R.id.remainderTitle)
        val note = holder.itemView.findViewById<TextView>(R.id.remainderNote)
        val time = holder.itemView.findViewById<TextView>(R.id.remainderTime)

        holder.itemView.apply {
            title.text = notes[position].title
            note.text = notes[position].note
            time.text = millisToDate(notes[position].remainder)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    private fun millisToDate(millis: Long): String {
        return SimpleDateFormat(Constants.DATE_FORMAT, Locale.US).format(Date(millis))
    }
}