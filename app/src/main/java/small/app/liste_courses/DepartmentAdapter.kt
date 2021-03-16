package small.app.liste_courses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_department.view.*
import small.app.liste_courses.model.Department
import small.app.liste_courses.room.entities.Item

class DepartmentAdapter(private val context: Context, private var list: List<Department>) :
RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return  DepartmentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_department, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val model = list[position]
        if(holder is DepartmentViewHolder){
            holder.itemView.tv_dep_name.text = model.name
        }
    }

    class DepartmentViewHolder(view: View) : RecyclerView.ViewHolder(view)


}