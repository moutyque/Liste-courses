package small.app.shopping.list.models

import small.app.shopping.list.objects.Utils
import small.app.shopping.list.room.entities.Item

data class Department(
    val name: String,
    var isUsed: Boolean,
    var items: MutableList<Item>,
    var itemsCount: Int,//Can be different from items.size if some items are not displayed, this var is used to store the number of items not
    var order: Int
) {


    fun classify(item: Item) {

        //Avoid that dropping an item on the drag start department increase the nb of items in the department
        if (item.departmentId != this.name) {
            this.itemsCount += 1
            with(item) {
                isClassified = true
                isUsed = true
                departmentId = this@Department.name
                order = this@Department.itemsCount.toLong()

                Utils.saveDepartmentAndItem(this, this@Department)//Save the new items count
            }
        }


    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Department

        if (name != other.name) return false
        if (isUsed != other.isUsed) return false

        if (items.size != other.items.size) return false
        for (i in items.indices) {
            if (!items[i].equals(other.items[i])) return false
        }

        if (items.equals(other.items)) return false
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

