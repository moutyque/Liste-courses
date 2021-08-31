package small.app.shopping.list.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.item_department.view.*
import small.app.shopping.list.R
import small.app.shopping.list.objects.DepartmentChange


class DepartmentsFullScreenAdapter(
    context: Context
) :
    DepartmentsAbstractAdapter(context) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {

        return DepartmentViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_department,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        fillView(position, holder)

    }

    private fun fillView(
        position: Int,
        holder: ViewHolder
    ) {
        val model = list[position]

        holder.itemView.tv_dep_name.text = model.name

        //Recycler view for the items in the department
        Log.d("DAdapter", "department name : ${model.name} & items ${model.items}")

        var itemsAdapter = holder.itemView.rv_items.adapter
        if (itemsAdapter == null) {
            itemsAdapter = ItemsFullScreenAdapter(
                context
            )
            holder.itemView.rv_items.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            holder.itemView.rv_items.adapter = itemsAdapter
        }

        (itemsAdapter as ItemsAdapter).updateList(model.items)



    }


    class DepartmentViewHolder(view: View) : ViewHolder(view)


    //Only when a new department is created this is called I think
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.filterIsInstance<Bundle>().forEach { bundle ->
                run {
                    bundle.keySet().forEach { key ->
                        run {
                            if (key == DepartmentChange.NAME.toString()) {
                                holder.itemView.tv_dep_name.text = bundle.get(key) as CharSequence?
                            }

                            if (key == DepartmentChange.ITEMS.toString()) {
                                (holder.itemView.rv_items.adapter as ItemsAdapter).updateList(list[position].items)
                            }


                        }
                    }


                }

            }

        }


    }


}