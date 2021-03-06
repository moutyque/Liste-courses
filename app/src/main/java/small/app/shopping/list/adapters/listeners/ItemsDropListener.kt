package small.app.shopping.list.adapters.listeners

import android.util.Log
import android.view.DragEvent
import android.view.View
import small.app.shopping.list.models.Department
import small.app.shopping.list.models.DragItem

class ItemsDropListener(private val dep: Department) :
    View.OnDragListener {
    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        // Handles each of the expected events
        when (event!!.action) {
            DragEvent.ACTION_DROP -> {
                Log.d("DAdapter", "Has drop $v")
                val droppedItem = event.localState
                if (droppedItem is DragItem && droppedItem.item.departmentId != dep.name) {
                        dep.classify(droppedItem.item)
                        return true
                }
                return false
            }
            else -> {
                // An unknown action type was received.
                Log.e(
                    "DAdapter",
                    "Untreated action type received by OnDragListener : ${event.action}."
                )
                return false
            }
        }
    }

}