package small.app.shopping.list.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import small.app.shopping.list.callback.IMovableAdapter
import small.app.shopping.list.enums.SIUnit.Companion.unitList
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.objects.Utils.delete
import small.app.shopping.list.objects.Utils.save
import small.app.shopping.list.room.entities.Item


class ItemsParamsAdapter(
    context: Context
) : ItemsAdapter(
    context

), IMovableAdapter {


    override fun fillView(holder: ItemsViewHolder, item: Item) {
        super.fillView(holder, item)
        holder.binding.ivCheckItem.visibility = View.VISIBLE
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        super.onBindViewHolder(holder, holder.adapterPosition)
        //Manage the view of the drop down list of unit
        holder.binding.apply {
            llListView.visibility = View.GONE
            llParamView.visibility = View.VISIBLE
            ibDeleteItem.setOnClickListener {
                list.delete(holder.adapterPosition)
            }
            with(sUnit) {
                if (adapter == null) {
                    val adapt = UnitAdapter(context)
                    adapt.actions = object : IUnitSelect {
                        override fun getItem(): Item {
                            Log.d(Utils.TAG, tvName.text as String)
                            return list.first { item -> item.name == tvName.text }
                        }

                        override fun initUnit() {
                            sUnit.setSelection(unitList().indexOf(list[holder.adapterPosition].unit.value))
                        }
                    }
                    adapter = adapt.adapter
                    onItemSelectedListener = adapt
                }
            }
            //Setup the drag and drop only on the reorder icon
            ivReorder.setOnTouchListener { v, event ->
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
        list.save()
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