package small.app.liste_courses.adapters

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import small.app.liste_courses.adapters.diffutils.ItemsDiffUtils
import small.app.liste_courses.adapters.listeners.IItemUsed
import small.app.liste_courses.room.entities.Item


class UnclassifiedItemsAdapter(
    context: Context, canChangeUnit: Boolean,
    itemUsed: IItemUsed
) : ItemsAdapter(
    context,
    canChangeUnit, itemUsed
) {

   /* init {

         backgroundScope.launch {
            list.addAll(Utils.repo.getUnclassifiedItem())
        }

    }
*/
    override fun updateList(list: List<Item>?) {
        if(list!=null){
            list.sortedBy { item ->  item.order}
            //this.list.beginBatchedUpdates()
            val diffResult = DiffUtil.calculateDiff(ItemsDiffUtils(this.list, list))
            this.list.clear()
            this.list.addAll(list)
            this.list.sortedBy { item ->  item.order}
            diffResult.dispatchUpdatesTo(this)
            //this.list.endBatchedUpdates()
        }

    }




}