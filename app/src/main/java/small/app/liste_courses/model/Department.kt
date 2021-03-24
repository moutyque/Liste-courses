package small.app.liste_courses.model

import small.app.liste_courses.room.entities.Item

data class Department(val name: String, var items: List<Item>) {

    init {
        for (item in items) {
            item.isClassified = true
        }

    }

    fun classify(item: Item) {
        val list = ArrayList(items)
        item.isClassified = true
        item.departmentId = name
        list.add(item)
        items = list.toList()
    }

}

