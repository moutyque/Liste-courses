package small.app.liste_courses.adapters.sortedListAdapterCallback

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedListAdapterCallback
import small.app.liste_courses.room.entities.Item

class ItemCallBack (adapter: RecyclerView.Adapter<*>?) : SortedListAdapterCallback<Item>(
    adapter
) {
    override fun compare(o1: Item?, o2: Item?): Int {
        return o1!!.order.compareTo(o2!!.order)
    }

    override fun areContentsTheSame(oldItem: Item?, newItem: Item?): Boolean {
        return oldItem!!.name == newItem!!.name
    }

    override fun areItemsTheSame(item1: Item?, item2: Item?): Boolean {
        return item1 == item2
    }

}