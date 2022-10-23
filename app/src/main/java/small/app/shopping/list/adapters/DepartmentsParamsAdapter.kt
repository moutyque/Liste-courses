package small.app.shopping.list.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import small.app.shopping.list.adapters.listeners.ItemsDropListener
import small.app.shopping.list.callback.IMovableAdapter
import small.app.shopping.list.callback.SimpleItemTouchHelperCallback
import small.app.shopping.list.databinding.ItemDepartmentParamBinding
import small.app.shopping.list.models.Department
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.objects.Utils.delete
import small.app.shopping.list.objects.Utils.saveAndUse

class DepartmentsParamsAdapter(context: Context) :
    DepartmentsAbstractAdapter(context), IMovableAdapter {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val binding =
            ItemDepartmentParamBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return DepartmentsParamsViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        require(holder is DepartmentsParamsViewHolder)
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
            //Recycler view for the items in the department
            Log.d(Utils.TAG, "department name : ${model.name} & items ${model.items}")
            var itemsAdapter = rvItems.adapter
            if (itemsAdapter == null) {
                itemsAdapter = setupAdapter()
            }

            (itemsAdapter as ItemsAdapter).updateList(model.items)


            ibExpand.setOnClickListener {
                ibExpand.visibility = View.GONE
                ibCollapse.visibility = View.VISIBLE
                rvItems.visibility = View.VISIBLE
            }
            ibCollapse.setOnClickListener {
                ibExpand.visibility = View.VISIBLE
                ibCollapse.visibility = View.GONE
                rvItems.visibility = View.GONE
            }

            val dragListen = ItemsDropListener(model)
            holder.itemView.setOnDragListener(dragListen)

            ibDeleteDepartment.setOnClickListener {
                list[position].delete()
            }

            //Setup the drag and drop only on the reorder icon
            ivReorder.setOnTouchListener { v, event ->
                Log.d(Utils.TAG, "Reorder department in Parameters Adapter. \nI touched $event")

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        canMove = true
                        v.performClick()
                    }
                    MotionEvent.ACTION_UP -> canMove = false
                }
                true
            }
        }
    }

    private fun ItemDepartmentParamBinding.setupAdapter(): RecyclerView.Adapter<*> {
        val itemsAdapter1 = ItemsParamsAdapter(
            context
        )
        rvItems.apply {
            layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = itemsAdapter1
            val callback = SimpleItemTouchHelperCallback(
                itemsAdapter1,
                SimpleItemTouchHelperCallback.Direction.VERTICAL
            )
            val itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(this)
        }
        return itemsAdapter1
    }


    override fun onItemMove(initialPosition: Int, targetPosition: Int) {
        if (initialPosition > -1 && targetPosition > -1) {
            //This is call at every move so the save must only occure once the drag is done
            with(list) {
                val init = get(initialPosition)
                val target = get(targetPosition)
                val tmp = init.order
                init.order = target.order
                target.order = tmp
                savableDepartment.add(init)
                savableDepartment.add(target)

                Utils.swapInCollection(list, initialPosition, targetPosition)
            }
            this.notifyItemMoved(initialPosition, targetPosition)
        }
    }

    override fun canMove(): Boolean {
        return canMove
    }

    override fun getAdapterList(): List<Department> {
        return list
    }

    override fun setMove(b: Boolean) {
        canMove = b
    }

    override fun onDragEnd() {
        savableDepartment.forEach { it.saveAndUse() }
        savableDepartment.clear()
    }

    class DepartmentsParamsViewHolder(val binding: ItemDepartmentParamBinding) :
        RecyclerView.ViewHolder(binding.root)

}