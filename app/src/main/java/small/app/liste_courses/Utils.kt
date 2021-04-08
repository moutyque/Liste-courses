package small.app.liste_courses

import kotlinx.coroutines.launch
import small.app.liste_courses.Scope.backgroundScope
import small.app.liste_courses.adapters.DepartmentsAdapter
import small.app.liste_courses.adapters.ItemsAdapter
import small.app.liste_courses.room.Repository
import small.app.liste_courses.room.entities.Item

class Utils {


    companion object {
        lateinit var repo: Repository


        fun useItem(item: Item) {
            backgroundScope.launch {
                val dbItem = repo.findItem(item.name)
                //If exist and not classified
                if (dbItem != null && item.isClassified) {
                    //  useClassifiedItem(item)
                } else {
                    //Can exist and not use, for example default option
                    //useUnclassifiedItem(item)
                    //Remove the item from the unclassified auto complete list
                }
            }
        }

        fun useUnclassifiedItem(item: Item, itemsAdapter: ItemsAdapter) {
            backgroundScope.launch {
                val dbItem = repo.findItem(item.name)
                //New item
                if (dbItem == null) repo.useItem(item)
                itemsAdapter.addToList(item)
                itemsAdapter.notifyDataSetChanged()
            }
        }

        fun useClassifiedItem(item: Item, departmentsAdapter: DepartmentsAdapter) {
            //Get the department from the item
            //Check if the department is already displayed through the department list
            //If no get the adapter and add it to the dep adapter
            //If yes add the item to the items list in the dep adapter => Update department and notify change on dep adapter
            backgroundScope.launch {
                val d = repo.findDepartment(item.departmentId)
                if (d != null) {
                    if (departmentsAdapter.getList().contains(d)) {//Department already displayed
                        val indexOf = departmentsAdapter.getList().indexOf(d)
                        departmentsAdapter.getList()[indexOf].items.add(item)
                        departmentsAdapter.getList()[indexOf].items.sortBy { i -> i.order }

                    } else {
                        d.items.add(item)
                        d.items.sortBy { i -> i.order }
                        departmentsAdapter.addToList(d)
                    }
                    departmentsAdapter.notifyDataSetChanged()
                }

            }

        }

        fun classifyItem(item: Item, source: ItemsAdapter, target: ItemsAdapter) {
            backgroundScope.launch {
                //Save the new item : change in department name and in order (maybe)
                repo.saveItem(item)
                //Remove the item from the previous list
                val indexS = source.getList().indexOf(item)
                source.getList().removeAt(indexS)
                source.notifyItemRemoved(indexS)
                target.addToList(item)
                target.notifyDataSetChanged()
            }
        }
    }
}