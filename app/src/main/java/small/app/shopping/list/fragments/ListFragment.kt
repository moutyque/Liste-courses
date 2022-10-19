package small.app.shopping.list.fragments

import android.R
import android.app.Application
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import small.app.shopping.list.adapters.DepartmentsListAdapter
import small.app.shopping.list.databinding.FragmentListBinding
import small.app.shopping.list.models.Department
import small.app.shopping.list.objects.Scope.backgroundScope
import small.app.shopping.list.objects.Utils.keepWithUsedItem
import small.app.shopping.list.objects.Utils.repo
import small.app.shopping.list.objects.Utils.saveAndUse
import small.app.shopping.list.objects.Utils.setupNamesDD
import small.app.shopping.list.objects.Utils.setupStoreListener
import small.app.shopping.list.viewmodels.FragmentViewModel


class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding

    private lateinit var viewModel: FragmentViewModel

    private lateinit var departmentsListAdapter: DepartmentsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Create the binding
        binding = FragmentListBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = FragmentViewModel(requireContext().applicationContext as Application,repo)

        initAddStore()
        setupDepartmentsRV()
        setupStoreSpinner()
        setupObservers()

        //Setup the department name suggestion in the text field
        initDepartmentNameSuggestion()

        initAddDepartment()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupObservers(){
        viewModel.fetchUsedStore().observe(viewLifecycleOwner){
            viewModel.selectedStore = it?.store
        }
    }

    private fun setupStoreSpinner() {
        binding.sStoreDropdown.setupStoreListener(viewModel,viewLifecycleOwner,setupNamesDD(viewModel.fetchStoreNames()))
    }

    private fun initAddStore() {
        binding.ibAddStore.setOnClickListener {
            val activity = context as FragmentActivity
            val fm: FragmentManager = activity.supportFragmentManager
            val dialog = NewStoreDialogFragment(repo)
            dialog.show(fm, NewStoreDialogFragment.TAG)
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
            viewModel.selectedStore?.let { addDepartment(it.name) }
        }

        binding.actDepartmentName.setOnKeyListener { _, keyCode, event ->
            if (KeyEvent.KEYCODE_ENTER == keyCode && event.action == KeyEvent.ACTION_UP) {
                viewModel.selectedStore?.let { addDepartment(it.name) }
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


        viewModel.getUnusedDepartmentsName().observe(
            viewLifecycleOwner
        ) {
            suggestedDepartmentsAdapter.clear()
            suggestedDepartmentsAdapter.addAll(it)
        }
    }

    /**
     * Create a new department from the name in the text field and db info and make it visible
     * or
     * Reuse an existing department with the same name as the one set in the text field
     */
    private fun addDepartment(storeName: String) {
        //Create the new department
        val depName = binding.actDepartmentName.text.toString().trim()
        if (depName.isNotEmpty()) {
            // Need to check if it exist first because we don't want to override an existing department
            var order = 0
            var depDb: Department? = null
            val job = backgroundScope.launch {
                order = repo.getNumberOfDepartments()
                depDb = repo.findDepartment(depName, storeName)
            }
            job.invokeOnCompletion {

                val dep: Department = depDb
                    ?: Department(
                        "${depName}_$storeName",
                        depName,
                        true,
                        ArrayList(),
                        0,
                        order,
                        storeName
                    )
                dep.isUsed = true
                dep.saveAndUse()

            }
            Toast.makeText(requireContext(), "$depName has been added.", Toast.LENGTH_LONG).show()
            binding.actDepartmentName.setText("")
        }
    }

    /**
     * Setup on the department recycler view :
     * The layout
     * The action to be performed when a new list of DepartmentWithItems is provided
     */
    private fun setupDepartmentsRV() {
        //Create the department adapter
        departmentsListAdapter = DepartmentsListAdapter(
            requireContext(),repo
        )


        //Setup departments recycler view
        binding.rvDepartment.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDepartment.adapter = departmentsListAdapter

        viewModel.fetchUsedStore().observe(viewLifecycleOwner) {
            departmentsListAdapter.updateList(it?.departments?.keepWithUsedItem())
        }

    }


}