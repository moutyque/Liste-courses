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
import small.app.liste_courses.model.MainViewModel
import small.app.liste_courses.room.entities.Item

class UnclassifiedItemsAdapter(
    private val context: Context,
    private var list: List<Item>,
    private val viewModel: MainViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_grossery_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        Log.d("IAdapter", model.name)
        if (holder is ItemsViewHolder && model.name.isNotEmpty()) {
            if (model.isUsed) {
                holder.itemView.tv_name.text = model.name

                if (model.isClassified) {
                    holder.itemView.iv_check_item.visibility = View.VISIBLE

                } else {
                    holder.itemView.iv_check_item.visibility = View.GONE

                }
                holder.itemView.iv_check_item.setOnClickListener {
                    model.isUsed = false
                    viewModel.updateView(model)
                }

                holder.itemView.tv_name.setOnLongClickListener(View.OnLongClickListener { view ->
                    Log.d("LongClick", "Click hold")
                    val clipText = "This is our ClipData text"
                    val item = ClipData.Item(clipText)
                    val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    val data = ClipData(clipText, mimeTypes, item)

                    val dragShadowBuilder = View.DragShadowBuilder(view)//shadowView
                    view.startDragAndDrop(data, dragShadowBuilder, model, 0)
                    true
                })
            } else {
                holder.itemView.visibility = View.GONE
            }


        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ItemsViewHolder(view: View) : RecyclerView.ViewHolder(view)

}