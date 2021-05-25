package small.app.liste_courses.objects

import kotlinx.coroutines.launch
import small.app.liste_courses.adapters.DepartmentsAdapter
import small.app.liste_courses.models.Department
import small.app.liste_courses.objects.Scope.backgroundScope
import small.app.liste_courses.objects.Scope.mainScope
import small.app.liste_courses.room.Repository
import small.app.liste_courses.room.entities.DepartmentWithItems
import small.app.liste_courses.room.entities.Item

object Utils {


    lateinit var repo: Repository


    fun useItem(
        item: Item,
        departmentsAdapter: DepartmentsAdapter
    ) {
        var dbItem: Item? = null
        val job = backgroundScope.launch {
            dbItem = repo.findItem(item.name)
        }
        job.invokeOnCompletion {
            //If exist and classified
            if (dbItem != null && dbItem!!.isClassified) {
                useClassifiedItem(dbItem!!, departmentsAdapter)
            } else {
                //Can exist and not use, for example default option
                saveItem(item)
                //Remove the item from the unclassified auto complete listpm
            }
        }


    }

    private fun useClassifiedItem(item: Item, departmentsAdapter: DepartmentsAdapter) {
        //Get the department from the item
        //Check if the department is already displayed through the department list
        //If no get the adapter and add it to the dep adapter
        //If yes add the item to the items list in the dep adapter => Update department and notify change on dep adapter

        backgroundScope.launch {
            item.isUsed = true
            repo.saveItem(item)
        }.invokeOnCompletion {
            val d = repo.findDepartment(item.departmentId)
            mainScope.launch {
                if (d != null) {
                    val dep = d
                    if (departmentsAdapter.list.find { it.name == dep.name } == null) {
                        dep.isUsed = true
                        saveDepartment(dep)
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

    fun saveDepartment(d: Department) {
        backgroundScope.launch {
            repo.saveDepartment(d)
        }
    }

    fun unuseItem(item: Item) {
        backgroundScope.launch {
            repo.unuseItem(item)
        }
    }


    fun getFilteredDepartmentWithItems(it: List<DepartmentWithItems>?): MutableList<DepartmentWithItems> {
        val mlist = mutableListOf<DepartmentWithItems>()
        it?.forEach { dep ->
            val local_dep = dep
            val local_items = mutableListOf<Item>()
            dep.items.forEach { item ->
                if (item.isUsed) local_items.add(item)
            }
            local_dep.items = local_items
            mlist.add(local_dep)
        }
        return mlist
    }
}