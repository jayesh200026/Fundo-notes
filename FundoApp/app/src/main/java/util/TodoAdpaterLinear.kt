package util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R

class TodoAdpaterLinear(
    var todos:List<Notes>
): RecyclerView.Adapter<TodoAdpaterLinear.TodoViewHolderLinear>() {
   inner class TodoViewHolderLinear(itemview:View):RecyclerView.ViewHolder(itemview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolderLinear {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.linear_layout,parent,false)
        return TodoViewHolderLinear(view)
    }

    override fun onBindViewHolder(holder: TodoAdpaterLinear.TodoViewHolderLinear, position: Int) {
        val title= holder.itemView.findViewById<TextView>(R.id.linearTitle)
        val note= holder.itemView.findViewById<TextView>(R.id.linearNote)
        holder.itemView.apply {
            title.text=todos[position].title
            note.text=todos[position].note
        }
    }

    override fun getItemCount(): Int {
        return todos.size
    }

}