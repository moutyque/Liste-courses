package small.app.shopping.list.room

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import small.app.shopping.list.models.Department
import small.app.shopping.list.room.entities.DepartmentWithItems
import small.app.shopping.list.room.entities.Item

class Repository(context: Context) {
    private val db = getInstance(context)

    fun getNumberOfDepartments(): Int {
        return db.departmentDAO().getNbDep()
    }

    fun getAllDepartments(): LiveData<List<DepartmentWithItems>?> {

        return db.departmentDAO().getAllDepartment()

    }

    fun saveDepartment(d: Department) {
        db.departmentDAO()
            .insertAll(
                small.app.shopping.list.room.entities.Department(
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

    fun saveItems(vararg items: Item) {
        db.itemDAO().insertAll(*items)
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
        updateDepartmentUsage(item)

    }

    private fun updateDepartmentUsage(item: Item) {
        if (db.itemDAO().findUsedByDepName(item.departmentId).isNullOrEmpty()) {
            val findByName = db.departmentDAO().findByName(item.departmentId)
            findByName?.apply {
                isUsed = false
                db.departmentDAO().insertAll(this)
            }

        }
    }

    private fun small.app.shopping.list.room.entities.Department.toDepartment(): Department {
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

    fun deleteItem(item: Item) {
        db.itemDAO().delete(item)
        updateDepartmentUsage(item)
    }

    fun deleteDepartment(department: Department) {

        val dep = small.app.shopping.list.room.entities.Department(
            name = department.name,
            isUsed = department.isUsed,
            itemsCount = department.itemsCount,
            order = department.order
        )
        db.departmentDAO().delete(dep)

        department.items.forEach {
            it.order = System.currentTimeMillis()
            it.isClassified = false
            it.departmentId = ""
            db.itemDAO().insertAll(it)

        }


    }


}