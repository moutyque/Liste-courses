package small.app.liste_courses.adapters


import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import kotlinx.android.synthetic.main.item_grossery_item.view.*
import small.app.liste_courses.R
import small.app.liste_courses.objects.Utils
import small.app.liste_courses.adapters.listeners.IItemUsed
import small.app.liste_courses.adapters.sortedListAdapterCallback.ItemCallBack
import small.app.liste_courses.models.DragItem
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



    private fun fillView(holder: ItemsViewHolder,item :Item){
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
        Log.d("IAdapter", model.name)
        Log.d("IAdapter", " $position")
        if (model.name.isNotEmpty()) {

            if (model.isUsed) {

                fillView(holder,model)
                holder.itemView.iv_check_item.setOnClickListener {
                    model.isUsed = false
                    //Update RV
                    Log.d("IAdapter", "Remove at position : $position")
                    Utils.unuseItem(model, this)
                }

                holder.itemView.iv_increase_qty.setOnClickListener {
                    model.qty += model.unit.mutliplicator
                    updateQty(model)
                }
                holder.itemView.iv_decrease_qty.setOnClickListener {
                    model.qty -= model.unit.mutliplicator
                    if (model.qty < 0) {
                        model.qty = 0
                    }
                    //Utils.saveItem(model)
                    updateQty(model)
                }

                holder.model = model
                holder.adapter = this
                holder.onLongClick(holder.itemView)

                // Creates a new drag event listener
                //val dragListen = ItemsDragListener(this)
                //holder.itemView.setOnDragListener(dragListen)

            } else {
                holder.itemView.visibility = View.GONE

            }
        }
    }

    private fun updateQty(position: Int, model: Item) {
       /* list[position].qty = model.qty
        this.notifyItemChanged(position)*/
        Utils.saveItem(list[position])
    }

    private fun updateQty(model: Item) {
        /* list[position].qty = model.qty
         this.notifyItemChanged(position)*/
        Utils.saveItem(model)
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

    open fun updateList(list: List<Item>?) {

    }
}