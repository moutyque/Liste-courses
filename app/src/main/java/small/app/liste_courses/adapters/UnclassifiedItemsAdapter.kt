package small.app.liste_courses.adapters

import android.content.Context
import small.app.liste_courses.adapters.listeners.IItemUsed


class UnclassifiedItemsAdapter(
    context: Context, canChangeUnit: Boolean,
    itemUsed: IItemUsed
) : ItemsAdapter(
    context,
    canChangeUnit
)