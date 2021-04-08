package small.app.liste_courses.model

import small.app.liste_courses.room.entities.Item

data class Department(
    val name: String,
    var isUsed: Boolean,
    var items: MutableList<Item>,
    var order: Int
) {

    //Not sure if usefull
    /*init {
        for (item in items) {
            item.isClassified = true
        }
    }*/

    fun classify(item: Item) {
        val list = ArrayList(items)
        item.isClassified = true
        item.isUsed = true
        item.departmentId = name
        item.order = items.size.toLong()
        list.add(item)
        items = list.toMutableList()
    }

}

