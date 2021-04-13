package small.app.liste_courses.adapters

import android.content.Context
import kotlinx.coroutines.launch
import small.app.liste_courses.Scope
import small.app.liste_courses.Utils.repo
import small.app.liste_courses.adapters.listeners.ILastItemUsed
import small.app.liste_courses.room.entities.Item

class DepartmentItemsAdapter(items : List<Item>, context: Context, canChangeUnit: Boolean,
                             lastItemUsed: ILastItemUsed
) : ItemsAdapter(context,
    canChangeUnit, lastItemUsed
) {

init {




        Scope.mainScope.launch {
            list.addAll(items.filter { i -> i.isUsed })
        }


}

}