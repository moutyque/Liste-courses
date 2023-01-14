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
import small.app.shopping.list.adapters.diffutils.ItemsDiffUtils
import small.app.shopping.list.comparators.ItemsComparator
import small.app.shopping.list.databinding.ItemGrosseryItemBinding
import small.app.shopping.list.enums.ItemChange
import small.app.shopping.list.enums.SIUnit
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.objects.Utils.classifyDropItem
import small.app.shopping.list.objects.Utils.saveAndUse
import small.app.shopping.list.objects.Utils.unuse
import small.app.shopping.list.room.entities.Item
import kotlin.math.max

abstract class ItemsAdapter(
    protected val context: Context
) :


    RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>() {

    protected val list = mutableListOf<Item>()

    protected var canMove = false

    lateinit var binding: ItemGrosseryItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {

        binding =
            ItemGrosseryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val viewHolder = ItemsViewHolder(binding)
        viewHolder.binding.ivDd.setOnLongClickListener(viewHolder)

        return viewHolder

    }


    protected open fun fillView(holder: ItemsViewHolder, item: Item) {
        holder.binding.apply {
            tvName.text = item.name
        }


    }


    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        //model and adapter variables are used to send the drag item
        holder.adapter = this@ItemsAdapter


        Log.d(Utils.TAG, " $position")
        if (list[position].name.isNotEmpty()) {

            fillView(holder, list[position])
            holder.binding.apply {
                ivCheckItem.setOnClickListener {
                    list[holder.layoutPosition].unuse()
                }
            }

            //Setup the drag event listener
            holder.itemView.setOnDragListener { v, event ->
                if (v != null && event != null) {
                    Log.d(Utils.TAG, event.toString())
                    Log.d(Utils.TAG, "Position : " + v.x + " : " + v.y)
                    with(holder.binding) {
                        when (event.action) {
                            DragEvent.ACTION_DRAG_ENTERED -> {
                                Log.d(Utils.TAG, "Enter")
                                separator.visibility = View.VISIBLE
                            }
                            DragEvent.ACTION_DRAG_EXITED -> {
                                Log.d(Utils.TAG, "Exited")
                                separator.visibility = View.GONE
                            }
                            DragEvent.ACTION_DROP -> {
                                val droppedItemName = event.localState
                                if (droppedItemName is String) {
                                    Log.d(Utils.TAG, "Has drop $droppedItemName")
                                    list[position].classifyDropItem(droppedItemName)
                                }
                                separator.visibility = View.GONE
                            }
                        }
                    }
                }
                true
            }
        }
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
        }
    }

    class ItemsViewHolder(val binding: ItemGrosseryItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnLongClickListener,
        View.OnTouchListener {
        // class member variable to save the X,Y coordinates
        private val lastTouchDownXY = FloatArray(2)

        var adapter: ItemsAdapter? = null

        init {
            binding.root.setOnTouchListener(this)
        }

        override fun onLongClick(v: View?): Boolean {

            //Prepare dragShadowBuilder element
            val clipText = "This is our ClipData text"
            val item = ClipData.Item(clipText)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(clipText, mimeTypes, item)

            //Use the drag view as a context to build the dragShadowBuilder
            this.binding.llCompletLine.apply {
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
                    this@ItemsViewHolder.binding.tvName.text as String,
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