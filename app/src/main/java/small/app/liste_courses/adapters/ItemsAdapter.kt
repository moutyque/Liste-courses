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
import small.app.liste_courses.objects.Item_change
import small.app.liste_courses.objects.ItemsComparator
import small.app.liste_courses.objects.Utils
import small.app.liste_courses.room.entities.Item

//TODO : issue with the list which is updated befoe the DiffUtils is call
abstract class ItemsAdapter(
    private val context: Context,
    private val canChangeUnit: Boolean
) :
    RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>() {

    var list = mutableListOf<Item>()

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
        val model = list[position]
        with(model) {
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
                        //list.removeAt(position)
                        list.removeAt(position)
                        notifyItemRemoved(position)
                        //TODO : remove from list ?
                        //Utils.unuseItem(this, this@ItemsAdapter)
                    }

                    holder.itemView.iv_increase_qty.setOnClickListener {
                        qty += unit.mutliplicator
                        Utils.saveItem(this)
                        //The fact to call this recall the onBindViewHolder() better update the value
                        holder.itemView.tv_qty.text = qty.toString()

                    }
                    holder.itemView.iv_decrease_qty.setOnClickListener {
                        qty -= unit.mutliplicator
                        if (qty < 0) {
                            qty = 0
                        }
                        Utils.saveItem(this)
                        holder.itemView.tv_qty.text = qty.toString()

                        //notifyItemChanged(position)
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

                //measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
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
            this.list.clear()
            this.list.addAll(list)
            this.list.sortedWith(ItemsComparator())
            diffResult.dispatchUpdatesTo(this)
        }

    }

    //TODO : this methode is called even if there
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
                            if (key == Item_change.UNIT.toString()) {
                                holder.itemView.tv_unit.text = bundle.get(key) as CharSequence?
                            }

                            if (key == Item_change.QTY.toString()) {
                                holder.itemView.tv_qty.text = bundle.get(key) as CharSequence?
                            }


                        }
                    }


                }

            }

        }

    }
}