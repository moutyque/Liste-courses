package small.app.liste_courses.adapters

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.item_grossery_item.view.*
import small.app.liste_courses.room.entities.Item

class ClassifiedUsedItemsAdapter(context: Context) : ItemsAdapter(context) {

    override fun fillView(holder: ItemsViewHolder, item: Item) {
        super.fillView(holder, item)

        holder.itemView.iv_check_item.visibility = View.VISIBLE

    }

}