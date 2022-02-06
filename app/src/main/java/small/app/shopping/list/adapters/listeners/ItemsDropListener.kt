package small.app.shopping.list.adapters.listeners

import android.util.Log
import android.view.DragEvent
import android.view.View
import small.app.shopping.list.models.Department
import small.app.shopping.list.objects.Utils

class ItemsDropListener(private val dep: Department) :
    View.OnDragListener {
    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        // Handles each of the expected events
        when (event!!.action) {
            DragEvent.ACTION_DROP -> {
                Log.d(Utils.TAG, "Has drop $v")
                val droppedItem = event.localState
                if (droppedItem is String) {
                    Utils.classifyDropItem(droppedItem, dep)
                    return true
                }
                return true
            }
            else -> {
                // An unknown action type was received.
                Log.e(
                    Utils.TAG,
                    "Untreated action type received by OnDragListener : ${event.action}."
                )
                return true
            }
        }
    }

}