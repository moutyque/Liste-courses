package small.app.liste_courses.adapters.listeners

import android.util.Log
import android.view.DragEvent
import android.view.View
import small.app.liste_courses.objects.Utils
import small.app.liste_courses.adapters.ItemsAdapter
import small.app.liste_courses.models.Department
import small.app.liste_courses.models.DragItem

class ItemsDropListener(private val itemsAdapter: ItemsAdapter, private val dep: Department) :
    View.OnDragListener {
    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        // Handles each of the expected events
        when (event!!.action) {
            DragEvent.ACTION_DROP -> {
                Log.d("DAdapter", "Has drop $v")
                val droppedItem = event.localState

                if (droppedItem is DragItem) {
                    Log.d("DAdapter", "Dropped ${droppedItem.item.name}")

                    dep.classify(droppedItem.item)
                    Utils.classifyItem(droppedItem.item, droppedItem.adapter, itemsAdapter)
                    droppedItem.adapter.itemUsed.onItemUse()
                }
                return true
            }
            else -> {
                // An unknown action type was received.
                Log.e(
                    "DAdapter",
                    "Untreated action type received by OnDragListener : ${event.action}."
                )
                return true
            }
        }
    }
}