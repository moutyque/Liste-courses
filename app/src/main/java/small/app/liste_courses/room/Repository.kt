package small.app.liste_courses.room

import android.content.Context
import small.app.liste_courses.model.Department
import small.app.liste_courses.room.entities.Item

class Repository(context: Context) {
    val db = getInstance(context)

    suspend fun getAllDepartment() : List<Department>{

        val list = ArrayList<Department>()
        val all = db.departmentDAO().getAll()
        for(d in all){
            list.add(getDepartmentByName(d.name))
        }
 return list.toList()
    }

    suspend fun saveDepartment(d : Department){
        db.departmentDAO().insertAll(small.app.liste_courses.room.entities.Department(d.name))
        db.itemDAO().insertAll(*d.items.toTypedArray())
    }

    suspend fun departmentExist(name : String): Boolean {
        val itemsFromDepartment = db.departmentDAO().getItemsFromDepartment(name)
        return  itemsFromDepartment != null
    }


    suspend fun getDepartmentByName(name : String) : Department{
        val dep = db.departmentDAO().getItemsFromDepartment(name)
        return Department(name = dep.department.name, items = dep.items)
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

}