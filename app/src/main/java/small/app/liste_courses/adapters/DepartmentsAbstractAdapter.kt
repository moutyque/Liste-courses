package small.app.liste_courses.adapters

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import small.app.liste_courses.adapters.diffutils.DepartmentsDiffUtils
import small.app.liste_courses.models.Department
import small.app.liste_courses.objects.DepartmentComparator
import small.app.liste_courses.objects.Utils
import small.app.liste_courses.room.entities.DepartmentWithItems

abstract class DepartmentsAbstractAdapter(
    val context: Context,
    private val onlyUsed: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var canMove = false
    private val _list=  mutableListOf<Department>()
    val list: List<Department>
        get()=_list

    override fun getItemCount(): Int {
        return list.size
    }

    open fun updateList(inlist: List<DepartmentWithItems>?) {
        if (inlist != null) {
            val departments = inlist.map { it.toDepartment() }
            departments.sortedWith(DepartmentComparator())
            val diffResult =
                DiffUtil.calculateDiff(DepartmentsDiffUtils(this.list, departments), false)
            diffResult.dispatchUpdatesTo(this)
            this._list.clear()
            this._list.addAll(departments)

            //this.list.sortedWith(DepartmentComparator())

        }
    }
    fun onItemMove(initialPosition: Int, targetPosition: Int) {
        if (initialPosition > -1 && targetPosition > -1) {
            with(list) {
                val init = get(initialPosition)
                val target = get(targetPosition)

                val tmp = init.order
                init.order = target.order
                target.order = tmp

                Utils.saveDepartment(init)
                Utils.saveDepartment(target)
            }
        }


    }


}