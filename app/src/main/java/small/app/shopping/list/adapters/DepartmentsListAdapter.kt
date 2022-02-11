package small.app.shopping.list.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.item_department.view.*
import small.app.shopping.list.R
import small.app.shopping.list.adapters.listeners.ItemsDropListener
import small.app.shopping.list.enums.DepartmentChange
import small.app.shopping.list.fragments.NewItemsDialogFragment
import small.app.shopping.list.models.Department
import small.app.shopping.list.objects.Utils


class DepartmentsListAdapter(
    context: Context
) :
    DepartmentsAbstractAdapter(context) {


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
            Log.d(Utils.TAG, "I touched $event")

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    canMove = true
                    v.performClick()
                }
                MotionEvent.ACTION_UP -> canMove = false
            }
            true
        }


        //Feature to add an item directly from the department
        holder.itemView.ib_newItems.setOnClickListener {
            val activity = context as FragmentActivity
            val fm: FragmentManager = activity.supportFragmentManager
            val dialog = NewItemsDialogFragment(model.name)
            dialog.show(fm, NewItemsDialogFragment.TAG)
        }

        //Recycler view for the items in the department
        Log.d(Utils.TAG, "department name : ${model.name} & items ${model.items}")

        setupAdapter(holder, model)
        //This drag adapter is necessary for the first items
        val dragListen = ItemsDropListener(model)
        holder.itemView.setOnDragListener(dragListen)
    }

    private fun setupAdapter(
        holder: ViewHolder,
        model: Department
    ) {
        var itemsAdapter = holder.itemView.rv_items.adapter
        if (itemsAdapter == null) {
            itemsAdapter = ClassifiedUsedItemsAdapter(
                context
            )
            holder.itemView.rv_items.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            holder.itemView.rv_items.adapter = itemsAdapter
        }

        (itemsAdapter as ItemsAdapter).updateList(model.items)
    }

    class DepartmentViewHolder(view: View) : ViewHolder(view)

    //Only when a new department is created this is called I think
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.filterIsInstance<Bundle>().forEach { bundle ->
                run {
                    bundle.keySet().forEach { key ->
                        run {
                            if (key == DepartmentChange.NAME.toString()) {
                                holder.itemView.tv_dep_name.text = bundle.get(key) as CharSequence?
                            }

                            if (key == DepartmentChange.ITEMS.toString()) {
                                (holder.itemView.rv_items.adapter as ItemsAdapter).updateList(list[position].items)
                            }
                        }
                    }
                }
            }
        }
    }
}