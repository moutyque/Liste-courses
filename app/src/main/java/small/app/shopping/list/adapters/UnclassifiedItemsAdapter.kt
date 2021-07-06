package small.app.shopping.list.adapters

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.item_grossery_item.view.*
import small.app.shopping.list.room.entities.Item

//TODO : add d&d
class UnclassifiedItemsAdapter(
    context: Context
) : ItemsAdapter(
    context
) {

    override fun fillView(holder: ItemsViewHolder, item: Item) {
        super.fillView(holder, item)

        holder.itemView.iv_check_item.visibility = View.GONE
    }
}
