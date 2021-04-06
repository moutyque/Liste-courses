package small.app.liste_courses.adapters

import androidx.recyclerview.widget.RecyclerView

/**
 * Inteface to of action inside a recyclerview
 * @param T : The object which is used inside the recycler view
 * @param R : The adapter type
 * @param S : The viewholder type, must the same used in the adapter
 */
interface IOnAdapterChangeListener<T,R,S> where S : RecyclerView.ViewHolder, R : RecyclerView.Adapter<S> {

    fun onObjectCreated(a: T)

    fun onItemUpdate(a: T, position: Int,list : MutableList<T>, code: ObjectChange)

    fun onItemDelete(a: T)

    fun setAdapter(adapter: R)

    fun getAdapter(): R

}