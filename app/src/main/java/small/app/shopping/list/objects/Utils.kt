package small.app.shopping.list.objects

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.coroutines.launch
import small.app.shopping.list.models.Department
import small.app.shopping.list.objects.Scope.backgroundScope
import small.app.shopping.list.objects.Scope.mainScope
import small.app.shopping.list.room.Repository
import small.app.shopping.list.room.entities.DepartmentWithItems
import small.app.shopping.list.room.entities.Item
import java.util.*


object Utils {


    lateinit var repo: Repository


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

    fun useItem(
        item: Item
    ) {
        var dbItem: Item? = null
        val job = backgroundScope.launch {
            dbItem = repo.findItem(item.name)
        }
        job.invokeOnCompletion {
            //If exist and classified
            if (dbItem != null && dbItem!!.isClassified) {
                useClassifiedItem(dbItem!!)
            } else {
                //Can exist and not use, for example default option
                saveItem(item)
                if (item.isClassified) {
                    backgroundScope.launch {
                        repo.findDepartment(item.departmentId)?.let {
                            updateOrder(it)
                        }

                    }
                }
                //Remove the item from the unclassified auto complete listpm
            }
        }


    }

    private fun useClassifiedItem(item: Item) {
        //Get the department from the item
        //Check if the department is already displayed through the department list
        //If no get the adapter and add it to the dep adapter
        //If yes add the item to the items list in the dep adapter => Update department and notify change on dep adapter

        backgroundScope.launch {
            item.isUsed = true
            repo.saveItem(item)
        }.invokeOnCompletion {
            backgroundScope.launch {
                val d = repo.findDepartment(item.departmentId)
                mainScope.launch {
                    d?.let {
                        it.isUsed = true
                        saveDepartment(it)
                    }
                }
            }


        }
    }

    fun saveDepartmentAndItem(item: Item, department: Department) {
        backgroundScope.launch {
            saveItem(item)
            repo.saveDepartment(department)
        }
    }


    fun saveItem(item: Item) {
        backgroundScope.launch {
            repo.saveItem(item)
        }
    }

    fun saveItems(vararg items: Item) {
        backgroundScope.launch {
            repo.saveItems(*items)
        }
    }

    fun saveDepartment(d: Department) {
        backgroundScope.launch {
            repo.saveDepartment(d)
        }
    }

    fun unuseItem(name: String) {
        backgroundScope.launch {
            repo.findItem(name)?.let {
                repo.unuseItem(it)
            }

        }
    }


    fun getFilteredDepartmentWithItems(it: List<DepartmentWithItems>?): MutableList<DepartmentWithItems> {
        val mlist = mutableListOf<DepartmentWithItems>()
        it?.forEach { dep ->
            val localDep = dep
            val localItems = mutableListOf<Item>()
            dep.items.forEach { item ->
                if (item.isUsed) localItems.add(item)
            }
            localDep.items = localItems
            mlist.add(localDep)
        }
        return mlist
    }


    /**
     * Delete an item and decrease order attribute of all the following items in the list by one
     * Then find the link department and decrease items count by one
     */
    fun deleteItem(items: List<Item>, position: Int) {

        backgroundScope.launch {
            val dep = repo.findDepartment(items[position].departmentId)
            if (position < items.size - 1) {
                val subList = items.subList(position + 1, items.size)
                subList.forEach { it.order-- }
                repo.saveItems(*subList.toTypedArray())
            }
            repo.deleteItem(items[position])

            dep!!.itemsCount--
            repo.saveDepartment(dep)


        }


    }

    fun deleteDepartment(department: Department) {
        backgroundScope.launch {
            repo.deleteDepartment(department)
        }
    }

    private fun classifyItemWithOrder(depId: String, item: Item) {
        backgroundScope.launch {
            repo.getDepartment(depId)?.classifyWithOrderDefined(item)
        }
    }

    fun updateOrder(department: Department) {
        backgroundScope.launch {
            repo.updateItemsOrderInDepartment(department.name)
        }

    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager: InputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken,
                0
            )
        }
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun classifyDropItem(droppedItemName: String, targetedItem: Item) {
        backgroundScope.launch {
            repo.findItem(droppedItemName)?.let {
                if (targetedItem.departmentId != it.departmentId) {
                    it.order = targetedItem.order
                    classifyItemWithOrder(depId = targetedItem.departmentId, item = it)
                }
            }
        }
    }

    fun classifyDropItem(droppedItemName: String, department: Department) {
        backgroundScope.launch {
            repo.findItem(droppedItemName)?.let {
                if (department.name != it.departmentId) {
                    department.classify(it)
                }
            }
        }
    }

}