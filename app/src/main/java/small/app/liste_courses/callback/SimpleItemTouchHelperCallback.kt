package small.app.liste_courses.callback

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SimpleItemTouchHelperCallback(
    private val adapter: IMovableAdapter,
    private val dir: Direction
) :
    ItemTouchHelper.Callback() {

    enum class Direction {
        VERTICAL,
        HORIZONTAL
    }

    private var hasStarted = false

    override fun isLongPressDragEnabled(): Boolean {
        Log.d("SimpleItemTouchHelperCallback", "Can u click")
        return adapter.canMove()
    }


    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = if (dir == Direction.VERTICAL) {
            ItemTouchHelper.UP or ItemTouchHelper.DOWN
        } else {
            ItemTouchHelper.START or ItemTouchHelper.END
        }

        return makeMovementFlags(dragFlags, 0)
    }


    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        hasStarted = true
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        adapter.onItemMove(fromPosition, toPosition)

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        Log.d("DDSwipe", "In the onSwipe")
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.0f
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_DRAG -> {
                viewHolder?.itemView?.alpha = 0.5f
                adapter.setMove(false)
            }
            ItemTouchHelper.ACTION_STATE_IDLE -> {
                if (hasStarted) {
                    adapter.onDragEnd()
                    hasStarted = false
                }
            }
        }

    }


    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {

        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = 1.0f
    }


}
