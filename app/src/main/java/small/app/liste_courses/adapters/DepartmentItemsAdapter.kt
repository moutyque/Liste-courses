package small.app.liste_courses.adapters

import android.content.Context
import small.app.liste_courses.adapters.listeners.IItemUsed
import small.app.liste_courses.room.entities.Item

class DepartmentItemsAdapter(context: Context, canChangeUnit: Boolean,
    itemUsed: IItemUsed
) : ItemsAdapter(
    context,
    canChangeUnit, itemUsed
) {


}