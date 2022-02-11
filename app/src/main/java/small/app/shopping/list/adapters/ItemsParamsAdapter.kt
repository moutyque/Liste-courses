package small.app.shopping.list.adapters

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.item_grossery_item.view.*
import small.app.shopping.list.callback.IMovableAdapter
import small.app.shopping.list.enums.SIUnit.Companion.unitList
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.room.entities.Item


class ItemsParamsAdapter(
    context: Context
) : ItemsAdapter(
    context

), IMovableAdapter {


    override fun fillView(holder: ItemsViewHolder, item: Item) {
        super.fillView(holder, item)
        holder.itemView.iv_check_item.visibility = View.VISIBLE
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        //Manage the view of the drop down list of unit
        holder.itemView.ll_list_view.visibility = View.GONE
        holder.itemView.ll_param_view.visibility = View.VISIBLE
        holder.itemView.ib_delete_item.setOnClickListener {
            Utils.deleteItem(list, position)

        }

        with(holder.itemView.s_unit) {
            if (adapter == null) {
                val adapt = UnitAdapter(context)
                adapt.actions = object : IUnitSelect {
                    override fun getItem(): Item {
                        Log.d(Utils.TAG, holder.itemView.tv_name.text as String)
                        return list.first { item -> item.name == holder.itemView.tv_name.text }
                    }

                    override fun initUnit() {
                        holder.itemView.s_unit.setSelection(unitList().indexOf(list[position].unit.value))
                    }
                }
                adapter = adapt.adapter
                onItemSelectedListener = adapt
            }
        }


        //Setup the drag and drop only on the reorder icon
        holder.itemView.iv_reorder.setOnTouchListener { v, event ->
            Log.d(Utils.TAG, "I touched $event")

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    canMove = true
                    v.performClick()
                }
                MotionEvent.ACTION_UP -> canMove = false
            }
            true
        }


    }


    override fun canMove(): Boolean {
        return canMove
    }

    override fun setMove(b: Boolean) {
        canMove = b
    }

    override fun getAdapterList(): List<Any> {
        return list.toList()
    }

    override fun onDragEnd() {
        Utils.saveItems(*list.toTypedArray())
        //Fix issue : when the drag end and we delete an item after, the position send to the onBindViewHolder is not the right one, it is the position of the previous item
        //Ex : if we move item 2 from position 2 to 4 and then delete item 2, we expect to get position 4 bt we get 2 instead
        this.notifyDataSetChanged()
    }


    override fun onItemMove(initialPosition: Int, targetPosition: Int) {
        if (initialPosition > -1 && targetPosition > -1) {
            //This is call at every move so the save must only occur once the drag is done
            with(list) {
                val init = get(initialPosition)
                val target = get(targetPosition)
                val tmp = init.order
                init.order = target.order
                target.order = tmp
                Utils.swapInCollection(list, initialPosition, targetPosition)
                this@ItemsParamsAdapter.notifyItemMoved(
                    initialPosition,
                    targetPosition
                )
            }
        }
    }


}