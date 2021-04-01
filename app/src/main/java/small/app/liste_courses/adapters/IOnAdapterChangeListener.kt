package small.app.liste_courses.adapters

import androidx.recyclerview.widget.RecyclerView

interface IOnAdapterChangeListener<T, R> where R : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    fun onObjectCreated(a: T)

    fun onItemUpdate(a: T, position: Int, code: ObjectChange)

    fun onItemDelete(a: T)

    fun setAdapter(adapter: R)

    fun getAdapter(): R

}