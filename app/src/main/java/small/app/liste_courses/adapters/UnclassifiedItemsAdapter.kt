package small.app.liste_courses.adapters

import android.content.Context
import kotlinx.coroutines.launch
import small.app.liste_courses.Scope
import small.app.liste_courses.Scope.backgroundScope
import small.app.liste_courses.Utils
import small.app.liste_courses.adapters.listeners.ILastItemUsed
import small.app.liste_courses.room.entities.Item

class UnclassifiedItemsAdapter(context: Context, canChangeUnit: Boolean,
                               lastItemUsed: ILastItemUsed
) : ItemsAdapter(context,
    canChangeUnit, lastItemUsed
) {

    init {

         backgroundScope.launch {
            list.addAll(Utils.repo.getUnclassifiedItem())
        }





    }


}