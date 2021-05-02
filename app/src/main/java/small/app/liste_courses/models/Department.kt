package small.app.liste_courses.models

import small.app.liste_courses.objects.Utils
import small.app.liste_courses.room.entities.Item

data class Department(
    val name: String,
    var isUsed: Boolean,
    var items: MutableList<Item>,
    var itemsCount: Int,//Can be differents from items.size if some items are not displayed, this var is used to store the number of items not
    var order: Int
) {


    fun classify(item: Item) {
        with(item) {
            isClassified = true
            isUsed = true
            departmentId = this@Department.name
            order = this@Department.itemsCount.toLong()
        }

        this.itemsCount++
        Utils.saveItem(item)


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

