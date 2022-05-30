package small.app.shopping.list.room

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import small.app.shopping.list.R
import small.app.shopping.list.models.Department
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.room.entities.DepartmentWithItems
import small.app.shopping.list.room.entities.Item

class Repository(private val context: Context) {
    private val db = getInstance(context)

    fun getNumberOfDepartments(): Int {
        return db.departmentDAO().getNbDep()
    }

    fun getAllDepartments(): LiveData<List<DepartmentWithItems>?> {
        return db.departmentDAO().getAllDepartment()
    }

    fun getAllItems(): List<Item> {
        return db.itemDAO().getAll()
    }

    fun getAllRawDepartments(): List<small.app.shopping.list.room.entities.Department> {
        return db.departmentDAO().getAll()
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

    fun saveDepartment(d: small.app.shopping.list.room.entities.Department) {
        db.departmentDAO()
            .insertAll(
                d
            )
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

    fun getUnusedDepartmentsName(): LiveData<List<String>> {
        return db.departmentDAO().getUnusedDepartmentsName()
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
        updateDepartmentUsage(item.departmentId)

    }

    private fun updateDepartmentUsage(depName: String) {
        //TODO : update this ugliness
        val name = context.getString(R.string.default_category_name)
        if (depName == name) return
        if (db.itemDAO().fetchAssociatedUsedItems(depName).isEmpty()) {
            val findByName = db.departmentDAO().findByName(depName)
            findByName?.apply {
                isUsed = false
                db.departmentDAO().insertAll(this)
            }

        }
    }

    private fun small.app.shopping.list.room.entities.Department.toDepartment(): Department {
        val dep = db.departmentDAO().getItemsFromDepartment(this.name)
        dep?.apply {
            Log.d(Utils.TAG, "In department $name there is ${dep.items.count()} items")
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

    fun getUnusedDepartmentItems(name: String): LiveData<List<String>> {
        return db.itemDAO().findUnusedItemsNameByDepName(name)
    }

    fun getDepartment(departmentId: String): Department? {
        return db.departmentDAO().findByName(departmentId)?.toDepartment()
    }

    fun deleteItem(item: Item) {
        db.itemDAO().delete(item)
        with(item.departmentId) {
            updateDepartmentUsage(this)
            updateItemsOrderInDepartment(this)
        }
    }

    fun updateItemsOrderInDepartment(departmentId: String) {
        if (db.itemDAO().findByDepName(departmentId).value.isNullOrEmpty()) {
            val findByName = findDepartment(departmentId)
            findByName?.let {
                var order: Long = 1
                val sortedItems = it.items.sortedWith { i1, i2 ->
                    i1.order.compareTo(i2.order)
                }
                val listIterator = sortedItems.listIterator()
                while (listIterator.hasNext()) {
                    val item = listIterator.next()
                    item.order = order
                    order++
                }
                Utils.repo.saveItems(*sortedItems.toTypedArray())
            }

        }
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