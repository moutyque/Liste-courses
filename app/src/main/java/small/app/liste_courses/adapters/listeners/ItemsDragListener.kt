package small.app.liste_courses.adapters.listeners

import android.util.Log
import android.view.DragEvent
import android.view.View
import small.app.liste_courses.Utils
import small.app.liste_courses.adapters.ItemsAdapter
import small.app.liste_courses.model.DragItem

class ItemsDragListener(val itemsAdapter: ItemsAdapter ) : View.OnDragListener {
    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        // Handles each of the expected events
        when (event!!.action) {
            DragEvent.ACTION_DROP -> {
                Log.d("IAdapter", "Drop action")
                val dragObj = event.localState
                if (dragObj is DragItem) {
                    Utils.classifyItem(
                        dragObj.item,
                        source = dragObj.adapter,
                        target = itemsAdapter
                    )
                }
                return true
            }
            else -> {
                // An unknown action type was received.
                Log.e(
                    "IAdapter",
                    "Untreated action type received by OnDragListener : ${event.action}."
                )
                return true
            }
        }
    }
}