package com.example.fundoapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.util.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NoteAdapter(
    var notes: List<NotesKey>
) : RecyclerView.Adapter<NoteAdapter.TodoViewHolder>(), Filterable {
    inner class TodoViewHolder(itemview: View, listener: onItemClickListner) :
        RecyclerView.ViewHolder(itemview) {
        init {
            itemview.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    private lateinit var mListner: onItemClickListner
    var filteredNotes: ArrayList<NotesKey> = ArrayList()

    init {
        filteredNotes = notes as ArrayList<NotesKey>
    }

    interface onItemClickListner {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListner(listener: onItemClickListner) {
        mListner = listener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_layout, parent, false)
        return TodoViewHolder(view, mListner)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val title = holder.itemView.findViewById<TextView>(R.id.gridTitle)
        val note = holder.itemView.findViewById<TextView>(R.id.gridNote)
        val remainder = holder.itemView.findViewById<TextView>(R.id.gridRemainder)

        holder.itemView.apply {
            title.text = filteredNotes[position].title
            note.text = filteredNotes[position].note
            if(filteredNotes[position].remainder == 0L){
                remainder.visibility = View.GONE
            }
            else if(filteredNotes[position].remainder > 0){
                remainder.isVisible = true
                remainder.text = millisToDate(filteredNotes[position].remainder)
            }
        }

    }

    override fun getItemCount(): Int {
        return filteredNotes.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filteredNotes = if (charSearch.isEmpty()) {
                    notes as ArrayList<NotesKey>
                } else {
                    val resultList = ArrayList<NotesKey>()
                    for (row in notes) {
                        if ((row.title.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                                    or (row.note.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))))
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredNotes
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredNotes = results?.values as ArrayList<NotesKey>
                notifyDataSetChanged()
            }

        }
    }
    private fun millisToDate(millis: Long): String {
        return SimpleDateFormat(Constants.DATE_FORMAT, Locale.US).format(Date(millis))
    }


}