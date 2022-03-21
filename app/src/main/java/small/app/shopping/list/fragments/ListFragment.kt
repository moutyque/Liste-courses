package small.app.shopping.list.fragments

import android.R
import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import small.app.shopping.list.adapters.DepartmentsListAdapter
import small.app.shopping.list.adapters.ItemsAdapter
import small.app.shopping.list.adapters.UnclassifiedItemsAdapter
import small.app.shopping.list.databinding.FragmentListBinding
import small.app.shopping.list.models.Department
import small.app.shopping.list.objects.Scope.backgroundScope
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.objects.Utils.repo
import small.app.shopping.list.viewmodels.FragmentViewModel


class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding

    private lateinit var viewModel: FragmentViewModel

    private lateinit var unclassifiedAdapter: ItemsAdapter

    lateinit var departmentsListAdapter: DepartmentsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Create the binding
        binding = FragmentListBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this).get(FragmentViewModel::class.java)

        initItemNameSuggestion()

        initAddItem()

        setupUnclassifiedItemsRV()
        setupDepartmentsRV()

        //Setup the department name suggestion in the text field
        initDepartmentNameSuggestion()

        initAddDepartment()

        // Inflate the layout for this fragment
        return binding.root
    }

    /**
     * Create an ArrayAdapter that contains the name of all the unused items and use it to suggest items to add in the shopping list
     */
    private fun initItemNameSuggestion() {
        val itemsName: ArrayList<String> = ArrayList()
        val suggestedItemsAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.simple_dropdown_item_1line,
            itemsName
        )

        //Setup the autocomplete item list
        viewModel.getUnusedItemsName().observe(viewLifecycleOwner, {
            suggestedItemsAdapter.clear()
            suggestedItemsAdapter.addAll(it)

        })

        binding.actvSelectionItem.setAdapter(suggestedItemsAdapter)
    }

    /**
     * Initialize the two ways to add an item from the text field.
     * 1) By clicking on the icon check
     * 2) By pressing enter
     */
    private fun initAddItem() {
        //Setup btn to add an new item
        binding.ibAddItem.setOnClickListener {
            addItem()
        }

        //Enable the enter button to add items
        binding.actvSelectionItem.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                addItem()
                true
            }
            false
        }
    }

    /**
     * Initialize the two ways to add a department from the text field.
     * 1) By clicking on the icon check
     * 2) By pressing enter
     */
    private fun initAddDepartment() {
        //Setup btn to add an new department
        binding.ibAddDepartment.setOnClickListener {
            addDepartment()
        }

        binding.actDepartmentName.setOnKeyListener { _, keyCode, event ->
            if (KeyEvent.KEYCODE_ENTER == keyCode && event.action == KeyEvent.ACTION_UP) {
                addDepartment()
            }

            true
        }
    }

    /**
     * Create an ArrayAdapter that contains the name of all the unused departments and use it to suggest items to add in the shopping list
     */
    private fun initDepartmentNameSuggestion() {
        val departmentsName: ArrayList<String> = ArrayList()
        val suggestedDepartmentsAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.simple_dropdown_item_1line,
            departmentsName
        )
        binding.actDepartmentName.setAdapter(suggestedDepartmentsAdapter)


        viewModel.getUnusedDepartmentsName().observe(viewLifecycleOwner, {
            suggestedDepartmentsAdapter.clear()
            suggestedDepartmentsAdapter.addAll(it)
        }
        )
    }

    /**
     * Create a new department from the name in the text field and db info and make it visible
     * or
     * Reuse an existing department with the same name as the one set in the text field
     */
    private fun addDepartment() {
        //Create the new department
        val depName = binding.actDepartmentName.text.toString().trim()
        if (depName.isNotEmpty()) {
            // Need to check if it exist first because we don't want to override an existing department
            var order = 0
            var depDb: Department? = null
            val job = backgroundScope.launch {
                order = repo.getNumberOfDepartments()
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
            Toast.makeText(requireContext(), "$depName has been added.", Toast.LENGTH_LONG).show()
            binding.actDepartmentName.setText("")
        }
    }

    /**
     * Create a new unclassified item from the name in the text field then use an util function to see if it already exist and do the necessary actions.
     */
    private fun addItem() {
        //Create or get the item
        viewModel.addItem(binding.actvSelectionItem.text.toString().trim())
        binding.actvSelectionItem.setText("")
        Utils.hideSoftKeyboard(context as Activity)
    }

    /**
     * Setup on the department recycler view :
     * The layout
     * The action to be performed when a new list of DepartmentWithItems is provided
     */
    private fun setupDepartmentsRV() {
        //Create the department adapter
        departmentsListAdapter = DepartmentsListAdapter(
            requireContext()
        )


        //Setup departments recycler view
        binding.rvDepartment.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDepartment.adapter = departmentsListAdapter

        viewModel.getUsedDepartment().observe(viewLifecycleOwner, {
            departmentsListAdapter.updateList(Utils.getFilteredDepartmentWithItems(it).toList())
        })

    }

    /**
     * Setup on the unclassified items recycler view :
     * The layout
     * The action to be performed when a new list of DepartmentWithItems is provided
     */
    private fun setupUnclassifiedItemsRV() {
        //Create the items adapter
        unclassifiedAdapter =
            UnclassifiedItemsAdapter(
                requireContext()
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


}