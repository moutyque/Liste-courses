package small.app.shopping.list.models

import small.app.shopping.list.adapters.ItemsAdapter
import small.app.shopping.list.room.entities.Item

data class DragItem(val item: Item, val adapter: ItemsAdapter)
