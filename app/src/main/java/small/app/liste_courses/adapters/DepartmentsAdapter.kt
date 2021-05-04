package small.app.liste_courses.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.item_department.view.*
import small.app.liste_courses.R
import small.app.liste_courses.adapters.diffutils.DepartmentsDiffUtils
import small.app.liste_courses.adapters.listeners.ItemsDropListener
import small.app.liste_courses.objects.Utils
import small.app.liste_courses.room.entities.DepartmentWithItems


class DepartmentsAdapter(
    context: Context, onlyUsed: Boolean = true
) :
    DepartmentsAbstractAdapter(context, onlyUsed) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {

        return DepartmentViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_department,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        fillView(position, holder)

    }

    private fun fillView(
        position: Int,
        holder: ViewHolder
    ) {
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

        var itemsAdapter = holder.itemView.rv_items.adapter
        if (itemsAdapter == null) {

            itemsAdapter = DepartmentItemsAdapter(
                context,
                false
            )

            holder.itemView.rv_items.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            holder.itemView.rv_items.adapter = itemsAdapter
        }

        (itemsAdapter as DepartmentItemsAdapter).updateList(model.items.filter { it.isUsed })


        val dragListen = ItemsDropListener(itemsAdapter, model)
        holder.itemView.setOnDragListener(dragListen)
    }


    fun onItemMove(initialPosition: Int, targetPosition: Int) {
        if (initialPosition > -1 && targetPosition > -1) {
            with(list) {
                // beginBatchedUpdates()
                val init = get(initialPosition)
                val target = get(targetPosition)

                val tmp = init.order
                init.order = target.order
                target.order = tmp

                Utils.saveDepartment(init)
                Utils.saveDepartment(target)
                //  endBatchedUpdates()
            }
        }


    }


    class DepartmentViewHolder(view: View) : ViewHolder(view)



    override fun updateList(inList: List<DepartmentWithItems>?) {

        if (inList != null) {
            val departments = inList.map { it.toDepartment() }
            departments.sortedBy { item -> item.order }
            val diffResult = DiffUtil.calculateDiff(DepartmentsDiffUtils(this.list, departments))
            this.list.clear()
            this.list.addAll(departments)
            //this.list.sortedBy { item -> item.order }
            diffResult.dispatchUpdatesTo(this)
        }
    }

    //Only when a new department is created this is called I think
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.filterIsInstance<DepartmentWithItems>().forEach { _q ->
                run {

                    fillView(position, holder)
                }

            }

        }


    }

}