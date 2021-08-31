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
import small.app.shopping.list.models.DragItem
import small.app.shopping.list.objects.ItemChange
import small.app.shopping.list.objects.ItemsComparator
import small.app.shopping.list.objects.SIUnit
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.room.entities.Item
import kotlin.math.max

abstract class ItemsAdapter(
    protected val context: Context
) :
    RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>(),
View.OnDragListener {
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
        val item = list[position]
        holder.model = item
        holder.adapter = this@ItemsAdapter

        Log.d("IAdapter", " $position")
        if (item.name.isNotEmpty()) {

            fillView(holder, item)
            holder.itemView.iv_check_item.setOnClickListener {
                item.isUsed = false
                //Update RV
                Log.d("IAdapter", "Remove at position : $position")
                Utils.unuseItem(item)
            }

            holder.itemView.iv_increase_qty.setOnClickListener {
                item.qty += item.unit.mutliplicator
                Utils.saveItem(item)
                holder.itemView.tv_qty.text = item.qty.toString()
            }
            holder.itemView.iv_decrease_qty.setOnClickListener {
                item.qty =
                    max(0, item.qty - item.unit.mutliplicator)
                Utils.saveItem(item)
                holder.itemView.tv_qty.text = item.qty.toString()

            }

            holder.itemView.tv_unit.text = item.unit.value
            //Setup the drag event listener
            holder.itemView.setOnDragListener(this)
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

        var model: Item? = null
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

                return  v!!.startDragAndDrop(data, dragShadowBuilder, DragItem(model!!, adapter!!), 0)
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
            return false; }


    }

    override fun onDrag(v: View?, event: DragEvent?): Boolean {

        if(v!=null && event!=null){
            Log.d("ItemsAdapter",event.toString())
            Log.d("ItemsAdapter","Position : " + v.x +" : " + v.y)

            when(event.action){
                DragEvent.ACTION_DRAG_ENTERED -> Log.d("ItemsAdapter","Enter")
                DragEvent.ACTION_DRAG_EXITED ->Log.d("ItemsAdapter","Exited")
            }
        }


        return true
    }



}