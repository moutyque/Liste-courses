package small.app.shopping.list.adapters

import android.content.Context
import android.view.View
import small.app.shopping.list.callback.IMovableAdapter
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.objects.Utils.saveAndUse
import small.app.shopping.list.room.entities.Item


class ItemsFullScreenAdapter(
    context: Context
) : ItemsAdapter(
    context

), IMovableAdapter {

    override fun fillView(holder: ItemsViewHolder, item: Item) {
        super.fillView(holder, item)
        binding.ivCheckItem.visibility = View.VISIBLE
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        //Manage the view of the drop down list of unit
        holder.binding.apply {
            llListView.visibility = View.VISIBLE
            llParamView.visibility = View.GONE
            ivDd.visibility = View.GONE
            llQtyModifiers.visibility = View.GONE
        }
        super.onBindViewHolder(holder, position)
    }


    override fun canMove(): Boolean {
        return canMove
    }

    override fun setMove(b: Boolean) {
        canMove = b
    }

    override fun getAdapterList(): List<Any> {
        return list.toList()
    }

    override fun onDragEnd() {
        list.saveAndUse()
    }


    override fun onItemMove(initialPosition: Int, targetPosition: Int) {
        if (initialPosition > -1 && targetPosition > -1) {
            //This is call at every move so the save must only occure once the drag is done
            with(list) {
                val init = get(initialPosition)
                val target = get(targetPosition)
                val tmp = init.order
                init.order = target.order
                target.order = tmp
                Utils.swapInCollection(list, initialPosition, targetPosition)
            }
            this.notifyItemMoved(initialPosition, targetPosition)
        }
    }


}