package small.app.shopping.list.models

import android.util.Log
import small.app.shopping.list.objects.Utils.saveAndUse
import small.app.shopping.list.objects.Utils.updateOrder
import small.app.shopping.list.room.entities.Item

data class Department(
    val id: String,
    val name: String,
    var isUsed: Boolean,
    var items: MutableList<Item>,
    var itemsCount: Int,//Can be different from items.size if some items are not displayed, this var is used to store the number of items not
    var order: Int,
    var storeId: String
) {

    fun classify(item: Item) {
        //Avoid that dropping an item on the drag start department increase the nb of items in the department
        if (item.departmentId != this.name) {
            Log.d("Department","classify")
            this.itemsCount += 1
            with(item) {
                isUsed = true
                departmentId = this@Department.id
                order = this@Department.itemsCount.toLong()
                storeId=this@Department.storeId
            }
        }
    }

    fun classifyAndSave(item: Item) {
        classify(item)
        saveAndUse(item)
    }

    fun classifyWithOrderDefined(item: Item) {
        //Avoid that dropping an item on the drag start department increase the nb of items in the department
        if (item.departmentId != this.name) {
            Log.d("Department","classify")
            this.itemsCount += 1
            with(item) {
                isUsed = true
                departmentId = this@Department.name
                //Save the new items count

            }
            saveAndUse(item)
            updateOrder()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Department

        if (name != other.name) return false
        if (isUsed != other.isUsed) return false

        if (order != other.order) return false
        if (items.size != other.items.size) return false
        for (i in items.indices) {
            if (!items[i].equals(other.items[i])) return false
        }

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

