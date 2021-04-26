package small.app.liste_courses.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import small.app.liste_courses.adapters.DepartmentsAdapter
import small.app.liste_courses.adapters.ItemsAdapter
import small.app.liste_courses.adapters.UnclassifiedItemsAdapter
import small.app.liste_courses.adapters.listeners.IItemUsed
import small.app.liste_courses.databinding.FragmentMainBinding
import small.app.liste_courses.models.Department
import small.app.liste_courses.objects.Scope.backgroundScope
import small.app.liste_courses.objects.Utils
import small.app.liste_courses.objects.Utils.repo
import small.app.liste_courses.room.entities.Item
import small.app.liste_courses.viewmodels.FragmentViewModel


//TODO : Si perte de focus clear le nom ?
class ListFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private lateinit var viewModel: FragmentViewModel

    private lateinit var unclassifiedAdapter: ItemsAdapter

    lateinit var departmentsAdapter: DepartmentsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Create the binding
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this).get(FragmentViewModel::class.java)

        val itemsName: ArrayList<String> = ArrayList()
        val suggestedItemsAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            itemsName
        )

        //Setup the autocomplete item list
        /* binding.actvSelectionItem.onFocusChangeListener =
             View.OnFocusChangeListener { v, hasFocus ->
                 if (hasFocus) {
                     var arr: Array<String>? = null
                     val job = backgroundScope.launch {
                         arr = repo.getUnusedItems().map { item -> item.name }.toTypedArray()
                     }
                     job.invokeOnCompletion {
                         suggestedItemsAdapter.clear()
                         suggestedItemsAdapter.addAll(*arr!!)
                     }
                 }
             }
 */
        binding.actvSelectionItem.setAdapter(suggestedItemsAdapter)

        //Setup btn to add an new item
        binding.ibAddItem.setOnClickListener {
            //Create or get the item
            val name = binding.actvSelectionItem.text.toString().trim()
            if (name.isNotEmpty()) {
                val item = Item(name = name)
                item.isUsed = true
                item.order=System.currentTimeMillis()
                Utils.useItem(item, unclassifiedAdapter, departmentsAdapter)
                binding.actvSelectionItem.setText("")
            }

        }
        setupUnclassifiedItemsRV()
        setupDepartmentsRV()

        val departmentsName: ArrayList<String> = ArrayList()
        val suggestedDepartmentsAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            departmentsName
        )
        binding.actDepartmentName.setAdapter(suggestedDepartmentsAdapter)

/*        binding.actDepartmentName.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    var arr: Array<String>? = null
                    val job = backgroundScope.launch {
                        arr = repo.getUnusedDepartments().map { dep -> dep.department.name }
                            .toTypedArray()
                    }
                    job.invokeOnCompletion {
                        suggestedDepartmentsAdapter.clear()
                        suggestedDepartmentsAdapter.addAll(*arr!!)
                    }
                }else{
                    binding.actDepartmentName.setText("")
                }
            }*/


        //Setup btn to add an new department
        binding.ibAddDepartment.setOnClickListener {
            //Create the new department
            val depName = binding.actDepartmentName.text.toString().trim()
            if (depName.isNotEmpty()) {
                // Need to check if it exist first because we don't want to override an existing department
                var order = 0
                var depDb: Department? = null
                val job = backgroundScope.launch {
                    order = repo.getAllDepartment().size
                    depDb = repo.findDepartment(depName)
                }
                job.invokeOnCompletion {

                    val dep: Department = depDb
                        ?: Department(
                            depName,
                            true,
                            ArrayList(),
                            order
                        )
                    dep.isUsed = true
                    Utils.saveDepartment(dep)

                    // Utils.keepUsedItems(dep)
                }

                //model.updateDepartmentsList(dep)

                binding.actDepartmentName.setText("")
            }

        }

        //SyncManager.setCreated(this)
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupDepartmentsRV() {
        //Create the department adapter
        departmentsAdapter = DepartmentsAdapter(
            requireContext(),
            viewModel = viewModel
        )


        //Setup departments recycler view
        binding.rvDepartment.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDepartment.adapter = departmentsAdapter

        val callback = SimpleItemTouchHelperCallback(departmentsAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvDepartment)

        //TODO: not call the first time we add an item to a department
        viewModel.getUsedDepartment().observe(viewLifecycleOwner, {
            departmentsAdapter.updateList(it)
        })

    }

    private fun setupUnclassifiedItemsRV() {
        //Create the items adapter
        unclassifiedAdapter =
            UnclassifiedItemsAdapter(
                requireContext(),
                false,
                object : IItemUsed {
                    override fun onLastItemUse() {
                        Log.d("UnclassifiedAdapter", "The last item has been used")
                    }

                    override fun onItemUse() {
                        /*unclassifiedAdapter.list.beginBatchedUpdates()
                        for (i in 0 until unclassifiedAdapter.list.size()) {
                            unclassifiedAdapter.list[i].order = i.toLong()
                            Utils.saveItem(unclassifiedAdapter.list[i])
                        }
                        unclassifiedAdapter.list.endBatchedUpdates()*/
                    }
                }
            )

        viewModel.getUnclassifiedItems().observe(viewLifecycleOwner, { items ->
            unclassifiedAdapter.updateList(items)
        })


        //Setup the items recycler view
        binding.rvUnclassifiedItems.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvUnclassifiedItems.adapter = unclassifiedAdapter
    }


    class SimpleItemTouchHelperCallback(adapter: DepartmentsAdapter) :
        ItemTouchHelper.Callback() {
        private val mAdapter: DepartmentsAdapter = adapter
        override fun isLongPressDragEnabled(): Boolean {
            Log.d("SimpleItemTouchHelperCallback", "Can u click")
            return mAdapter.canMove
        }


        override fun isItemViewSwipeEnabled(): Boolean {
            return true
        }

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val swapFlags = ItemTouchHelper.UP
            val dragFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(dragFlags, 0)
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
            //TODO : Might need to implement it later
            Log.d("DDSwipe", "In the onSwipe")
        }

        override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
            return 0.0f
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