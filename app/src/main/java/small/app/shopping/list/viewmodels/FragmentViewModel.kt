package small.app.shopping.list.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.room.entities.DepartmentWithItems
import small.app.shopping.list.room.entities.Item

class FragmentViewModel(application: Application) : AndroidViewModel(application) {


    fun getUnclassifiedItems(): LiveData<List<Item>?> {
        return Utils.repo.getUnclassifiedItem()
    }

    fun getUnusedDepartmentsName(): LiveData<List<String>> {
        return Utils.repo.getUnusedDepartmentsName()
    }


    fun getUsedDepartment(): LiveData<List<DepartmentWithItems>?> {
        return Utils.repo.getUsedDepartment()
    }

    fun getUnusedItemsName(): LiveData<List<String>> {
        return Utils.repo.getUnusedItemsName()
    }

    fun getUnusedItemsNameInDepartment(name: String): LiveData<List<String>> {
        return Utils.repo.getUnusedDepartmentItems(name)
    }

    fun getAllDepartments(): LiveData<List<DepartmentWithItems>?> {
        return Utils.repo.getAllDepartments()
    }

    fun addItem(itemName: String) {
        if (itemName.isNotEmpty()) {
            val item = Item(name = itemName)
            item.isUsed = true
            item.order = System.currentTimeMillis()
            Utils.useItem(item)
        }
    }

    fun addItem(itemName: String, depName: String) {
        if (itemName.isNotEmpty()) {
            val item = Item(name = itemName)
            item.isUsed = true

            item.order = System.currentTimeMillis()
            if (depName.isNotEmpty()) {
                item.departmentId = depName
                item.isClassified = true
            }
            Utils.useItem(item)
        }
    }


}