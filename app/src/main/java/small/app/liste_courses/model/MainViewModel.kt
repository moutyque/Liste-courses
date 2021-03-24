package small.app.liste_courses.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import small.app.liste_courses.room.Repository
import small.app.liste_courses.room.entities.Item

class MainViewModel(val repo: Repository) : ViewModel() {

    private val backgroundScope = CoroutineScope(Job() + Dispatchers.IO)
    private val mainScope = CoroutineScope(Job() + Dispatchers.Main)

    var autoCompleteItems = MutableLiveData<List<Item>>()

    var unclassifiedItems: ArrayList<Item> = ArrayList()

    var departments: ArrayList<Department> = ArrayList()

    val newItems = MutableLiveData(false)

    val newDepartment = MutableLiveData(false)

    /**
     * Remove an item from autoComplete and perhaps add it to unclassifiedItem
     * //TODO Optimization can be done by checking if the item that we get from the list as already been classified
     */
    fun updateItemsList() {
        updateItemsList(null)
    }

    fun updateItemsList(item: Item?) {
        var itemsList: List<Item> = ArrayList()
        val job = backgroundScope.launch {
            if (item != null) repo.saveItem(item)
            Log.d("MainFragment", "updateItemsList")

            //Update auto complete list
            itemsList = repo.getUnusedItems()
            //Update unclassified item list
            unclassifiedItems.clear()
            unclassifiedItems.addAll(repo.getUnclassifiedItem())


            Log.d("MainFragment", "autoCompleteItems size ${itemsList.size}")
            Log.d("MainFragment", "unclassifiedItems size ${unclassifiedItems.size}")


        }
        job.invokeOnCompletion {
            mainScope.launch {
                autoCompleteItems.value = itemsList
                newItems.value = true
            }

        }
    }

    fun updateDepartmentsList() {
        updateDepartmentsList(null)
    }

    fun updateDepartmentsList(department: Department?) {
        var localNewItem = false
        val job = backgroundScope.launch {

            if (department != null && (department.items.isEmpty() && !repo.departmentExist(
                    department.name
                ) || department.items.isNotEmpty())
            ) {
                repo.saveDepartment(department)
                localNewItem = department.items.isNotEmpty()
                department.items.forEach {
                    repo.saveItem(it)
                }
            }
            departments.clear()
            departments.addAll(repo.getAllDepartment())
            updateItemsList()
        }
        job.invokeOnCompletion {
            mainScope.launch {
                newDepartment.value = true
                if (localNewItem) newItems.value = true
            }

        }

    }
}