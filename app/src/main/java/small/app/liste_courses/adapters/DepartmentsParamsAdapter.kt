package small.app.liste_courses.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_department.view.*
import small.app.liste_courses.R
import small.app.liste_courses.adapters.listeners.IActions
import small.app.liste_courses.adapters.listeners.IItemUsed
import small.app.liste_courses.adapters.listeners.ItemsDropListener
import small.app.liste_courses.models.Department
import small.app.liste_courses.objects.Utils
import small.app.liste_courses.room.entities.Item

/*
TODO : Display all department and all items under it
TODO : Manage modification of qty and unit
TODO : Manage items order
TODO : Manager department order

TODO : update main fragment
 */
class DepartmentsParamsAdapter(context: Context, onlyUsed: Boolean = false) :
    DepartmentsAbstractAdapter(context, onlyUsed), IActions {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return DepartmentsParamsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_departmnet_param,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        holder.itemView.tv_dep_name.text = model.name
        //Perform the D&D action on department only if we click on the department title and not and the item list
        holder.itemView.tv_dep_name.setOnTouchListener { v, event ->
            Log.d("ClickOnDep", "I touched $event")

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    canMove = true
                    v.performClick()
                }
                MotionEvent.ACTION_UP -> canMove = false
            }
            true
        }
        //Recycler view for the items in the department

        Log.d("DAdapter", "department name : ${model.name} & items ${model.items}")


        val itemsAdapter = DepartmentItemsAdapter(
            context,
            false,
            object : IItemUsed {
                override fun onLastItemUse() {
                    list[0].isUsed = false
                    Utils.saveDepartment(list[0])
                    list.removeAt(0)
                }

                override fun onItemUse() {

                }

            }
        )
        holder.itemView.rv_items.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        holder.itemView.rv_items.adapter = itemsAdapter
        val dragListen = ItemsDropListener(itemsAdapter, model)
        holder.itemView.setOnDragListener(dragListen)
    }


    class DepartmentsParamsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    override fun onNewDepartment(d: Department) {
        list.add(d)
    }

    override fun onItemChange(i: Item) {
        TODO("Not yet implemented")
    }

    override fun onItemClassify(i: Item) {
        TODO("Not yet implemented")
    }
}