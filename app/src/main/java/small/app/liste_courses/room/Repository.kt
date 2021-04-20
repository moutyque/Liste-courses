package small.app.liste_courses.room

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import small.app.liste_courses.models.Department
import small.app.liste_courses.room.entities.DepartmentWithItems
import small.app.liste_courses.room.entities.Item
import java.util.*
import kotlin.collections.ArrayList

class Repository(context: Context) {
    private val db = getInstance(context)

    fun getAllDepartment(): List<Department> {

        val list = ArrayList<Department>()
        val all = db.departmentDAO().getAll()
        Log.d("Repository", "There is ${all.size} departments ")
        for (d in all) {
            list.add(d.toDepartment())
        }
        list.sortBy { d -> d.order }
        return list.toList()
    }

    fun saveDepartment(d: Department) {
        db.departmentDAO()
            .insertAll(
                small.app.liste_courses.room.entities.Department(
                    d.name,
                    d.isUsed,
                    d.order
                )
            )
        for (i in 0 until d.items.size) {
            saveItem(d.items[i])
        }


    }

    fun getUnusedItems(): List<Item> {
        return db.itemDAO().getAllWithUsage(false)
    }

    fun getUnclassifiedItem(): LiveData<List<Item>> {
        return db.itemDAO().getAllWithUsageAndClassification(isUsed = true, isClassified = false)
    }

    fun getUnusedDepartments(): List<DepartmentWithItems> {
        return db.departmentDAO().getUnusedDepartment()
    }

    fun saveItem(i: Item) {
        db.itemDAO().insertAll(i)
    }


    private fun small.app.liste_courses.room.entities.Department.toDepartment(): Department {
        val dep = db.departmentDAO().getItemsFromDepartment(this.name)
        Log.d("Repository", "In department $name there is ${dep.items.count()} items")
        return Department(
            name = dep.department.name,
            isUsed = dep.department.isUsed,
            items = dep.items.toMutableList(),
            order = dep.department.order
        )
    }

    fun findItem(name: String): Item? {
        return db.itemDAO().findByName(name)
    }

    fun findDepartment(name: String): Department? {
        val findByName = db.departmentDAO().findByName(name)
        return findByName?.toDepartment()
    }

    fun getUsedDepartment(): List<DepartmentWithItems> {
        return db.departmentDAO().getUsedDepartment()
    }

    fun getDepartments():List<DepartmentWithItems>{
        return db.departmentDAO().getDepartments()
    }

}