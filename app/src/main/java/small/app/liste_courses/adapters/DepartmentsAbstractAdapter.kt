package small.app.liste_courses.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import small.app.liste_courses.models.Department
import small.app.liste_courses.objects.Scope
import small.app.liste_courses.room.entities.DepartmentWithItems

abstract class DepartmentsAbstractAdapter(
    val context: Context,
    private val onlyUsed: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), IList<Department> {

    var canMove = false
    val list =
        mutableListOf<Department>()//: SortedList<Department> =        SortedList(Department::class.java, DepartmentCallBack(this))
/*
    init {

        Scope.backgroundScope.launch {
            val usedDepartment = if(onlyUsed) Utils.repo.getUsedDepartment() else Utils.repo.getDepartments()
            with(list) {
                beginBatchedUpdates()
                addAll(usedDepartment.map { departmentWithItems ->

                    Department(
                        name = departmentWithItems.department.name,
                        isUsed = departmentWithItems.department.isUsed,
                        items = if (onlyUsed) departmentWithItems.items.filter { i -> i.isUsed }.toMutableList() else departmentWithItems.items.toMutableList(),
                        order = departmentWithItems.department.order
                    )

                }.toMutableList())
                endBatchedUpdates()
            }

        }
    }*/


    override fun getItemCount(): Int {
        return list.size
    }

    override fun add(i: Department) {
        Scope.mainScope.launch {
            list.add(i)
        }

    }

    override fun remove(i: Department) {
    }

    override fun contains(i: Department): Boolean {
        return list.indexOf(i) > -1
    }


    override fun findIndex(i: Department): Int {
        for (index in 0 until list.size) {
            if (list[index].name == i.name) {
                return index
            }
        }
        return -1
    }

    open fun updateList(departments: List<DepartmentWithItems>?) {

    }


}