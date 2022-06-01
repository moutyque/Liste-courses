package small.app.shopping.list.adapters

import android.content.Context
import android.view.View
import small.app.shopping.list.room.entities.Item

class ClassifiedUsedItemsAdapter(context: Context) : small.app.shopping.list.adapters.ItemsAdapter(context) {

    override fun fillView(holder: ItemsViewHolder, item: Item) {
        super.fillView(holder, item)
        holder.binding.apply {
            ivCheckItem.visibility = View.VISIBLE
            ivDd.visibility = View.GONE
        }

    }

}