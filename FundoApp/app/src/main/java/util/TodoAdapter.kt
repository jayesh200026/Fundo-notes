package util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R

class TodoAdapter(
    var todos:List<Notes>
):RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    inner class TodoViewHolder(itemview: View):RecyclerView.ViewHolder(itemview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.grid_layout,parent,false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
       val title= holder.itemView.findViewById<TextView>(R.id.gridTitle)
       val note= holder.itemView.findViewById<TextView>(R.id.gridNote)

        holder.itemView.apply {
            title.text=todos[position].title
            note.text=todos[position].note
        }

    }

    override fun getItemCount(): Int {
        return todos.size
    }


}