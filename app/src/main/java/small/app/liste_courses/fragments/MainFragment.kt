package small.app.liste_courses.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import small.app.liste_courses.MainActivity
import small.app.liste_courses.adapters.DepartmentAdapter
import small.app.liste_courses.adapters.IOnAdapterChangeListener
import small.app.liste_courses.adapters.ItemsAdapter
import small.app.liste_courses.adapters.ObjectChange
import small.app.liste_courses.databinding.FragmentMainBinding
import small.app.liste_courses.model.Department
import small.app.liste_courses.model.MainViewModel
import small.app.liste_courses.room.entities.Item


/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
//TODO : Si perte de focus clear le nom ?
//TODO : ne pas autoriser les retour à la ligne dans le nom de l'item
class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding
    private lateinit var activity: MainActivity

    private lateinit var model: MainViewModel

    private lateinit var unclassifiedAdapter: ItemsAdapter

    private lateinit var departmentsAdapter: DepartmentAdapter

    private val mainScope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        activity = requireActivity() as MainActivity
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Create the binding
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        //Create the model
        model = MainViewModel(activity.repo)
        binding.model = model

        //Mange the autocompletion field
        binding.actvSelectionItem.validator = object : AutoCompleteTextView.Validator {
            override fun isValid(text: CharSequence?): Boolean {
                return !text?.contains("\n")!!
            }

            override fun fixText(invalidText: CharSequence?): CharSequence {
                return invalidText!!.toString().replace("\n", "")
            }

        }
        //Setup the autocomplete item list
        model.autoCompleteItems.observe(viewLifecycleOwner, Observer { list ->
            val toTypedArray: Array<String> = list.map { i -> i.name }.toTypedArray()
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line, toTypedArray
            )
            binding.actvSelectionItem.setAdapter(adapter)
        })


        //Setup btn to add an new item
        binding.ibAddItem.setOnClickListener {
            //Create or get the item
            val name = binding.actvSelectionItem.text.toString()
            val item = if (model.autoCompleteItems.value!!.map { i -> i.name }.contains(name)) {
                model.autoCompleteItems.value!!.filter { item -> item.name == name }.get(0)
            } else {
                Item(name = name)
            }
            item.isUsed = true
            item.order = model.unclassifiedItems.size.toLong()

            model.createItem(item)

            binding.actvSelectionItem.setText("")
        }


        setUpUnclassifiedItemsRV()

        //Update the list of items to be displayed in the rv and in the autocompletion
        model.updateItemsList()
        model.newItems.observe(viewLifecycleOwner, Observer { newValue ->
            if (newValue) {
                mainScope.launch {
                    unclassifiedAdapter.notifyDataSetChanged()
                    model.newItems.value = false
                }
            }
        })

        model.updateDepartmentsList()

        setupDepartmentsRV()

        model.newDepartment.observe(viewLifecycleOwner, Observer { newValue ->
            if (newValue) {
                mainScope.launch {
                    model.departments.sortBy { d -> d.order }
                    departmentsAdapter.notifyDataSetChanged()
                    model.newDepartment.value = false
                }
            }
        })

        binding.ibAddDepartment.setOnClickListener {
            //Create the new department
            // Need to check if it exist first because we don't want to override an existing department
            val dep = Department(
                binding.etDepartmentName.text.toString(),
                ArrayList(),
                model.departments.size
            )
            model.updateDepartmentsList(dep)

            binding.etDepartmentName.setText("")
        }


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupDepartmentsRV() {
        //Create the department adapter
        departmentsAdapter = DepartmentAdapter(
            requireContext(),
            model.departments
        )

        val departmentChange = object : IOnAdapterChangeListener<Department, DepartmentAdapter> {

            private lateinit var adapter: DepartmentAdapter
            override fun onObjectCreated(a: Department) {
            }

            override fun onItemUpdate(a: Department, position: Int, code: ObjectChange) {
                when (code) {
                    ObjectChange.CLASSIFIED -> {
                        //A new item has been classified in this department
                        model.updateDepartmentsList(a)
                    }
                }
            }

            override fun onItemDelete(a: Department) {
            }

            override fun setAdapter(adapter: DepartmentAdapter) {
                this.adapter = adapter
            }

            override fun getAdapter(): DepartmentAdapter {
                return adapter
            }


        }

        departmentsAdapter.IOnDepartmentChangeListener = departmentChange

        val classifiedItemChange = object : IOnAdapterChangeListener<Item, ItemsAdapter> {

            private lateinit var depItemsAdapter: ItemsAdapter

            override fun onObjectCreated(a: Item) {
                model.updateItemsList(a)
            }

            override fun onItemUpdate(a: Item, position: Int, code: ObjectChange) {
                when (code) {

                    ObjectChange.CLASSIFIED -> {
                        model.updateView(a) // Need to refresh the two lists
                    }
                    ObjectChange.QTY -> {
                        model.updateItem(a)
                        getAdapter().notifyItemChanged(position)
                    }
                    ObjectChange.USED -> {
                        //Validate inside a department
                        model.updateItem(a)
                        //model.updateDepartmentsList()//TODO : ok mais perfo ?
                        //getAdapter().getList().removeAt(position)
                        getAdapter().notifyDataSetChanged()
                    }
                    else -> Log.e("ItemChange", "Unknown or unused code has been send : $code")
                }
                //model.updateView(a)
            }


            override fun onItemDelete(a: Item) {
            }

            override fun setAdapter(adapter: ItemsAdapter) {
                depItemsAdapter = adapter
            }

            override fun getAdapter(): ItemsAdapter {
                return depItemsAdapter
            }
        }

        departmentsAdapter.IOnItemChangeListener = classifiedItemChange

        //Setup departments recycler view
        binding.rvDepartment.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvDepartment.adapter = departmentsAdapter

        val callback = SimpleItemTouchHelperCallback(departmentsAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvDepartment)
    }

    private fun setUpUnclassifiedItemsRV() {
        //Create the items adapter
        unclassifiedAdapter =
            ItemsAdapter(
                requireContext(),
                model.unclassifiedItems,
                false
            )

        val unclassifiedItemChange = object : IOnAdapterChangeListener<Item, ItemsAdapter> {

            private lateinit var itemsAdapter: ItemsAdapter

            override fun onObjectCreated(a: Item) {
                model.updateItemsList(a)
            }

            override fun onItemUpdate(a: Item, position: Int, code: ObjectChange) {
                when (code) {
                    ObjectChange.USED -> {
                        if (a.isClassified) {
                            model.updateView(a) //Full refresh cause we also need to refresh the department
                        } else {//New item added to the unclassified list
                            model.updateItem(a)
                            getAdapter().notifyItemChanged(position)
                        }
                    }
                    ObjectChange.CLASSIFIED -> {
                        model.updateView(a) // Need to refresh the two lists
                    }
                    ObjectChange.QTY -> {
                        model.updateItem(a)
                        getAdapter().notifyItemChanged(position)
                    }
                    else -> Log.e("ItemChange", "Unknown code has been send : $code")
                }
                //model.updateView(a)
            }


            override fun onItemDelete(a: Item) {
            }

            override fun setAdapter(adapter: ItemsAdapter) {
                itemsAdapter = adapter
            }

            override fun getAdapter(): ItemsAdapter {
                return itemsAdapter
            }
        }
        unclassifiedItemChange.setAdapter(unclassifiedAdapter)

        unclassifiedAdapter.IOnAdapterChangeListener = unclassifiedItemChange


        //Setup the items recycler view
        binding.rvUnclassifiedItems.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvUnclassifiedItems.adapter = unclassifiedAdapter
    }

    fun updateDepartmentList(item: Any) {
        if (item is Department)
            model.updateDepartmentsList(item)
    }

    class SimpleItemTouchHelperCallback(adapter: DepartmentAdapter) :
        ItemTouchHelper.Callback() {
        private val mAdapter: DepartmentAdapter
        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            return true
        }

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val swipeFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val dragFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            Log.d(
                "DDSwipe",
                "In the onMove methode from ${viewHolder.adapterPosition} to ${target.adapterPosition}"
            )

            mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
            mAdapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            Log.d("DDSwipe", "In the onSwipe")
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder?.itemView?.alpha = 0.5f
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {

            super.clearView(recyclerView, viewHolder)
            viewHolder?.itemView?.alpha = 1.0f
        }


        init {
            mAdapter = adapter
        }
    }

}