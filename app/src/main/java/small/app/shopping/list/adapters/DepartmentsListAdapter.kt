package small.app.shopping.list.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import small.app.shopping.list.adapters.listeners.ItemsDropListener
import small.app.shopping.list.databinding.ItemDepartmentBinding
import small.app.shopping.list.enums.DepartmentChange
import small.app.shopping.list.fragments.NewItemDialogFragment
import small.app.shopping.list.models.Department
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.room.Repository


class DepartmentsListAdapter(
    context: Context,
    private val repository: Repository
) :
    DepartmentsAbstractAdapter(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val binding =
            ItemDepartmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DepartmentViewHolder(binding)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun fillView(
        position: Int,
        holder: DepartmentViewHolder
    ) {
        val model = list[position]

        holder.binding.apply {
            tvDepName.text = model.name
            //Perform the D&D action on department only if we click on the department title and not and the item list
            tvDepName.setOnTouchListener { v, event ->
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

            //Add an item directly from the department
            ibNewItems.setOnClickListener {
                val activity = context as FragmentActivity
                val fm: FragmentManager = activity.supportFragmentManager
                val dialog = NewItemDialogFragment(model.name,model.storeId,repository)
                dialog.show(fm, NewItemDialogFragment.TAG)
            }

            //Recycler view for the items in the department
            Log.d(Utils.TAG, "department name : ${model.name} & items ${model.items}")

            setupAdapter(rvItems, model)
            //This drag adapter is necessary for the first items
            val dragListen = ItemsDropListener(model)
            holder.itemView.setOnDragListener(dragListen)
        }


    }

    private fun setupAdapter(
        recyclerView: RecyclerView,
        model: Department
    ) {
        var itemsAdapter = recyclerView.adapter
        if (itemsAdapter == null) {
            itemsAdapter = ClassifiedUsedItemsAdapter(
                context
            )
            recyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            recyclerView.adapter = itemsAdapter
        }

        (itemsAdapter as ItemsAdapter).updateList(model.items)
    }

    class DepartmentViewHolder(val binding: ItemDepartmentBinding) : ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        fillView(position, holder as DepartmentViewHolder)

    }

    //Only when a new department is created this is called I think
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        require(holder is DepartmentViewHolder)
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.filterIsInstance<Bundle>().forEach { bundle ->
                run {
                    bundle.keySet().forEach { key ->
                        run {
                            if (key == DepartmentChange.NAME.toString()) {
                                holder.binding.tvDepName.text = bundle.get(key) as CharSequence?
                            }

                            if (key == DepartmentChange.ITEMS.toString()) {
                                (holder.binding.rvItems.adapter as ItemsAdapter).updateList(list[position].items)
                            }
                        }
                    }
                }
            }
        }
    }
}