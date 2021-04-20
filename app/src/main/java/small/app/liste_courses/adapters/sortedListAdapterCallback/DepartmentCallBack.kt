package small.app.liste_courses.adapters.sortedListAdapterCallback

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedListAdapterCallback
import small.app.liste_courses.models.Department

class DepartmentCallBack(adapter: RecyclerView.Adapter<*>?) : SortedListAdapterCallback<Department>(
    adapter
) {
    override fun compare(o1: Department?, o2: Department?): Int {
        return o1!!.order.compareTo(o2!!.order)
    }

    override fun areContentsTheSame(oldItem: Department?, newItem: Department?): Boolean {
        return oldItem!!.name == newItem!!.name
    }

    override fun areItemsTheSame(item1: Department?, item2: Department?): Boolean {
        return item1 == item2
    }
}