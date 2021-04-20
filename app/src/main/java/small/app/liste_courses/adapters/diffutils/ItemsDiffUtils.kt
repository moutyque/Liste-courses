package small.app.liste_courses.adapters.diffutils

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.SortedList
import small.app.liste_courses.objects.Item_change
import small.app.liste_courses.room.entities.Item

class ItemsDiffUtils(private val oldList : List<Item>, private val newList : List<Item>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
       return  oldList[oldItemPosition]==newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].equals(newList[newItemPosition])
    }

    /*override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {

        val old = oldList[oldItemPosition]
        val new  =newList[newItemPosition]



        return when {
            old.name != new.name -> {
                Item_change.NAME
            }
            old.qty != new.qty -> {
                Item_change.QTY
            }
            old.order != new.order -> {
                Item_change.ORDER
            }
            old.departmentId != new.departmentId -> {
                Item_change.DEPARTMENT_ID
            }
            old.unit != new.unit -> {
                Item_change.UNIT

            }
            else ->Item_change.UNKNOWN
        }

    }*/
}