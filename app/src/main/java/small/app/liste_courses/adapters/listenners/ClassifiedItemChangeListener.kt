package small.app.liste_courses.adapters.listenners

import android.util.Log
import androidx.lifecycle.ViewModel
import small.app.liste_courses.adapters.ItemsAdapter
import small.app.liste_courses.adapters.ObjectChange
import small.app.liste_courses.model.MainViewModel
import small.app.liste_courses.room.entities.Item

class ClassifiedItemChangeListener(val model: MainViewModel) :
    IOnAdapterChangeListener<Item, ItemsAdapter, ItemsAdapter.ItemsViewHolder> {

    lateinit var depItemsAdapter: ItemsAdapter

    override fun onObjectCreated(a: Item) {
        model.updateItemsList(a)
    }

    override fun onObjectUpdate(
        a: Item,
        position: Int,
        list: MutableList<Item>,
        code: ObjectChange
    ) {
        Log.d("classifiedItemChange", "item list size : ${getAdapter().getList().size}")
        when (code) {

            ObjectChange.CLASSIFIED -> {
                model.updateView(a) // Need to refresh the two lists
                getAdapter().notifyDataSetChanged()
            }
            ObjectChange.QTY -> {
                Log.d("classifiedItemChange", "Qty change on ${a.name} , new qty ${a.qty}")
                model.updateItem(a)
                val get = list[position]
                if (get != a) {
                    list.remove(get)
                    list.add(position, a)
                }
                getAdapter().notifyDataSetChanged()
                //getAdapter().notifyItemChanged(position)

            }
            ObjectChange.USED -> {
                //Validate inside a department
                model.updateItem(a)
                //model.updateDepartmentsList()//TODO : ok mais perfo ?
                //getAdapter().getList().removeAt(position)
                //getAdapter().notifyDataSetChanged()
            }
            else -> Log.e("ItemChange", "Unknown or unused code has been send : $code")
        }
        //model.updateView(a)
    }


    override fun onObjectDelete(a: Item) {
    }

    override fun setAdapter(adapter: ItemsAdapter) {
        depItemsAdapter = adapter
    }

    override fun getAdapter(): ItemsAdapter {
        return depItemsAdapter
    }

    override fun getModel(): ViewModel {
        return model
    }
}