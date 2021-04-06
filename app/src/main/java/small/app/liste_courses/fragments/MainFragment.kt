package small.app.liste_courses.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import small.app.liste_courses.MainActivity
import small.app.liste_courses.adapters.DepartmentAdapter
import small.app.liste_courses.adapters.listenners.IOnAdapterChangeListener
import small.app.liste_courses.adapters.ItemsAdapter
import small.app.liste_courses.adapters.ObjectChange
import small.app.liste_courses.adapters.listenners.DepartmentChangeListener
import small.app.liste_courses.adapters.listenners.ClassifiedItemChangeListener
import small.app.liste_courses.adapters.listenners.UnclassifiedItemChangeListener
import small.app.liste_courses.databinding.FragmentMainBinding
import small.app.liste_courses.model.Department
import small.app.liste_courses.model.MainViewModel
import small.app.liste_courses.room.entities.Item



//TODO : Si perte de focus clear le nom ?
//TODO : ne pas autoriser les retour à la ligne dans le nom de l'item
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
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
    ): View {

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
        model.autoCompleteItems.observe(viewLifecycleOwner, { list ->
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
            val name = binding.actvSelectionItem.text.toString().trim()
            if(name.isNotEmpty()){
                val item = if (model.autoCompleteItems.value!!.map { i -> i.name }.contains(name)) {
                    model.autoCompleteItems.value!!.filter { item -> item.name == name }[0]
                } else {
                    Item(name = name)
                }
                item.isUsed = true
                item.order = model.unclassifiedItems.size.toLong()

                model.createItem(item)

                binding.actvSelectionItem.setText("")
            }

        }


        setUpUnclassifiedItemsRV()

        //Update the list of items to be displayed in the rv and in the autocompletion
        model.updateItemsList()
        model.newItems.observe(viewLifecycleOwner, { newValue ->
            if (newValue) {
                mainScope.launch {
                    unclassifiedAdapter.notifyDataSetChanged()
                    model.newItems.value = false
                }
            }
        })

        model.updateDepartmentsList()

        setupDepartmentsRV()

        model.newDepartment.observe(viewLifecycleOwner,  { newValue ->
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
            if(binding.etDepartmentName.text.toString().trim().isNotEmpty()){
                // Need to check if it exist first because we don't want to override an existing department
                val dep = Department(
                    binding.etDepartmentName.text.toString(),
                    ArrayList(),
                    model.departments.size
                )
                model.updateDepartmentsList(dep)

                binding.etDepartmentName.setText("")
            }

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



        departmentsAdapter.IOnDepartmentChangeListener = DepartmentChangeListener(model)

        departmentsAdapter.IOnItemChangeListener = ClassifiedItemChangeListener(model)

        //Setup departments recycler view
        binding.rvDepartment.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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




        unclassifiedAdapter.IOnAdapterChangeListener = UnclassifiedItemChangeListener(model,unclassifiedAdapter)


        //Setup the items recycler view
        binding.rvUnclassifiedItems.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvUnclassifiedItems.adapter = unclassifiedAdapter
    }


    class SimpleItemTouchHelperCallback(adapter: DepartmentAdapter) :
        ItemTouchHelper.Callback() {
        private val mAdapter: DepartmentAdapter = adapter
        override fun isLongPressDragEnabled(): Boolean {
            Log.d("SimpleItemTouchHelperCallback","Can u click")
            return mAdapter.canMove
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
                mAdapter.canMove = false
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {

            super.clearView(recyclerView, viewHolder)
            viewHolder.itemView.alpha = 1.0f
        }


    }

}