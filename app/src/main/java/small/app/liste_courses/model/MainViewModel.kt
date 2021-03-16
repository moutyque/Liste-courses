package small.app.liste_courses.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import small.app.liste_courses.room.Repository
import small.app.liste_courses.model.Department
import small.app.liste_courses.room.entities.Item

class MainViewModel(val repo : Repository)  : ViewModel() {

    private val backgroundScope = CoroutineScope(Job() + Dispatchers.IO)
    private val mainScope = CoroutineScope(Job() + Dispatchers.Main)

    var autoCompleteItems = MutableLiveData<List<Item>>()

    var unclassifiedItems: ArrayList<Item> = ArrayList()

    var departments:ArrayList<Department> = ArrayList()

    val newItems = MutableLiveData(false)

    val newDepartment = MutableLiveData(false)

    /**
     * Remove an item from autoComplete and perhaps add it to unclassifiedItem
     * //TODO Optimization can be done by checking if the item that we get from the list as already been classified
     */
    fun updateItemsList() {
        updateItemsList(null)
    }

    fun updateItemsList(item : Item?) {
        val job = backgroundScope.launch {
            if(item !=null) repo.saveItem(item)
            Log.d("MainFragment", "updateItemsList")

            //Update auto complete list
            val itemsList = repo.getUnusedItems()
            mainScope.launch { autoCompleteItems.value = itemsList }

            //Update unclassified item list
            unclassifiedItems.clear()
            unclassifiedItems.addAll(repo.getUnclassifiedItem())


            Log.d("MainFragment", "autoCompleteItems size ${itemsList.size}")
            Log.d("MainFragment", "unclassifiedItems size ${unclassifiedItems.size}")


        }
        job.invokeOnCompletion {
            mainScope.launch {
                newItems.value = true
            }

        }
    }
    fun updateDepartmentsList(){
        updateDepartmentsList(null)
    }

    fun updateDepartmentsList(department: Department?){
        val job = backgroundScope.launch {
            if(department!=null)Log.d("updateDep" , (!(repo.departmentExist(department.name))).toString())
            if(department!=null && (department.items.isEmpty() && !repo.departmentExist(department.name) || department.items.isNotEmpty())){
                    repo.saveDepartment(department)
            }

            departments.clear()
            departments.addAll(repo.getAllDepartment())
        }
        job.invokeOnCompletion {
            mainScope.launch {
                newDepartment.value = true
            }

        }

    }
}