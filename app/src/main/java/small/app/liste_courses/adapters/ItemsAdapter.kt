package small.app.liste_courses.adapters


import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_grossery_item.view.*
import small.app.liste_courses.R
import small.app.liste_courses.adapters.diffutils.ItemsDiffUtils
import small.app.liste_courses.models.DragItem
import small.app.liste_courses.objects.ItemChange
import small.app.liste_courses.objects.ItemsComparator
import small.app.liste_courses.objects.SIUnit
import small.app.liste_courses.objects.Utils
import small.app.liste_courses.room.entities.Item

//TODO : add modfication name & change order


abstract class ItemsAdapter(
    protected val context: Context,
    protected val canChangeParam: Boolean
) :
    RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>() {
    protected val list = mutableListOf<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        return ItemsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_grossery_item,
                parent,
                false
            )
        )
    }


    private fun fillView(holder: ItemsViewHolder, item: Item) {
        holder.itemView.tv_name.text = item.name
        if (item.isClassified) {
            holder.itemView.iv_check_item.visibility = View.VISIBLE

        } else {
            holder.itemView.iv_check_item.visibility = View.GONE
        }

        holder.itemView.tv_unit.visibility = View.VISIBLE
        holder.itemView.s_unit.visibility = View.GONE

        holder.itemView.tv_unit.text = item.unit.value

        holder.itemView.tv_qty.text = item.qty.toString()

    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {

        Log.d("IAdapter", list[position].name)
        Log.d("IAdapter", " $position")
        if (list[position].name.isNotEmpty()) {

            if (list[position].isUsed) {
                fillView(holder, list[position])
                holder.itemView.iv_check_item.setOnClickListener {
                    list[position].isUsed = false
                    //Update RV
                    Log.d("IAdapter", "Remove at position : $position")
                    Utils.unuseItem(list[position])
                }

                holder.itemView.iv_increase_qty.setOnClickListener {
                    list[position].qty += list[position].unit.mutliplicator
                    Utils.saveItem(list[position])
                    holder.itemView.tv_qty.text = list[position].qty.toString()
                }
                holder.itemView.iv_decrease_qty.setOnClickListener {
                    list[position].qty =
                        Math.max(0, list[position].qty - list[position].unit.mutliplicator)
                    Utils.saveItem(list[position])
                    holder.itemView.tv_qty.text = list[position].qty.toString()

                }

                holder.itemView.tv_unit.text = list[position].unit.value

                //Both variable are used to send the drag item
                holder.model = list[position]
                holder.adapter = this@ItemsAdapter
                holder.onLongClick(holder.itemView)

            } else {
                holder.itemView.visibility = View.GONE

            }
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ItemsViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnLongClickListener {

        private var longPressed = false
        var model: Item? = null
        var adapter: ItemsAdapter? = null

        init {
            view.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            v?.apply {


                val clipText = "This is our ClipData text"
                val item = ClipData.Item(clipText)
                val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                val data = ClipData(clipText, mimeTypes, item)

                layout(0, 0, measuredWidth, measuredHeight)
                val dragShadowBuilder = DragShadowBuilder(this.tv_name)

                //val dragShadowBuilder = View.DragShadowBuilder(v.tv_name)//shadowView
                v.startDragAndDrop(data, dragShadowBuilder, DragItem(model!!, adapter!!), 0)
                longPressed = true
                return true
            }

            return false
        }


    }


    fun updateList(list: List<Item>?) {
        if (list != null) {
            list.sortedWith(ItemsComparator())
            val diffResult = DiffUtil.calculateDiff(ItemsDiffUtils(this.list, list), false)
            diffResult.dispatchUpdatesTo(this)

            this.list.clear()
            this.list.addAll(list)
            //this.list.sortedWith(ItemsComparator())

        }

    }

    override fun onBindViewHolder(
        holder: ItemsViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        // super.onBindViewHolder(holder, position, payloads)
        if (payloads.isEmpty()) {
            //Keep this for the first call
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.filterIsInstance<Bundle>().forEach { bundle ->
                run {
                    bundle.keySet().forEach { key ->
                        run {

                            if (key == ItemChange.QTY.toString()) {
                                val qty: String = (bundle.get(key) as CharSequence?).toString()
                                list[position].qty = qty.toLong()
                                holder.itemView.tv_qty.text = qty
                            }

                            if (key == ItemChange.UNIT.toString()) {
                                val unit: String = (bundle.get(key) as CharSequence?).toString()
                                list[position].unit = SIUnit.fromValue(unit)
                                holder.itemView.tv_unit.text = unit
                            }

                        }
                    }


                }

            }

        }

    }
}