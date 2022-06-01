package small.app.shopping.list.objects

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import small.app.shopping.list.models.Department
import small.app.shopping.list.objects.Scope.backgroundScope
import small.app.shopping.list.objects.Scope.mainScope
import small.app.shopping.list.room.Repository
import small.app.shopping.list.room.converters.DepartmentConverter
import small.app.shopping.list.room.converters.ItemConverter
import small.app.shopping.list.room.entities.DepartmentWithItems
import small.app.shopping.list.room.entities.Item
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

    //----------------------- ITEM EXTENSION METHODS -----------------------------
    fun useItem(itemName: String) {
        backgroundScope.launch {
            repo.findItem(itemName)?.useItem()
        }
    }

    fun Item.useOrCreate() {
        backgroundScope.launch {
            repo.findItem(this@useOrCreate.name)?.useItem() ?: save()
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

        backgroundScope.launch {
            this@useItem.isUsed = true
            repo.saveItem(this@useItem)
        }.invokeOnCompletion {
            backgroundScope.launch {
                val d = repo.findDepartment(this@useItem.departmentId)
                mainScope.launch {
                    d?.let {
                        it.isUsed = true
                        it.save()
                    }
                }
            }
        }
    }

    fun Item.save() {
        backgroundScope.launch {
            repo.saveItem(this@save)
        }
    }

    fun MutableList<Item>.save() {
        backgroundScope.launch {
            repo.saveItems(*this@save.toTypedArray())
        }
    }


    /**
     * Delete an item and decrease order attribute of all the following items in the list by one
     * Then find the link department and decrease items count by one
     */
    fun List<Item>.delete(position: Int) {
        backgroundScope.launch {
            val dep = repo.findDepartment(this@delete[position].departmentId)
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
            repo.getDepartment(depId)?.classifyWithOrderDefined(this@classifyItemWithOrder)
        }
    }

    fun Item.classifyDropItem(droppedItemName: String) {
        backgroundScope.launch {
            repo.findItem(droppedItemName)?.let {
                if (this@classifyDropItem.departmentId != it.departmentId) {
                    it.order = this@classifyDropItem.order
                    it.classifyItemWithOrder(this@classifyDropItem.departmentId)
                }
            }
        }
    }

    //----------------------- DEPARTMENT EXTENSION METHODS -----------------------------
    fun small.app.shopping.list.room.entities.Department.save() {
        backgroundScope.launch {
            this@save.save()
        }
    }

    fun Department.save() {
        backgroundScope.launch {
            repo.findItem(name)?.let {
                repo.saveItem(it)
            }
            repo.saveDepartment(this@save)
        }
    }

    private fun DepartmentWithItems.keeUsedItems() {
        this.items = this.items.filter { it.isUsed }
    }

    fun List<DepartmentWithItems>.keepWithUsedItem(): List<DepartmentWithItems> =
        this.onEach { it.keeUsedItems() }

    fun Department.delete() {
        backgroundScope.launch {
            repo.deleteDepartment(this@delete)
        }
    }


    fun Department.updateOrder() {
        backgroundScope.launch {
            repo.updateItemsOrderInDepartment(this@updateOrder.name)
        }
    }


    fun Department.classifyDropItem(droppedItemName: String) {
        backgroundScope.launch {
            repo.findItem(droppedItemName)?.let {
                if (this@classifyDropItem.name != it.departmentId) {
                    this@classifyDropItem.classify(it)
                }
            }
        }
    }


}

