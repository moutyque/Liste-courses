package small.app.liste_courses.adapters

import android.content.Context
import kotlinx.coroutines.launch
import small.app.liste_courses.Scope.backgroundScope
import small.app.liste_courses.Utils
import small.app.liste_courses.adapters.listeners.IItemUsed

class UnclassifiedItemsAdapter(context: Context, canChangeUnit: Boolean,
                               itemUsed: IItemUsed
) : ItemsAdapter(context,
    canChangeUnit, itemUsed
) {

    init {

         backgroundScope.launch {
            list.addAll(Utils.repo.getUnclassifiedItem())
        }

    }


}