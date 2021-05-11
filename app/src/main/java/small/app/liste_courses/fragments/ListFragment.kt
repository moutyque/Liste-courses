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
import small.app.liste_courses.databinding.FragmentMainBinding
import small.app.liste_courses.models.Department
import small.app.liste_courses.objects.Scope.backgroundScope
import small.app.liste_courses.objects.Utils
import small.app.liste_courses.objects.Utils.repo
import small.app.liste_courses.room.entities.DepartmentWithItems
import small.app.liste_courses.room.entities.Item
import small.app.liste_courses.viewmodels.FragmentViewModel
import java.util.*
import kotlin.collections.ArrayList


class ListFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private lateinit var viewModel: FragmentViewModel

    private lateinit var unclassifiedAdapter: ItemsAdapter

    lateinit var departmentsAdapter: DepartmentsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("", "Oncreate")
        super.onCreate(savedInstanceState)
    }

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
        viewModel.getUnusedItemsName().observe(viewLifecycleOwner, {
            suggestedItemsAdapter.clear()
            suggestedItemsAdapter.addAll(it)

        })

        binding.actvSelectionItem.setAdapter(suggestedItemsAdapter)

        //Setup btn to add an new item
        binding.ibAddItem.setOnClickListener {
            //Create or get the item
            val name = binding.actvSelectionItem.text.toString().trim()
            if (name.isNotEmpty()) {
                val item = Item(name = name)
                item.isUsed = true
                item.order = System.currentTimeMillis()
                Utils.useItem(item, departmentsAdapter)
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


        viewModel.getUnusedDepartmentsName().observe(viewLifecycleOwner, {
            suggestedDepartmentsAdapter.clear()
            suggestedDepartmentsAdapter.addAll(it)
        }
        )

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
                            0,
                            order
                        )
                    dep.isUsed = true
                    Utils.saveDepartment(dep)

                }
                binding.actDepartmentName.setText("")
            }

        }
        // Inflate the layout for this fragment
        return binding.root
    }


    private fun setupDepartmentsRV() {
        //Create the department adapter
        departmentsAdapter = DepartmentsAdapter(
            requireContext()
        )


        //Setup departments recycler view
        binding.rvDepartment.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDepartment.adapter = departmentsAdapter

        val callback = SimpleItemTouchHelperCallback(departmentsAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvDepartment)

        viewModel.getUsedDepartment().observe(viewLifecycleOwner, {
            val mlist = mutableListOf<DepartmentWithItems>()
            it?.forEach { dep ->
                val local_dep = dep
                val local_items = mutableListOf<Item>()
                dep.items.forEach { item ->
                    if (item.isUsed) local_items.add(item)
                }
                local_dep.items = local_items
                mlist.add(local_dep)
            }
            departmentsAdapter.updateList(mlist.toList())
        })

    }

    private fun setupUnclassifiedItemsRV() {
        //Create the items adapter
        unclassifiedAdapter =
            UnclassifiedItemsAdapter(
                requireContext(),
                false
            )

        viewModel.getUnclassifiedItems().observe(viewLifecycleOwner, { items ->
            unclassifiedAdapter.updateList(items)
        })


        //Setup the items recycler view
        binding.rvUnclassifiedItems.layoutManager =
            LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL, false
            )
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
            val dragFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(dragFlags, 0)
        }


        override fun onMove(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {

            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition

            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(mAdapter.list, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(mAdapter.list, i, i - 1)
                }
            }
            mAdapter.notifyItemMoved(fromPosition, toPosition)
            mAdapter.onItemMove(fromPosition, toPosition)
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