package small.app.liste_courses.adapters

import android.R
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_grossery_item.view.*
import small.app.liste_courses.callback.IMovableAdapter
import small.app.liste_courses.objects.SIUnit
import small.app.liste_courses.objects.Utils


class ItemsParamsAdapter(
    context: Context, canChangeUnit: Boolean


) : ItemsAdapter(
    context,
    canChangeUnit
), IMovableAdapter {

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

//Manage the view of the drop down list of unit
        if (canChangeParam) {
            holder.itemView.ll_list_view.visibility = View.GONE
            holder.itemView.ll_param_view.visibility = View.VISIBLE
            holder.itemView.ib_delete_item.setOnClickListener {
                Utils.deleteItem(list[position])
            }

            if (holder.itemView.s_unit.adapter == null) {
                val unitList = arrayListOf<String>()

                unitList.add(SIUnit.EMPTY.value)
                unitList.add(SIUnit.CL.value)
                unitList.add(SIUnit.L.value)
                unitList.add(SIUnit.G.value)
                unitList.add(SIUnit.KG.value)

                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    context,
                    R.layout.simple_dropdown_item_1line,
                    unitList
                )

                val itemPosition = position
                holder.itemView.s_unit.adapter = adapter
                holder.itemView.s_unit.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        var initilized = false
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            when (initilized) {
                                true -> {

                                    list[itemPosition].unit = SIUnit.fromValue(unitList[position])

                                    Utils.saveItem(list[itemPosition])
                                }
                                false -> {
                                    holder.itemView.s_unit.setSelection(unitList.indexOf(list[itemPosition].unit.value))
                                    initilized = true
                                }
                            }

                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            //Not used
                        }

                    }

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


}