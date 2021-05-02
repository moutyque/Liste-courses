package small.app.liste_courses.adapters

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import small.app.liste_courses.adapters.diffutils.DepartmentsDiffUtils
import small.app.liste_courses.models.Department
import small.app.liste_courses.room.entities.DepartmentWithItems

abstract class DepartmentsAbstractAdapter(
    val context: Context,
    private val onlyUsed: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var canMove = false
    val list =
        mutableListOf<Department>()
    override fun getItemCount(): Int {
        return list.size
    }

    open fun updateList(departments: List<DepartmentWithItems>?) {
        if (list != null) {
            list.sortedBy { dep -> dep.order }
            val diffResult = DiffUtil.calculateDiff(DepartmentsDiffUtils(this.list, list), false)
            this.list.clear()
            this.list.addAll(list)
            //this.list.sortedBy { item -> item.order }
            diffResult.dispatchUpdatesTo(this)
        }
    }


}