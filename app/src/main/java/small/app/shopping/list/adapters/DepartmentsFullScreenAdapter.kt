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
import small.app.shopping.list.databinding.ItemDepartmentBinding
import small.app.shopping.list.enums.DepartmentChange
import small.app.shopping.list.objects.Utils


class DepartmentsFullScreenAdapter(
    context: Context
) :
    DepartmentsAbstractAdapter(context) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val binding = ItemDepartmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DepartmentViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        fillView(position, holder as DepartmentViewHolder)

    }

    private fun fillView(
        position: Int,
        holder: DepartmentViewHolder
    ) {
        val model = list[position]
        holder.binding.apply {
            ibNewItems.visibility = View.GONE
            tvDepName.text = model.name

            //Recycler view for the items in the department
            Log.d(Utils.TAG, "department name : ${model.name} & items ${model.items}")
            setupAdapter()
            (rvItems.adapter as ItemsAdapter).updateList(model.items)
        }
    }

    private fun ItemDepartmentBinding.setupAdapter(){
        if (rvItems.adapter == null) {
            rvItems.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            rvItems.adapter = ItemsFullScreenAdapter(
                context
            )
        }
    }


    class DepartmentViewHolder(val binding: ItemDepartmentBinding) : ViewHolder(binding.root)


    //Only when a new department is created this is called I think
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        require(holder is DepartmentViewHolder)
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.filterIsInstance<Bundle>().forEach { bundle ->
                run {
                    bundle.keySet().forEach { key ->
                        run {
                            if (key == DepartmentChange.NAME.toString()) {
                                holder.binding.tvDepName.text = bundle.get(key) as CharSequence?
                            }

                            if (key == DepartmentChange.ITEMS.toString()) {
                                (holder.binding.rvItems.adapter as ItemsAdapter).updateList(list[position].items)
                            }


                        }
                    }


                }

            }

        }


    }


}