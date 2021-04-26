package small.app.liste_courses.adapters.diffutils

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.SortedList
import small.app.liste_courses.objects.Item_change
import small.app.liste_courses.room.entities.Item

class ItemsDiffUtils(private val oldList: MutableList<Item>, private val newList: List<Item>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return old.name==new.name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return old.equals(new)
    }
    //TODO : improve this method to check which modification has been done

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val new = newList[newItemPosition]
        val old = oldList[oldItemPosition]
        val bundle = Bundle()
        if(new.name != old.name){
            bundle.putString(Item_change.NAME.toString(),new.name)
        }
        if(new.qty!=old.qty){
            bundle.putString(Item_change.QTY.toString(), new.qty.toString())
        }
        if(new.unit!=old.unit){
            bundle.putString(Item_change.UNIT.toString(), new.unit.toString())
        }

        return bundle
    }
}