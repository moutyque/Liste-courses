package small.app.shopping.list.adapters

import android.R
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_grossery_item.view.*
import small.app.shopping.list.callback.IMovableAdapter
import small.app.shopping.list.objects.SIUnit
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.room.entities.Item


class ItemsParamsAdapter(
    context: Context
) : ItemsAdapter(
    context

), IMovableAdapter {
    private val unitList = arrayListOf<String>()

    init {
        unitList.add(SIUnit.EMPTY.value)
        unitList.add(SIUnit.CL.value)
        unitList.add(SIUnit.L.value)
        unitList.add(SIUnit.G.value)
        unitList.add(SIUnit.KG.value)
    }

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

        if (holder.itemView.s_unit.adapter == null) {
            setupUnitAdapter(holder)
        }
        holder.itemView.s_unit.setSelection(unitList.indexOf(holder.model!!.unit.value))


        //Setup the drag and drop only on the reorder icon
        holder.itemView.iv_reorder.setOnTouchListener { v, event ->
            Log.d("ClickOnReorder", "I touched $event")

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

    private fun setupUnitAdapter(holder: ItemsViewHolder) {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            context,
            R.layout.simple_dropdown_item_1line,
            unitList
        )

        holder.itemView.s_unit.adapter = adapter
        holder.itemView.s_unit.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                var initialized = false
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (initialized) {
                        true -> {

                            holder.model!!.unit = SIUnit.fromValue(unitList[position])
                            Utils.saveItem(holder.model!!)
                        }
                        false -> {
                            holder.itemView.s_unit.setSelection(unitList.indexOf(holder.model!!.unit.value))
                            initialized = true
                        }
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //Not used
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

        Utils.saveItems(*list.toTypedArray())
    }


    override fun onItemMove(initialPosition: Int, targetPosition: Int) {
        if (initialPosition > -1 && targetPosition > -1) {
            //This is call at every move so the save must only occure once the drag is done
            with(list) {
                val init = get(initialPosition)
                val target = get(targetPosition)
                val tmp = init.order
                init.order = target.order
                target.order = tmp
                Utils.swapInCollection(list, initialPosition, targetPosition)
            }
            this.notifyItemMoved(initialPosition, targetPosition)
        }
    }


}