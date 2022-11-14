package small.app.shopping.list.objects

import android.R
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import small.app.shopping.list.models.Department
import small.app.shopping.list.objects.Scope.backgroundScope
import small.app.shopping.list.room.Repository
import small.app.shopping.list.room.converters.DepartmentConverter
import small.app.shopping.list.room.converters.ItemConverter
import small.app.shopping.list.room.converters.StoreConverter
import small.app.shopping.list.room.entities.DepartmentWithItems
import small.app.shopping.list.room.entities.Item
import small.app.shopping.list.room.entities.Store
import small.app.shopping.list.viewmodels.FragmentViewModel
import java.util.*


object Utils {


    lateinit var repo: Repository
    const val TAG = "ListCourses"

    fun swapInCollection(list: List<Any>, fromPosition: Int, toPosition: Int) {
        if (fromPosition in 0 until toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(list, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(list, i, i - 1)
            }
        }
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun getAllItemsAsJson() =
        with(Dispatchers.IO) {
            val itemConverter = ItemConverter()
            repo.getAllItems().map { itemConverter.toJson(it) }
        }

    fun getAllDepartmentAsJson() = with(Dispatchers.IO) {
        val depConverter = DepartmentConverter()
        repo.getAllRawDepartments().map { depConverter.toJson(it) }
    }

    fun getAllStoreAsJson() = with(Dispatchers.IO) {
        val storeConverter = StoreConverter()
        repo.getAllStores().map { storeConverter.toJson(it) }
    }


    fun Fragment.setupNamesDD(source: LiveData<List<String>>): ArrayAdapter<String> {
        val names: ArrayList<String> = ArrayList()
        val namesAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.simple_dropdown_item_1line,
            names
        )
        source.observe(viewLifecycleOwner) {
            namesAdapter.clear()
            namesAdapter.addAll(it)
        }
        return namesAdapter
    }

    fun Spinner.setupStoreListener(
        viewModel: FragmentViewModel,
        viewLifecycleOwner: LifecycleOwner,
        adapter: ArrayAdapter<String>
    ) {
        this.adapter = adapter
        this.onItemSelectedListener = viewModel.storeSelectListener
        viewModel.getStores().observe(viewLifecycleOwner) { stores ->
            //Avoid setting an already set item
            if (this.selectedItem == null || this.selectedItem as String != stores.firstOrNull { it.isUsed }?.name) {
                this.setSelection(stores.indexOfFirst { it.isUsed })
            }
        }
    }

    //----------------------- ITEM EXTENSION METHODS -----------------------------

    fun Item.useOrCreate() {
        backgroundScope.launch {
            repo.findItem(this@useOrCreate.name,storeId)?.useItem() ?: saveAndUse()
        }
    }

    fun Item.unuse() {
        backgroundScope.launch {
            repo.unuseItem(this@unuse)
        }
    }

    private fun Item.useItem() {
        //Get the department from the item
        //Check if the department is already displayed through the department list
        //If no get the adapter and add it to the dep adapter
        //If yes add the item to the items list in the dep adapter => Update department and notify change on dep adapter
        this@useItem.isUsed = true
        var d: Department? = null
        backgroundScope.launch {
            d = repo.findDepartment(this@useItem.departmentId)
        }.invokeOnCompletion {
            d?.let {
                it.isUsed = true
                it.saveAndUse(this@useItem)
            }
        }
    }

    fun Item.saveAndUse() {
        backgroundScope.launch {
            repo.saveItem(this@saveAndUse)
        }
    }

    fun MutableList<Item>.saveAndUse() {
        backgroundScope.launch {
            repo.saveItems(*this@saveAndUse.toTypedArray())
        }
    }


    /**
     * Delete an item and decrease order attribute of all the following items in the list by one
     * Then find the link department and decrease items count by one
     */
    fun List<Item>.delete(position: Int) {
        backgroundScope.launch {
            val dep = repo.findDepartment(
                this@delete[position].departmentId
            )
            if (position < this@delete.size - 1) {
                val subList = this@delete.subList(position + 1, this@delete.size)
                subList.forEach { it.order-- }
                repo.saveItems(*subList.toTypedArray())
            }
            repo.deleteItem(this@delete[position])

            dep!!.itemsCount--
            repo.saveDepartment(dep)

        }
    }

    private fun Item.classifyItemWithOrder(depId: String) {
        backgroundScope.launch {
            repo.getDepartment(depId)
                ?.classifyWithOrderDefined(this@classifyItemWithOrder)
        }
    }

    fun Item.classifyDropItem(droppedItemName: String) {
        backgroundScope.launch {
            repo.findItem(droppedItemName,storeId)?.let {
                if (this@classifyDropItem.departmentId != it.departmentId) {
                    it.order = this@classifyDropItem.order
                    it.classifyItemWithOrder(this@classifyDropItem.departmentId)
                }
            }
        }
    }

    //----------------------- DEPARTMENT EXTENSION METHODS -----------------------------

    fun Department.saveAndUse(item: Item? = null) {
        backgroundScope.launch {
            item?.let { repo.saveItem(it) }
            repo.saveDepartment(this@saveAndUse)
        }
    }

    private fun DepartmentWithItems.keepUsedItems() {
        this.items = this.items.filter { it.isUsed }
    }

    fun List<DepartmentWithItems>.keepWithUsedItem(): List<DepartmentWithItems> =
        this.filter { it.department.isUsed }.onEach { it.keepUsedItems() }

    fun Department.delete() {
        backgroundScope.launch {
            this@delete.items.forEach { repo.deleteItem(it) }
        repo.deleteDepartment(this@delete)}
    }


    fun Department.updateOrder() {
        backgroundScope.launch {
            repo.updateItemsOrderInDepartment(this@updateOrder.name)
        }
    }


    fun Department.classifyDropItem(droppedItemName: String) {
        backgroundScope.launch {
            repo.findItem(droppedItemName, storeId)?.let {
                if (this@classifyDropItem.name != it.departmentId) {
                    this@classifyDropItem.classifyAndSave(it)
                }
            }
        }
    }

    //----------------------- STORE EXTENSION METHODS -----------------------------
//TODO: update selected store when updating available stores
    fun Store.save() {
        backgroundScope.launch {
            repo.saveStore(this@save)
        }
    }

    fun unusedStores() {
        backgroundScope.launch {
            repo.getAllStores().forEach {
                it.isUsed = false
                it.save()
            }
        }
    }

    fun useStore(name: String) {
        backgroundScope.launch {
            repo.getAllStores().forEach {
                it.isUsed = false
                it.save()
            }
            repo.getStore(name)?.run {
                isUsed = true
                save()
            }
        }
    }
}
inline fun <E: Any, T: Collection<E>> T?.withNotNullNorEmpty(func: T.() -> Unit): Unit {
    if (this != null && this.isNotEmpty()) {
        with (this) { func() }
    }
}

inline fun  <E: Any, T: Collection<E>> T?.whenNotNullNorEmpty(func: (T) -> Unit): Unit {
    if (this != null && this.isNotEmpty()) {
        func(this)
    }
}

inline fun <E: Any, T: Collection<E>> T?.withNullOrEmpty(func: () -> Unit): Unit {
    if (this == null || this.isEmpty()) {
        func()
    }
}
