package small.app.liste_courses.models

import small.app.liste_courses.room.entities.Item

data class Department(
    val name: String,
    var isUsed: Boolean,
    var items: MutableList<Item>,
    var order: Int
) {


    fun classify(item: Item) {

        this.items.add(item)
        val position = items.indexOf(item)
        with(items[position]) {
            isClassified = true
            isUsed = true
            departmentId = this@Department.name
            order = items.size.toLong()
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Department

        if (name != other.name) return false
        if (isUsed != other.isUsed) return false
        if (items != other.items) return false
        if (order != other.order) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + isUsed.hashCode()
        result = 31 * result + items.hashCode()
        result = 31 * result + order
        return result
    }

}

