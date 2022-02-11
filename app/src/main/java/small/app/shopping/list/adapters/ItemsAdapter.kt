package small.app.shopping.list.adapters


import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.DragShadowBuilder
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_grossery_item.view.*
import small.app.shopping.list.R
import small.app.shopping.list.adapters.diffutils.ItemsDiffUtils
import small.app.shopping.list.comparators.ItemsComparator
import small.app.shopping.list.enums.ItemChange
import small.app.shopping.list.enums.SIUnit
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.room.entities.Item
import kotlin.math.max

abstract class ItemsAdapter(
    protected val context: Context
) :


    RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>() {

    protected val list = mutableListOf<Item>()

    protected var canMove = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val viewHolder = ItemsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_grossery_item,
                parent,
                false
            )
        )
        viewHolder.itemView.iv_dd.setOnLongClickListener(viewHolder)

        return viewHolder

    }


    protected open fun fillView(holder: ItemsViewHolder, item: Item) {
        holder.itemView.tv_name.text = item.name
        holder.itemView.tv_unit.text = item.unit.value
        holder.itemView.tv_qty.text = item.qty.toString()

    }


    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        //model and adapter variables are used to send the drag item
        holder.adapter = this@ItemsAdapter


        Log.d(Utils.TAG, " $position")
        if (list[position].name.isNotEmpty()) {

            fillView(holder, list[position])
            holder.itemView.iv_check_item.setOnClickListener {
                Utils.unuseItem(list[holder.layoutPosition])
            }

            holder.itemView.iv_increase_qty.setOnClickListener {
                increaseQty(position, holder)
            }
            holder.itemView.iv_decrease_qty.setOnClickListener {
                decreaseQty(position, holder)
            }

            holder.itemView.tv_unit.text = list[position].unit.value
            //Setup the drag event listener
            holder.itemView.setOnDragListener { v, event ->
                if (v != null && event != null) {
                    Log.d(Utils.TAG, event.toString())
                    Log.d(Utils.TAG, "Position : " + v.x + " : " + v.y)
                    when (event.action) {
                        DragEvent.ACTION_DRAG_ENTERED -> {
                            Log.d(Utils.TAG, "Enter")
                            holder.itemView.separator.visibility = View.VISIBLE
                        }
                        DragEvent.ACTION_DRAG_EXITED -> {
                            Log.d(Utils.TAG, "Exited")
                            holder.itemView.separator.visibility = View.GONE
                        }
                        DragEvent.ACTION_DROP -> {
                            val droppedItemName = event.localState
                            if (droppedItemName is String) {
                                Log.d(Utils.TAG, "Has drop ${droppedItemName}")
                                Utils.classifyDropItem(droppedItemName, list[position])
                            }
                            holder.itemView.separator.visibility = View.GONE
                        }
                    }
                }
                true
            }
        }
    }

    private fun decreaseQty(
        position: Int,
        holder: ItemsViewHolder
    ) {
        val item = list[position]
        Log.d(Utils.TAG, "decrease qty")
        val newQty = item.qty - item.unit.mutliplicator - item.qty % item.unit.mutliplicator

        item.qty =
            max(0, newQty)
        Utils.saveItem(item)
        holder.itemView.tv_qty.text = item.qty.toString()
    }

    private fun increaseQty(
        position: Int,
        holder: ItemsViewHolder
    ) {
        val item = list[position]
        val newQty = item.qty + item.unit.mutliplicator - item.qty % item.unit.mutliplicator
        Log.d(Utils.TAG, "increase qty, previous qty ${item.qty}, new qty $newQty")
        item.qty = newQty
        Log.d(Utils.TAG, item.qty.toString())
        Utils.saveItem(item)
        holder.itemView.tv_qty.text = item.qty.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: List<Item>?) {
        Log.d(Utils.TAG, "updateLists")
        //Need to update the empty list for unclassified item for example
        if (list != null) {
            list.sortedWith(ItemsComparator())
            val diffResult = DiffUtil.calculateDiff(ItemsDiffUtils(this.list, list), false)
            diffResult.dispatchUpdatesTo(this)
            this.list.clear()
            this.list.addAll(list)
        }
    }

    override fun onBindViewHolder(
        holder: ItemsViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
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

        var adapter: ItemsAdapter? = null

        init {
            view.setOnTouchListener(this)
        }

        override fun onLongClick(v: View?): Boolean {

            //Prepare dragShadowBuilder element
            val clipText = "This is our ClipData text"
            val item = ClipData.Item(clipText)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(clipText, mimeTypes, item)

            //Use the drag view as a context to build the dragShadowBuilder
            this.itemView.ll_complet_line.apply {
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

                return v!!.startDragAndDrop(
                    data,
                    dragShadowBuilder,
                    this@ItemsViewHolder.itemView.tv_name.text as String,
                    0
                )
            }
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            // save the X,Y coordinates
            if (event!!.action == MotionEvent.ACTION_DOWN) {
                lastTouchDownXY[0] = event.x
                lastTouchDownXY[1] = event.y
            }
            v?.performClick()
            // let the touch event pass on to whoever needs it
            return false
        }


    }


}