package small.app.liste_courses.adapters

import android.content.ClipDescription
import android.content.Context
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_department.view.*
import small.app.liste_courses.R
import small.app.liste_courses.model.Department
import small.app.liste_courses.model.MainViewModel
import small.app.liste_courses.room.entities.Item

class DepartmentAdapter(
    private val context: Context,
    private var list: List<Department>,
    private var viewModel: MainViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onDropAction: Unit? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DepartmentViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_department,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is DepartmentViewHolder) {
            holder.itemView.tv_dep_name.text = model.name

//Manage the drop action

            // Creates a new drag event listener
            val dragListen = View.OnDragListener { v, event ->

                // Handles each of the expected events
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        // Determines if this View can accept the dragged data
                        event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    }
                    DragEvent.ACTION_DRAG_ENTERED -> {

                        true
                    }

                    DragEvent.ACTION_DRAG_LOCATION ->
                        // Ignore the event
                        true
                    DragEvent.ACTION_DRAG_EXITED -> {

                        true
                    }
                    DragEvent.ACTION_DROP -> {
                        Log.d("DDD", "Has drop")
                        val item = event.clipData.getItemAt(0)

                        val localState = event.localState

                        if (localState is Item) {
                            Log.d("DDD", "Dropped ${localState.name}")
                            model.classify(localState)
                            viewModel.updateDepartmentsList(model)
                        }
                        //val dragData = item.text
                        // Toast.makeText(context, dragData, Toast.LENGTH_LONG).show()


                        // Returns true. DragEvent.getResult() will return true.
                        true
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        // Turns off any color tinting


                        // Does a getResult(), and displays what happened.
                        when (event.result) {
                            true ->
                                Toast.makeText(context, "The drop was handled.", Toast.LENGTH_LONG)
                            else ->
                                Toast.makeText(context, "The drop didn't work.", Toast.LENGTH_LONG)
                        }.show()

                        // returns true; the value is ignored.
                        true
                    }
                    else -> {
                        // An unknown action type was received.
                        Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
                        false
                    }
                }
            }


            holder.itemView.setOnDragListener(dragListen)


            //Recycler view for the items in the department

            Log.d("DAdapter", "departement name : ${model.name} & items ${model.items}")

            holder.itemView.rv_items.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            holder.itemView.rv_items.adapter = UnclassifiedItemsAdapter(
                context,
                model.items.filter { it -> it.isUsed },
                viewModel
            )


        }
    }

    class DepartmentViewHolder(view: View) : RecyclerView.ViewHolder(view)


}