package small.app.shopping.list.comparators

import small.app.shopping.list.room.entities.Item

class ItemsComparator : Comparator<Item> {
    override fun compare(o1: Item?, o2: Item?): Int {
        if (o1 == null && o2 == null) return 0
        if (o1 == null) return -1
        if (o2 == null) return 1

        return o1.order.compareTo(o2.order)
    }
}