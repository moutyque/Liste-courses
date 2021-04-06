package small.app.liste_courses.adapters.listenners

import android.util.Log
import small.app.liste_courses.adapters.ItemsAdapter
import small.app.liste_courses.adapters.ObjectChange
import small.app.liste_courses.model.MainViewModel
import small.app.liste_courses.room.entities.Item

class UnclassifiedItemChangeListener (val model : MainViewModel,var itemsAdapter: ItemsAdapter) : IOnAdapterChangeListener<Item, ItemsAdapter, ItemsAdapter.ItemsViewHolder> {


    override fun onObjectCreated(a: Item) {
        model.updateItemsList(a)
    }

    override fun onItemUpdate(a: Item, position: Int, list: MutableList<Item>,code: ObjectChange) {
        when (code) {
            ObjectChange.USED -> {
                if (a.isClassified) {
                    model.updateView(a) //Full refresh cause we also need to refresh the department
                } else {//New item added to the unclassified list
                    model.updateItem(a)
                    getAdapter().notifyItemChanged(position)
                }
            }
            ObjectChange.CLASSIFIED -> {
                Log.d("Classify","Item : ${a.name}")
                model.updateView(a) // Need to refresh the two lists
            }
            ObjectChange.QTY -> {
                model.updateItem(a)
                getAdapter().notifyItemChanged(position)
            }

            else -> Log.e("ItemChange", "Unknown code has been send : $code")
        }
        //model.updateView(a)
    }


    override fun onItemDelete(a: Item) {
    }

    override fun setAdapter(adapter: ItemsAdapter) {
        itemsAdapter = adapter
    }

    override fun getAdapter(): ItemsAdapter {
        return itemsAdapter
    }
}