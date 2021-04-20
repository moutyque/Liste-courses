package small.app.liste_courses.models

import small.app.liste_courses.adapters.ItemsAdapter
import small.app.liste_courses.room.entities.Item

data class DragItem(val item: Item, val adapter: ItemsAdapter)
