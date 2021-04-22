package small.app.liste_courses.adapters

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import small.app.liste_courses.adapters.diffutils.ItemsDiffUtils
import small.app.liste_courses.adapters.listeners.IItemUsed
import small.app.liste_courses.room.entities.Item


class UnclassifiedItemsAdapter(
    context: Context, canChangeUnit: Boolean,
    itemUsed: IItemUsed
) : ItemsAdapter(
    context,
    canChangeUnit, itemUsed
) {




}