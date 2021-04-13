package small.app.liste_courses.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import kotlinx.coroutines.launch
import small.app.liste_courses.Scope.backgroundScope
import small.app.liste_courses.Scope.mainScope
import small.app.liste_courses.adapters.DepartmentsAdapter
import small.app.liste_courses.fragments.MainFragment
import small.app.liste_courses.room.Repository
import small.app.liste_courses.room.entities.Item

class MainViewModel(private val repo: Repository, private val fragment: MainFragment) :
    ViewModel() {


    var autoCompleteItems = MutableLiveData<List<Item>>()
    var autoCompleteDepartment = MutableLiveData<List<Department>>()

    //Must be a list because it is passed to an
    //TODO : change for sorted list
   // var unclassifiedItems: SortedList<Item> = SortedList()

    //Must be a list because it is passed to an adapter


    val itemsChange = MutableLiveData(false)

    val departmentsChange = MutableLiveData(false)



    /**
     * Remove an item from autoComplete and perhaps add it to unclassifiedItem
     * //TODO Optimization can be done by checking if the item we get from the list as already been classified
     */
    /*
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
             val newUItemsList = repo.getUnclassifiedItem()

             //TODO: see if there is a better way to update the list by comapring the items and the only call the needed notifychange on hte rv
             unclassifiedItems.clear()
             unclassifiedItems.addAll(newUItemsList)

         }
         job.invokeOnCompletion {
             mainScope.launch {
                 autoCompleteItems.value = itemsList
                 itemsChange.value = true
             }

         }
     }

     fun updateDepartment(d: Department) {
         val job = backgroundScope.launch {
             repo.saveDepartment(d)
             d.items.forEach {
                 repo.saveItem(it)
             }
             departments.add(d)
         }

         job.invokeOnCompletion {

             mainScope.launch {
                 autoCompleteDepartment.value = departments.filter { d -> !d.isUsed }
                 departmentsChange.value = true
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
                 localNewItem = department.items.isNotEmpty()
                 updateDepartment(department)
             }

             departments.clear()
             departments.addAll(repo.getAllDepartment())
             updateItemsList()
         }
         job.invokeOnCompletion {

             mainScope.launch {
                 if (localNewItem) itemsChange.value = true
             }

         }

     }

     fun updateView(item: Item) {
         updateItemsList(item)
         updateDepartmentsList()
     }

     */

   /* fun useItem(item: Item) {
        /*
        Check if the item exist
        If not save it and add it to the unclassified item
         */
        val launch = backgroundScope.launch {
            repo.useItem(item)
        }
        launch.invokeOnCompletion {
            if (item.isClassified) {

            } else {
                unclassifiedItems.add(item)
                mainScope.launch {
                    fragment.updateUnclassifiedItems(unclassifiedItems.indexOf(item))
                }

            }
        }

    }*/

    /*fun addDepartment(name : String,departmentsAdapter: DepartmentsAdapter){
        var order = 0
        var depDB : Department? = null
        val job = backgroundScope.launch {
            order = repo.getAllDepartment().size
            depDB = repo.findDepartment(name)

        }
        job.invokeOnCompletion {
            if(depDB==null){
                depDB = Department(
                    name,
                    true,
                    ArrayList(),
                    order
                )
                backgroundScope.launch {
                    repo.saveDepartment(depDB!!)
                }
            }
        }
        depDB?.let { departmentsAdapter.addDepartment(it) }
        //Update the adapters



    }*/

}