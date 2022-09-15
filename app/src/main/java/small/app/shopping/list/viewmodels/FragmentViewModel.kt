package small.app.shopping.list.viewmodels

import android.app.Application
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.launch
import small.app.shopping.list.objects.Scope.backgroundScope
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.objects.Utils.delete
import small.app.shopping.list.objects.Utils.save
import small.app.shopping.list.objects.Utils.useOrCreate
import small.app.shopping.list.room.Repository
import small.app.shopping.list.room.entities.Department
import small.app.shopping.list.room.entities.DepartmentWithItems
import small.app.shopping.list.room.entities.Item
import small.app.shopping.list.room.entities.Store

class FragmentViewModel(application: Application, private val repo: Repository) : AndroidViewModel(application) {

    val storeSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            //TODO: Not sure if the result of Store and Store Names return same list in same order
            Utils.useStore(parent?.adapter?.getItem(position) as String)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

    }

    var selectedStore: Store? = null

    fun getStores(): LiveData<List<Store>> = repo.fetchStores()


    fun getUnusedDepartmentsName(): LiveData<List<String>> =repo.getUnusedDepartmentsName()


    fun getUsedDepartment(): LiveData<List<DepartmentWithItems>?> = repo.fetchUsedDepartment()


    fun getUnusedItemsNameInDepartment(name: String, storeName: String): LiveData<List<String>> = repo.getUnusedDepartmentItems(name,storeName)

    fun addItem(itemName: String, depName: String, storeName: String) {
        if (itemName.isNotEmpty()) {
            Item(name = itemName).apply {
                isUsed = true
                order = System.currentTimeMillis()
                if (depName.isNotEmpty()) {
                    departmentId = "${depName}_$storeName"
                }
                useOrCreate()
                storeId = storeName
            }

        }
    }

    fun addStore(storeName: String)=
        changeStore(Store(storeName, true))


    fun changeStore(store: Store) {
        Utils.unusedStores()
        store.isUsed = true
        store.save()
    }

    fun fetchStoreNames(): LiveData<List<String>> = repo.fetchStoreNames()

    fun fetchUsedStore() = repo.fetchUsedStore()

    fun deleteCurrentStore() {
        backgroundScope.launch {
            repo.getUsedStore()?.let {

                repo.getStoreDepartments(it).forEach { dep -> dep.toDepartment().delete() }
                repo.deleteStore(it)
            }
            repo.getAllStores().firstOrNull()
                ?.apply {
                    isUsed = true
                    save()
                }
        }
    }

    fun imports(stores: List<Store>, departments: List<Department>, items: List<Item>) {
            backgroundScope.launch {
                repo.saveStores(*stores.toTypedArray())
                repo.saveDepartments(*departments.toTypedArray())
                repo.saveItems(*items.toTypedArray())
            }
    }


}