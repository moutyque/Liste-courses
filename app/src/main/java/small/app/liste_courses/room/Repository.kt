package small.app.liste_courses.room

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import small.app.liste_courses.models.Department
import small.app.liste_courses.room.entities.DepartmentWithItems
import small.app.liste_courses.room.entities.Item

class Repository(context: Context) {
    private val db = getInstance(context)

    fun getNumberOfDepartments() : Int{
        return db.departmentDAO().getNbDep()
    }

    fun getAllDepartments(): LiveData<List<DepartmentWithItems>?> {

        return  db.departmentDAO().getAllDepartment()

    }

    fun saveDepartment(d: Department) {
        db.departmentDAO()
            .insertAll(
                small.app.liste_courses.room.entities.Department(
                    d.name,
                    d.isUsed,
                    d.itemsCount,
                    d.order
                )
            )
    }

    fun getUnusedItems(): LiveData<List<Item>?> {
        return db.itemDAO().getAllWithUsage(false)
    }

    fun getUnclassifiedItem(): LiveData<List<Item>?> {
        return db.itemDAO().getAllWithUsageAndClassification(isUsed = true, isClassified = false)
    }

    fun saveItem(i: Item) {
        db.itemDAO().insertAll(i)
    }


    fun getUnusedDepartments(): LiveData<List<Department>> {
        return MutableLiveData(
            db.departmentDAO().getUnusedDepartment().value.orEmpty().map {
                it.toDepartment()
            })
    }

    fun getUnusedDepartmentsName(): LiveData<List<String>> {
        return db.departmentDAO().getUnusedDepartmentsName()
    }

    fun getDepartmentItems(depName: String): LiveData<List<Item>?> {
        return db.itemDAO().findByDepName(depName)
    }

    fun findItem(name: String): Item? {
        return db.itemDAO().findByName(name)
    }

    fun findDepartment(name: String): Department? {
        val findByName = db.departmentDAO().findByName(name)
        return findByName?.toDepartment()
    }


    fun getUsedDepartment(): LiveData<List<DepartmentWithItems>?> {
        return db.departmentDAO().getUsedDepartment()
    }


    fun unuseItem(item: Item) {
        item.isUsed = false
        db.itemDAO().insertAll(item)
        if (db.itemDAO().findUsedByDepName(item.departmentId).isNullOrEmpty()) {
            val findByName = db.departmentDAO().findByName(item.departmentId)
            findByName?.apply {
                isUsed = false
                db.departmentDAO().insertAll(this)
            }

        }

    }

    private fun small.app.liste_courses.room.entities.Department.toDepartment(): Department {
        val dep = db.departmentDAO().getItemsFromDepartment(this.name)
        dep?.apply {
            Log.d("Repository", "In department $name there is ${dep.items.count()} items")
            return Department(
                name = dep.department.name,
                isUsed = dep.department.isUsed,
                items = dep.items.toMutableList(),
                itemsCount = itemsCount,
                order = dep.department.order
            )
        }
        throw Exception("Unknown department name ${this.name}")

    }

    fun getUnusedItemsName(): LiveData<List<String>> {
        return db.itemDAO().getUnusedItemsName()

    }





}