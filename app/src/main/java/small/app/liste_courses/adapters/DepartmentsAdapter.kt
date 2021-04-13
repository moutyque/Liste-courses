package small.app.liste_courses.adapters

import android.content.Context
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.SortedList

import kotlinx.android.synthetic.main.item_department.view.*
import kotlinx.coroutines.launch
import small.app.liste_courses.R
import small.app.liste_courses.Scope
import small.app.liste_courses.Utils
import small.app.liste_courses.Utils.repo
import small.app.liste_courses.adapters.listeners.ILastItemUsed
import small.app.liste_courses.adapters.listeners.ItemsDropListener

import small.app.liste_courses.adapters.sortedListAdapterCallback.DepartmentCallBack
import small.app.liste_courses.model.Department


class DepartmentsAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<DepartmentsAdapter.DepartmentViewHolder>(), IList<Department> {


    var canMove = false

    var departments: SortedList<Department> =
        SortedList(Department::class.java, DepartmentCallBack(this))

    init {

        Scope.backgroundScope.launch {
            val usedDepartment = repo.getUsedDepartment()
            with(departments) {
                beginBatchedUpdates()
                addAll(usedDepartment.map { departmentWithItems ->
//                    val sortedList: SortedList<Item> =
//                        SortedList(Item::class.java, ItemCallBack(null))
//                    with(sortedList) {
//                        beginBatchedUpdates()
//                        addAll(departmentWithItems.items)
//                        endBatchedUpdates()
//                    }

                    Department(
                        name = departmentWithItems.department.name,
                        isUsed = departmentWithItems.department.isUsed,
                        items = departmentWithItems.items.filter { i->i.isUsed }.toMutableList(),
                        order = departmentWithItems.department.order
                    )
                }.toMutableList())
                endBatchedUpdates()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        return DepartmentViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_department,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return departments.size()
    }

    override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {
        val model = departments[position]

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
            model.name,
            context,
            false,
            object : ILastItemUsed{
                override fun onLastItemUse() {
                    departments[position].isUsed = false
                    Utils.saveDepartment(departments[position])
                    departments.removeItemAt(position)
                }

            }
        )
        holder.itemView.rv_items.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        holder.itemView.rv_items.adapter = itemsAdapter
        val dragListen = ItemsDropListener(itemsAdapter,model)
        holder.itemView.setOnDragListener(dragListen)

    }


    fun onItemMove(initialPosition: Int, targetPosition: Int) {
        with(departments) {
            beginBatchedUpdates()
            var init = departments.get(initialPosition)
            val target = departments.get(targetPosition)
            departments.remove(init)
            departments.remove(target)
            val tmp = init.order
            init.order = target.order
            target.order = tmp
            departments.add(init)
            departments.add(target)
            endBatchedUpdates()
        }

    }


    class DepartmentViewHolder(view: View) : ViewHolder(view)

    fun addDepartment(d: Department) {

        Utils.saveDepartment(d)


        Scope.mainScope.launch {
            with(departments) {
                beginBatchedUpdates()
                add(d)
                endBatchedUpdates()
            }
        }
    }

    override fun add(i: Department) {
        Scope.mainScope.launch {
            departments.add(i)
        }

    }

    override fun remove(i: Department) {
        Scope.mainScope.launch {
            departments.remove(i)
        }
    }

    override fun contains(i: Department): Boolean {
        return departments.indexOf(i) > -1
    }


    override fun findIndex(i: Department) : Int{
        for(index in 0 until departments.size()){
            if(departments[index].name == i.name){
            return index
            }
        }
        return -1
    }


}