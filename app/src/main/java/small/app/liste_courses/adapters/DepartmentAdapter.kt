package small.app.liste_courses.adapters

import android.content.ClipDescription
import android.content.Context
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.item_department.view.*
import small.app.liste_courses.R
import small.app.liste_courses.adapters.listenners.IOnAdapterChangeListener
import small.app.liste_courses.model.Department
import small.app.liste_courses.room.entities.Item
import java.util.*


class DepartmentAdapter(
    private val context: Context,
    private var list: MutableList<Department>
) :
    RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder>(), IListGetter<Department> {

    lateinit var itemChangeListener: IOnAdapterChangeListener<Item, ItemsAdapter, ItemsAdapter.ItemsViewHolder>
    lateinit var departmentChangeListener: IOnAdapterChangeListener<Department, DepartmentAdapter, DepartmentViewHolder>

    var canMove = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
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

    override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {
        val model = list[position]

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
                    Log.d("DDD", "Has drop $v")
                    val localState = event.localState

                    if (localState is Item) {
                        Log.d("DDD", "Dropped ${localState.name}")
                        model.classify(localState)
                        departmentChangeListener.onItemUpdate(
                            model,
                            position,
                            list,
                            ObjectChange.CLASSIFIED
                        )
                    }
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
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

        //Perform the D&D action on department only if we click on the department title and not and the item list
        holder.itemView.tv_dep_name.setOnTouchListener { v, event ->
            Log.d("ClickOnDep", "I touched $event")

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    canMove = true
                    v.performClick()
                }
                MotionEvent.ACTION_UP -> canMove = false
            }
            true
        }
        //Recycler view for the items in the department

        Log.d("DAdapter", "department name : ${model.name} & items ${model.items}")



        val itemsList = model.items.filter { it.isUsed }//.toMutableList()
        Log.d("DAdapter","Items for this adapter $itemsList")
        val itemsAdapter = ItemsAdapter(
            context,
            itemsList.toMutableList(),
            false
        )

        holder.itemView.rv_items.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)


        itemChangeListener.setAdapter(itemsAdapter)
        itemsAdapter.IOnAdapterChangeListener = itemChangeListener
        holder.itemView.rv_items.adapter = itemsAdapter

    }


    fun onItemMove(initialPosition: Int, targetPosition: Int) {
        Collections.swap(list, initialPosition, targetPosition)

    }


    class DepartmentViewHolder(view: View) : ViewHolder(view)

    override fun getList(): MutableList<Department> {
        return list
    }


}