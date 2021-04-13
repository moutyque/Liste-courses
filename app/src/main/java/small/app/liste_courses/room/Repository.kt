package small.app.liste_courses.room

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.SortedList
import small.app.liste_courses.adapters.sortedListAdapterCallback.DepartmentCallBack
import small.app.liste_courses.adapters.sortedListAdapterCallback.ItemCallBack
import small.app.liste_courses.model.Department
import small.app.liste_courses.room.entities.DepartmentWithItems
import small.app.liste_courses.room.entities.Item

class Repository(context: Context) {
    val db = getInstance(context)

    suspend fun getAllDepartment(): List<Department> {

        val list = ArrayList<Department>()
        val all = db.departmentDAO().getAll()
        Log.d("Repository", "There is ${all.size} departments ")
        for (d in all) {
            list.add(d.toDepartment())
        }
        list.sortBy { d -> d.order }
        return list.toList()
    }

    suspend fun saveDepartment(d: Department) {
        db.departmentDAO()
            .insertAll(
                small.app.liste_courses.room.entities.Department(
                    d.name,
                    d.isUsed,
                    d.order
                )
            )
       for(i in 0 until d.items.size){
           saveItem(d.items[i])
        }


    }

    suspend fun departmentExist(name: String): Boolean {
        val itemsFromDepartment = db.departmentDAO().getItemsFromDepartment(name)
        return itemsFromDepartment != null
    }


    suspend fun getUnusedItems(): List<Item> {
        return db.itemDAO().getAllWithUsage(false)
    }

    suspend fun getUsedItems(): List<Item> {
        return db.itemDAO().getAllWithUsage(true)
    }

    suspend fun getUnclassifiedItem(): List<Item> {
        return db.itemDAO().getAllWithUsageAndClassification(isUsed = true, isClassified = false)
    }

    suspend fun saveItem(i: Item) {
        db.itemDAO().insertAll(i)
    }

    suspend fun useItem(i: Item) {
        var savedItem = db.itemDAO().findByName(i.name)
        if (savedItem == null) {
            saveItem(i)
        } else {
            savedItem.isUsed = true
            saveItem(savedItem)
        }


    }

    suspend fun small.app.liste_courses.room.entities.Department.toDepartment(): Department {
        val dep = db.departmentDAO().getItemsFromDepartment(this.name)
        Log.d("Repository", "In department $name there is ${dep.items.count()} items")
        return Department(
            name = dep.department.name,
            isUsed = dep.department.isUsed,
            items = dep.items.toMutableList(),
            order = dep.department.order
        )
    }

    suspend fun findItem(name: String): Item? {
        return db.itemDAO().findByName(name)
    }

    suspend fun findDepartment(name: String): Department? {
        val findByName = db.departmentDAO().findByName(name)
        if (findByName == null) {
            return null
        }
        return findByName.toDepartment()
    }

    suspend fun getUsedDepartment(): List<DepartmentWithItems> {
        return db.departmentDAO().getUsedDepartment()
    }

    fun findDepartmentItems(depName: String) : List<Item>{
        return db.itemDAO().findByDepName(depName)
    }

}