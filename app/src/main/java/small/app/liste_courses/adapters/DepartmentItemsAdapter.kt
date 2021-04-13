package small.app.liste_courses.adapters

import android.content.Context
import kotlinx.coroutines.launch
import small.app.liste_courses.Scope
import small.app.liste_courses.Utils.repo
import small.app.liste_courses.adapters.listeners.ILastItemUsed
import small.app.liste_courses.room.entities.Item

class DepartmentItemsAdapter(depName : String, context: Context, canChangeUnit: Boolean,
                             lastItemUsed: ILastItemUsed
) : ItemsAdapter(context,
    canChangeUnit, lastItemUsed
) {

init {


    var items : List<Item> = ArrayList()
    val job = Scope.backgroundScope.launch {
        items = repo.findDepartmentItems(depName).filter { i->i.isUsed }

    }
    job.invokeOnCompletion {
        Scope.mainScope.launch {
            list.addAll(items)
        }

    }
}

}