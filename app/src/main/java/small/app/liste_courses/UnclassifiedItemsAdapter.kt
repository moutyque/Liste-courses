package small.app.liste_courses

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_unclassified_item.view.*
import small.app.liste_courses.room.entities.Item

class UnclassifiedItemsAdapter(private val context: Context, private var list: List<Item>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemsViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_unclassified_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        Log.d("Adapter", model.name)
        if (holder is ItemsViewHolder) {
            holder.itemView.tv_name.text = model.name
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(newList: List<Item>) {
        list = newList
        //notifyDataSetChanged()
    }

    class ItemsViewHolder(view: View) : RecyclerView.ViewHolder(view)

}