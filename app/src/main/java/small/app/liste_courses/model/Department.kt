package small.app.liste_courses.model

import androidx.recyclerview.widget.SortedList
import small.app.liste_courses.room.entities.Item

data class Department(
    val name: String,
    var isUsed: Boolean,
    var items: MutableList<Item>,
    var order: Int
) {


    fun classify(item: Item) {

         this.items.add(item)
        val position =  items.indexOf(item)
        with(items[position]){
            isClassified = true
            isUsed = true
            departmentId = this@Department.name
            order = items.size.toLong()
        }

    }

}

