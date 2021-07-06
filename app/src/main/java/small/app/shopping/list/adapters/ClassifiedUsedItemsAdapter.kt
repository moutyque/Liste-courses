package small.app.shopping.list.adapters

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.item_grossery_item.view.*
import small.app.shopping.list.room.entities.Item

class ClassifiedUsedItemsAdapter(context: Context) : small.app.shopping.list.adapters.ItemsAdapter(context) {

    override fun fillView(holder: small.app.shopping.list.adapters.ItemsAdapter.ItemsViewHolder, item: Item) {
        super.fillView(holder, item)

        holder.itemView.iv_check_item.visibility = View.VISIBLE
        holder.itemView.iv_dd.visibility = View.GONE
    }

}