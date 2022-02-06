package small.app.shopping.list.adapters

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import small.app.shopping.list.adapters.diffutils.DepartmentsDiffUtils
import small.app.shopping.list.models.Department
import small.app.shopping.list.objects.DepartmentComparator
import small.app.shopping.list.room.entities.DepartmentWithItems

abstract class DepartmentsAbstractAdapter(
    val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var canMove = false
    private val _list = mutableListOf<Department>()
    val list: List<Department>
        get() = _list


    val savableDepartment = mutableSetOf<Department>()

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
        }
    }


}