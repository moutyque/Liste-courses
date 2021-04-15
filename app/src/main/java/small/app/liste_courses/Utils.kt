package small.app.liste_courses

import kotlinx.coroutines.launch
import small.app.liste_courses.Scope.backgroundScope
import small.app.liste_courses.Scope.mainScope
import small.app.liste_courses.adapters.DepartmentsAdapter
import small.app.liste_courses.adapters.ItemsAdapter
import small.app.liste_courses.model.Department
import small.app.liste_courses.room.Repository
import small.app.liste_courses.room.entities.Item

object Utils {

    lateinit var repo: Repository


    fun useItem(
        item: Item,
        unclassifiedItemsAdapter: ItemsAdapter,
        departmentsAdapter: DepartmentsAdapter
    ) {
        backgroundScope.launch {
            val dbItem = repo.findItem(item.name)
            //If exist and not classified
            if (dbItem != null && dbItem.isClassified) {
                useClassifiedItem(dbItem, departmentsAdapter)
            } else {
                //Can exist and not use, for example default option
                useUnclassifiedItem(item, unclassifiedItemsAdapter, dbItem != null)
                //Remove the item from the unclassified auto complete list
            }
        }
    }

    private fun useUnclassifiedItem(item: Item, itemsAdapter: ItemsAdapter, exist: Boolean) {
        backgroundScope.launch {
            if (!exist) repo.saveItem(item)
        }

        mainScope.launch {
            //New item
            itemsAdapter.add(item)
        }
    }

    private fun useClassifiedItem(item: Item, departmentsAdapter: DepartmentsAdapter) {
        //Get the department from the item
        //Check if the department is already displayed through the department list
        //If no get the adapter and add it to the dep adapter
        //If yes add the item to the items list in the dep adapter => Update department and notify change on dep adapter

        var d: Department? = null
        val job = backgroundScope.launch {
            item.isUsed = true
            repo.saveItem(item)
            d = repo.findDepartment(item.departmentId)
        }
        job.invokeOnCompletion {
            mainScope.launch {
                if (d != null) {
                    //Remove unuseItem
                    val dep = d!!
                    keepUsedItems(dep)

                    val index = departmentsAdapter.findIndex(dep)
                    if (index > -1) {//Department already displayed
                        departmentsAdapter.departments[index].items.add(item)
                        saveDepartment(departmentsAdapter.departments[index])
                        departmentsAdapter.notifyItemChanged(index)
                    } else {
                        dep.isUsed = true
                        dep.items.add(item)
                        departmentsAdapter.add(dep)
                        saveDepartment(dep)

                    }
                }
            }
        }
    }

    fun keepUsedItems(dep: Department) {
        val filter = dep.items.filter { it.isUsed }
        dep.items.clear()
        dep.items.addAll(filter)
    }

    fun classifyItem(item: Item, source: ItemsAdapter, target: ItemsAdapter) {

        source.remove(item)
        target.add(item)
        backgroundScope.launch {
            //Save the new item : change in department name and in order (maybe)
            repo.saveItem(item)
        }

    }


    fun unuseItem(item: Item, itemsAdapter: ItemsAdapter) {
        val job = backgroundScope.launch {
            repo.saveItem(item)
        }
        job.invokeOnCompletion {
            mainScope.launch {
                itemsAdapter.list.beginBatchedUpdates()
                itemsAdapter.remove(item)
                //Hide department if last item hidden
                if (itemsAdapter.list.size() == 0) {
                    itemsAdapter.itemUsed.onLastItemUse()
                } else {
                    itemsAdapter.itemUsed.onItemUse()
                }
                itemsAdapter.list.endBatchedUpdates()
            }

        }
    }


    fun saveItem(item: Item) {
        backgroundScope.launch {
            repo.saveItem(item)
        }
    }

    fun saveDepartment(d: Department) {
        backgroundScope.launch {
            repo.saveDepartment(d)

        }
    }
}