package small.app.liste_courses

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragAndDropHelper :  ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
    override fun onMove(
        recyclerView: RecyclerView,
        dragged: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val draggedPosition = dragged.adapterPosition
        val targetPosition = target.adapterPosition

        Log.d("DD","draggedPosition : ${draggedPosition}")
        Log.d("DD","targetPosition : ${targetPosition}")

        return false // true if moved, false otherwise
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }
}