package com.example.fundoapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.ui.OnItemClickListner
import java.util.*
import kotlin.collections.ArrayList

class NoteAdapter(
    var notes: List<NotesKey>
) : RecyclerView.Adapter<NoteViewHolder>(), Filterable {

    private lateinit var mListner: OnItemClickListner
    var filteredNotes: ArrayList<NotesKey> = ArrayList()

    init {
        filteredNotes = notes as ArrayList<NotesKey>
    }

    fun setOnItemClickListner(listener: OnItemClickListner) {
        mListner = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_layout, parent, false)
        return NoteViewHolder(view, mListner)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val item = filteredNotes[position]
        holder.bind(item)
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
}