package small.app.liste_courses.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import small.app.liste_courses.models.Department
import small.app.liste_courses.objects.Utils
import small.app.liste_courses.room.entities.DepartmentWithItems
import small.app.liste_courses.room.entities.Item

class FragmentViewModel(application: Application) : AndroidViewModel(application) {


    fun getUnclassifiedItems(): LiveData<List<Item>?> {
        return Utils.repo.getUnclassifiedItem()
    }

    fun getUnusedDepartments(): LiveData<List<Department>> {
        return Utils.repo.getUnusedDepartments()
    }


    fun getUsedDepartment(): LiveData<List<DepartmentWithItems>?> {
        return Utils.repo.getUsedDepartment()
    }

    fun getDepartmentItems(depName: String): LiveData<List<Item>?> {
        return Utils.repo.getDepartmentItems(depName)
    }


}