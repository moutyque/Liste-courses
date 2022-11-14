package small.app.shopping.list.room

import android.util.Log
import androidx.lifecycle.LiveData
import small.app.shopping.list.models.Department
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.room.entities.DepartmentWithItems
import small.app.shopping.list.room.entities.Item
import small.app.shopping.list.room.entities.Store
import small.app.shopping.list.room.entities.Department as DepartmentEntity

class Repository(private val db: AppDatabase) {

    fun getNumberOfDepartments(): Int =
        db.departmentDAO().getNbDep()


    fun getAllDepartments(): LiveData<List<DepartmentWithItems>?> =
        db.departmentDAO().fetchAllDepartment()


    fun getAllItems(): List<Item> =
        db.itemDAO().getAll()


    fun getAllRawDepartments(): List<DepartmentEntity> =
        db.departmentDAO().getAll()


    fun saveDepartment(d: Department) =
        db.departmentDAO()
            .insertAll(
                with(d) {
                    DepartmentEntity(
                        "${name}_$storeId",
                        name,
                        isUsed,
                        itemsCount,
                        order,
                        storeId
                    )
                }
            )

    fun DepartmentEntity.save() =
        db.departmentDAO()
            .insertAll(
                this
            )

    fun saveItem(i: Item) =
        db.itemDAO().insertAll(i)


    fun saveItems(vararg items: Item) =
        db.itemDAO().insertAll(*items)


    fun getUnusedDepartmentsName(): LiveData<List<String>> =
        db.departmentDAO().fetchUnusedDepartmentsName()


    fun findItem(itemName: String, storeName: String): Item? =
        db.itemDAO().findByName(itemName, storeName)


    fun findDepartment(name: String): Department? =
        db.departmentDAO().getByName(name)?.toDepartment()


    fun fetchUsedDepartment(): LiveData<List<DepartmentWithItems>?> =
        db.departmentDAO().fetchUsedDepartments()


    fun unuseItem(item: Item) {
        item.isUsed = false
        db.itemDAO().insertAll(item)
        updateDepartmentUsage(item.departmentId)
    }

    private fun updateDepartmentUsage(depName: String) {
        if (db.itemDAO().getUsedDepItems(depName).isEmpty()) {
            db.departmentDAO().getByIds(depName)?.apply {
                isUsed = false
                db.departmentDAO().insertAll(this)
            }
        }
    }

    private fun DepartmentEntity.toDepartment(): Department {
        val dep = db.departmentDAO().getItemsFromDepartment(this.name, this.storeId)
        dep?.apply {
            Log.d(Utils.TAG, "In department $name there is ${dep.items.count()} items")
            return Department(
                id = "${dep.department.name}_${dep.department.storeId}",
                name = dep.department.name,
                isUsed = dep.department.isUsed,
                items = dep.items.toMutableList(),
                itemsCount = itemsCount,
                order = dep.department.order,
                storeId = dep.department.storeId
            )
        }
        throw Exception("Unknown department name ${this.name}")

    }

    fun getUnusedItemsName(): LiveData<List<String>> {
        return db.itemDAO().fetchUnusedItemsName()

    }

    fun getUnusedDepartmentItems(name: String) =
        db.itemDAO().fetchUnusedDepItems(name)

    fun getDepartment(departmentId: String) =
        db.departmentDAO().getByName(departmentId)?.toDepartment()

    fun deleteItem(item: Item) {
        db.itemDAO().delete(item)
        item.apply {
            updateDepartmentUsage(departmentId)
            updateItemsOrderInDepartment(departmentId)
        }
    }

    fun updateItemsOrderInDepartment(departmentId: String) {
        if (db.itemDAO().getDepItems(departmentId).isEmpty()) {
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
                saveItems(*sortedItems.toTypedArray())
            }

        }
    }

    fun deleteDepartment(department: Department) = db.departmentDAO().delete(department.toEntity())


    fun fetchStoreNames() = db.storeDao().fetchNames()

    fun fetchStores() = db.storeDao().fetchAll()

    fun getUsedStore() = db.storeDao().getUsedStore()

    fun fetchUsedStore() = db.storeDao().fetchUsedStore()
    fun saveStore(store: Store) {
        db.storeDao().insertAll(store)

    }

    fun getAllStores(): List<Store> = db.storeDao().getAll()
    fun getStore(name: String): Store? = db.storeDao().getStore(name)
    fun deleteStore(store: Store) = db.storeDao().delete(store)
    fun getStoreDepartments(store: Store) = db.departmentDAO().getStoreDepartments(store.name)
    fun saveDepartments(vararg department: DepartmentEntity) =
        db.departmentDAO().insertAll(*department)

    fun saveStores(vararg stores: Store) = db.storeDao().insertAll(*stores)
    fun fetchDepartments(storeId: String): LiveData<List<DepartmentWithItems>?> = db.departmentDAO().fetchStoreDepartment(storeId)
}

private fun Department.toEntity() =
    DepartmentEntity(
        id = "${name}_$storeId",
        name = name,
        isUsed = isUsed,
        itemsCount = itemsCount,
        order = order,
        storeId = storeId
    )

