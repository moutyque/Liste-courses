package small.app.liste_courses.adapters

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_grossery_item.view.*
import small.app.liste_courses.R
import small.app.liste_courses.adapters.listenners.IOnAdapterChangeListener
import small.app.liste_courses.room.entities.Item

class ItemsAdapter(
    private val context: Context,
    private var list: MutableList<Item>,
    private val canChangeUnit: Boolean
) :
    RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>(), IListGetter<Item> {

    var IOnAdapterChangeListener: IOnAdapterChangeListener<Item, ItemsAdapter, ItemsViewHolder>? = null
        set(value) {
            field = value
            field?.setAdapter(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        return ItemsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_grossery_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        val model = list[position]
        Log.d("IAdapter", model.name)
        Log.d("IAdapter"," $position")
        if ( model.name.isNotEmpty()) {
            if (model.isUsed) {
                holder.itemView.tv_name.text = model.name
                Log.d("IAdapter", "TvName start binding: ${holder.itemView.width}")
                if (model.isClassified) {
                    holder.itemView.iv_check_item.visibility = View.VISIBLE

                } else {
                    holder.itemView.iv_check_item.visibility = View.GONE
                }

                holder.itemView.iv_check_item.setOnClickListener {
                    model.isUsed = false
                    //Update RV
                    Log.d("IAdapter", "Remove at position : $position")
                    //list.removeAt(position)
                    //Update db
                    IOnAdapterChangeListener!!.onItemUpdate(model, position, list,ObjectChange.USED)
                    list.remove(model)
                    this.notifyItemRemoved(position)

                }
                //Manage the view of the drop down list of unit
                if (canChangeUnit) {
                    holder.itemView.tv_unit.visibility = View.GONE
                    holder.itemView.s_unit.visibility = View.VISIBLE

                   // holder.itemView.tv_qty.

                } else {
                    holder.itemView.tv_unit.visibility = View.VISIBLE
                    holder.itemView.s_unit.visibility = View.GONE
                }
                holder.itemView.tv_unit.text = model.unit.value

                //Manage qty
                holder.itemView.tv_qty.text = model.qty.toString()

                holder.itemView.iv_increase_qty.setOnClickListener {
                    model.qty += model.unit.mutliplicator
                    Log.d("IAdapter","List size ${list.size}")

                    updateQty(position, model)
                }
                holder.itemView.iv_decrease_qty.setOnClickListener {
                    model.qty -= model.unit.mutliplicator
                    if (model.qty < 0) {
                        model.qty = 0;
                    }

                    updateQty(position, model)
                }
//holder.itemView.tv_name
                holder.model = model
                holder.onLongClick(holder.itemView)
            } else {
                holder.itemView.visibility = View.GONE

            }


        }
        Log.d("IAdapter", "TvName end binding: ${holder.itemView.tv_name.width}")


    }

    private fun updateQty(position: Int, model: Item) {
        Log.d("IAdapter", "before change qty : ${list[position].qty}")
        IOnAdapterChangeListener!!.onItemUpdate(model, position, list, ObjectChange.QTY)
        this.notifyItemChanged(position)
        Log.d("IAdapter", "after change qty : ${list[position].qty}")
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ItemsViewHolder(view: View) : RecyclerView.ViewHolder(view) , View.OnLongClickListener, View.OnClickListener{

        var model : Item? = null

            init {
                view.setOnClickListener(this)
                view.setOnLongClickListener(this)
            }
        override fun onLongClick(v: View?): Boolean {
            v?.apply {
                Log.d("ItemsViewHolder", "Click hold on $v!!")
                Log.d("ItemsViewHolder", "Is textview visible ${v.tv_name.visibility}")
                //v.tv_name.width = ViewGroup.LayoutParams.WRAP_CONTENT

                v.tv_name.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                v.tv_name.layout(0, 0, v.tv_name.getMeasuredWidth(), v.tv_name.getMeasuredHeight());

                val clipText = "This is our ClipData text"
                val item = ClipData.Item(clipText)
                val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                val data = ClipData(clipText, mimeTypes, item)

                val dragShadowBuilder = View.DragShadowBuilder(v.tv_name)//shadowView
                v.startDragAndDrop(data, dragShadowBuilder, model, 0)
                return true
            }

            return false
        }

        override fun onClick(v: View?) {
            Log.d("ItemsViewHolder", "Click quick")        }


    }

    override fun getList(): MutableList<Item> {
        Log.d("IAdapter","Get the list of size ${list.size}")
        return list
    }

}