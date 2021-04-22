package small.app.liste_courses.adapters


import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_grossery_item.view.*
import small.app.liste_courses.R
import small.app.liste_courses.adapters.diffutils.ItemsDiffUtils
import small.app.liste_courses.adapters.listeners.IItemUsed
import small.app.liste_courses.models.DragItem
import small.app.liste_courses.objects.Utils
import small.app.liste_courses.room.entities.Item

abstract class ItemsAdapter(
    private val context: Context,
    private val canChangeUnit: Boolean,
    val itemUsed: IItemUsed
) :
    RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>(), IList<Item> {

    val list = mutableListOf<Item>()//SortedList(Item::class.java, ItemCallBack(this))


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

        //Manage the view of the drop down list of unit
        if (canChangeUnit) {
            holder.itemView.tv_unit.visibility = View.GONE
            holder.itemView.s_unit.visibility = View.VISIBLE

        } else {
            holder.itemView.tv_unit.visibility = View.VISIBLE
            holder.itemView.s_unit.visibility = View.GONE
        }
        holder.itemView.tv_unit.text = item.unit.value

        //Manage qty
        holder.itemView.tv_qty.text = item.qty.toString()
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        with(list[position]) {
            Log.d("IAdapter", name)
            Log.d("IAdapter", " $position")
            if (name.isNotEmpty()) {

                if (isUsed) {
                    fillView(holder, this)
                    holder.itemView.iv_check_item.setOnClickListener {
                        this.isUsed = false
                        //Update RV
                        Log.d("IAdapter", "Remove at position : $position")
                        Utils.saveItem(this)
                        notifyItemChanged(position)

                        //TODO : remove from list ?
                        //Utils.unuseItem(this, this@ItemsAdapter)
                    }

                    holder.itemView.iv_increase_qty.setOnClickListener {
                        qty += unit.mutliplicator
                        Utils.saveItem(this)
                        notifyItemChanged(position)

                        //updateQty(qty, position)
                    }
                    holder.itemView.iv_decrease_qty.setOnClickListener {
                        qty -= unit.mutliplicator
                        if (qty < 0) {
                            qty = 0
                        }
                        Utils.saveItem(this)
                        notifyItemChanged(position)

                        //updateQty(qty, position)
                    }

                    //Both variable are used to send the drag item
                    holder.model = this
                    holder.adapter = this@ItemsAdapter
                    holder.onLongClick(holder.itemView)

                    // Creates a new drag event listener
                    //val dragListen = ItemsDragListener(this)
                    //holder.itemView.setOnDragListener(dragListen)

                } else {
                    holder.itemView.visibility = View.GONE

                }
            }

        }
    }

    private fun updateQty(qty: Long, position: Int) {
        list[position].qty = qty
        Utils.saveItem(list[position])
        notifyItemChanged(position)
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

                v.tv_name.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                v.tv_name.layout(0, 0, v.tv_name.measuredWidth, v.tv_name.measuredHeight)

                val clipText = "This is our ClipData text"
                val item = ClipData.Item(clipText)
                val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                val data = ClipData(clipText, mimeTypes, item)

                val dragShadowBuilder = View.DragShadowBuilder(v.tv_name)//shadowView
                v.startDragAndDrop(data, dragShadowBuilder, DragItem(model!!, adapter!!), 0)
                longPressed = true
                return true
            }

            return false
        }


    }

    override fun add(i: Item) {
        //if (i.order == -1L) i.order = list.size().toLong()
        //list.add(i)
    }

    override fun remove(i: Item) {
        // list.remove(i)
    }

    override fun contains(i: Item): Boolean {
        return list.indexOf(i) > -1
    }

    override fun findIndex(i: Item): Int {
        for (index in 0 until list.size) {
            if (list[index].name == i.name) {
                return index
            }
        }
        return -1
    }

    fun updateList(list: List<Item>?) {
        if (list != null) {
            list.sortedBy { item -> item.order }
            val diffResult = DiffUtil.calculateDiff(ItemsDiffUtils(this.list, list))
            this.list.clear()
            this.list.addAll(list)
            this.list.sortedBy { item -> item.order }
            diffResult.dispatchUpdatesTo(this)
        }

    }

    //TODO : improve this method to check which modification has been done
    override fun onBindViewHolder(
        holder: ItemsViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.filterIsInstance<Item>().forEach { item ->
                run {
                    fillView(holder, item)

                }

            }

        }

    }
}