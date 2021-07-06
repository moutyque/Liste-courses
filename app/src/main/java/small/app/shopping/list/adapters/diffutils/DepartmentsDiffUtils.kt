package small.app.shopping.list.adapters.diffutils

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import small.app.shopping.list.models.Department
import small.app.shopping.list.objects.DepartmentChange

class DepartmentsDiffUtils(
    private val oldList: List<Department>,
    private val newList: List<Department>
) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return old.equals(new)
    }

   override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {

          if (newList.size != oldList.size || newItemPosition >= newList.size || oldItemPosition >= oldList.size) return null
          val new = newList[newItemPosition]
          val old = oldList[oldItemPosition]
          val bundle = Bundle()

          if (!new.items.equals(old.items)) {
              bundle.putString(DepartmentChange.ITEMS.toString(), "")
          }

          return bundle
      }
}