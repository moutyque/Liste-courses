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

    fun getAllDepartments() : LiveData<List<DepartmentWithItems>?>{
        return  Utils.repo.getAllDepartments()
    }


}