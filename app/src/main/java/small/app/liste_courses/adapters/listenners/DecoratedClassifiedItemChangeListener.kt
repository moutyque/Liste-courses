package small.app.liste_courses.adapters.listenners

import androidx.lifecycle.ViewModel
import small.app.liste_courses.adapters.DepartmentsAdapter
import small.app.liste_courses.adapters.ItemsAdapter
import small.app.liste_courses.adapters.ObjectChange
import small.app.liste_courses.model.MainViewModel
import small.app.liste_courses.room.entities.Item

/**
 * Listener to hide the department if the last item is removed from the department
 */
class DecoratedClassifiedItemChangeListener(
    private val departmentsAdapter: DepartmentsAdapter,
    private val depPosition: Int,
    var itemChangeListener: IOnAdapterChangeListener<Item, ItemsAdapter, ItemsAdapter.ItemsViewHolder>
) :
    IOnAdapterChangeListener<Item, ItemsAdapter, ItemsAdapter.ItemsViewHolder> {
    override fun onObjectCreated(a: Item) {
        itemChangeListener.onObjectCreated(a)
    }

    override fun onObjectUpdate(
        a: Item,
        position: Int,
        list: MutableList<Item>,
        code: ObjectChange
    ) {
        itemChangeListener.onObjectUpdate(a, position, list, code)
        //All the items under the department have been used
        if (list.none { i -> i.isUsed }) {
            //Update in DB
            val dp = departmentsAdapter.getList()[depPosition]
            departmentsAdapter.getList().removeAt(depPosition)
            departmentsAdapter.notifyItemRemoved(depPosition)
           dp.apply {
                isUsed = false
                (itemChangeListener.getModel() as MainViewModel).updateDepartmentsList(this)
            }
        }

    }

    override fun onObjectDelete(a: Item) {
        itemChangeListener.onObjectDelete(a)
    }

    override fun setAdapter(adapter: ItemsAdapter) {
        itemChangeListener.setAdapter(adapter)

    }

    override fun getAdapter(): ItemsAdapter {
        return itemChangeListener.getAdapter()
    }

    override fun getModel(): ViewModel {
        return itemChangeListener.getModel()
    }
}