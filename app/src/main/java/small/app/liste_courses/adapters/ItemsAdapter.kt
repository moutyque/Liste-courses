package small.app.liste_courses.adapters


import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import android.widget.LinearLayout
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

abstract class ItemsAdapter(
    protected val context: Context
) :
    RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>() {
    protected val list = mutableListOf<Item>()

    protected var canMove = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        return ItemsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_grossery_item,
                parent,
                false
            )
        )
    }


    protected open fun fillView(holder: ItemsViewHolder, item: Item) {
        holder.itemView.tv_name.text = item.name
        holder.itemView.tv_unit.text = item.unit.value
        holder.itemView.tv_qty.text = item.qty.toString()

    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {

        Log.d("IAdapter", list[position].name)
        Log.d("IAdapter", " $position")
        if (list[position].name.isNotEmpty()) {

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

            //holder.onLongClick(holder.itemView.iv_dd)
            //Defined which action will happened when we long click on the iv_dd by passing an object that implement View.OnLongClickListener
            //holder.itemView.iv_dd.setOnLongClickListener(holder)


        }


    }

    override fun getItemCount(): Int {
        return list.size
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

    class ItemsViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnLongClickListener,
        View.OnTouchListener {
        // class member variable to save the X,Y coordinates
        private val lastTouchDownXY = FloatArray(2)

        //        private var longPressed = false
        var model: Item? = null
        var adapter: ItemsAdapter? = null

        init {
            view.setOnTouchListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            val parent = v?.parent?.parent as LinearLayout

            parent.apply {

                val clipText = "This is our ClipData text"
                val item = ClipData.Item(clipText)
                val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                val data = ClipData(clipText, mimeTypes, item)


                val dragShadowBuilder = object : DragShadowBuilder(this) {

                    override fun onProvideShadowMetrics(
                        outShadowSize: Point?,
                        outShadowTouchPoint: Point?
                    ) {
                        super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint)
                        outShadowTouchPoint?.x =
                            measuredWidth - lastTouchDownXY[0].toInt() // Make the dragShadowBuilder appears at the the left of the line and not on the clicked point
                    }
                }

                layout(
                    0,
                    0, measuredWidth, measuredHeight
                )

                v.startDragAndDrop(data, dragShadowBuilder, DragItem(model!!, adapter!!), 0)

                return true
            }

            return false
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            // save the X,Y coordinates
            if (event!!.action == MotionEvent.ACTION_DOWN) {
                lastTouchDownXY[0] = event.x
                lastTouchDownXY[1] = event.y
            }
            v?.performClick()
            // let the touch event pass on to whoever needs it
            return false; }


    }
}