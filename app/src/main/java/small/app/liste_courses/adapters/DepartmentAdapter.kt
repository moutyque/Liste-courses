package small.app.liste_courses.adapters

import android.content.ClipDescription
import android.content.Context
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_department.view.*
import small.app.liste_courses.R
import small.app.liste_courses.model.Department
import small.app.liste_courses.model.MainViewModel
import small.app.liste_courses.room.entities.Item

class DepartmentAdapter(private val context: Context, private var list: List<Department>,private var viewModel: MainViewModel) :
RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return DepartmentViewHolder(
           LayoutInflater.from(context).inflate(
               R.layout.item_department,
               parent,
               false
           )
       )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val model = list[position]
        if(holder is DepartmentViewHolder){
            holder.itemView.tv_dep_name.text = model.name

//Manage the drop action
            val dragListener = View.OnDragListener { view, event ->
                when (event.action) {
                    DragEvent.ACTION_DROP ->{
                        Log.d("DD","Has drop")
                        val item = event.clipData.getItemAt(0)

                        val localState = event.localState

                        if(localState is Item){
                            Log.d("DD","Droped ${localState.name}")
                            model.classify(localState)
                            viewModel.updateItemsList(localState)
                            viewModel.updateDepartmentsList(model)

                        }
                        val dragData = item.text
                        Toast.makeText(context,dragData, Toast.LENGTH_LONG).show()
                        true
                    }
                    else -> false

                }

            }
            holder.itemView.setOnDragListener(dragListener)


            //Recycler view for the items in the department

Log.d("DAdapter","departement name : ${model.name} & items ${model.items}")

                holder.itemView.rv_items.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                holder.itemView.rv_items.adapter = UnclassifiedItemsAdapter(
                context,
                model.items)



        }
    }

    class DepartmentViewHolder(view: View) : RecyclerView.ViewHolder(view)


}