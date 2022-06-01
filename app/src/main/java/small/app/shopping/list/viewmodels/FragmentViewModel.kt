package small.app.shopping.list.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.objects.Utils.useOrCreate
import small.app.shopping.list.room.entities.DepartmentWithItems
import small.app.shopping.list.room.entities.Item

class FragmentViewModel(application: Application) : AndroidViewModel(application) {


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


    fun addItem(itemName: String, depName: String) {
        if (itemName.isNotEmpty()) {
            Item(name = itemName).apply {
                isUsed = true
                order = System.currentTimeMillis()
                if (depName.isNotEmpty()) {
                    departmentId = depName
                }
                useOrCreate()
            }

        }
    }

    fun useItem(itemName: String) {
        Utils.useItem(itemName)
    }


}