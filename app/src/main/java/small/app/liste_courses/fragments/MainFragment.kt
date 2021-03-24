package small.app.liste_courses.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import small.app.liste_courses.MainActivity
import small.app.liste_courses.adapters.DepartmentAdapter
import small.app.liste_courses.adapters.UnclassifiedItemsAdapter
import small.app.liste_courses.databinding.FragmentMainBinding
import small.app.liste_courses.model.Department
import small.app.liste_courses.model.MainViewModel
import small.app.liste_courses.room.entities.Item


/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
//TODO : problème lorsque le nom de l'item est grand le bouton réduit de taille
//TODO : ne pas autoriser l'espace dans le nom de l'item
class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding
    private lateinit var activity: MainActivity

    private lateinit var model: MainViewModel

    private lateinit var unclassifiedAdapter: UnclassifiedItemsAdapter

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


            model.updateItemsList(item)

            binding.actvSelectionItem.setText("")
        }


        //Create the items adapter
        unclassifiedAdapter =
            UnclassifiedItemsAdapter(
                requireContext(),
                model.unclassifiedItems
            )


        //Setup the items recycler view
        binding.rvUnclassifiedItems.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvUnclassifiedItems.adapter = unclassifiedAdapter

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

        //Create the department adapter
        departmentsAdapter = DepartmentAdapter(
            requireContext(),
            model.departments,
            model
        )

        //Setup departments recycler view
        binding.rvDepartment.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvDepartment.adapter = departmentsAdapter



        model.newDepartment.observe(viewLifecycleOwner, Observer { newValue ->
            if (newValue) {
                mainScope.launch {
                    departmentsAdapter.notifyDataSetChanged()
                    model.newDepartment.value = false
                }
            }
        })

        binding.ibAddDepartment.setOnClickListener {
            //Create the new department
            // Need to check if it exist first because we don't want to override an existing department
            val dep = Department(binding.etDepartmentName.text.toString(), ArrayList())
            model.updateDepartmentsList(dep)

            binding.etDepartmentName.setText("")
        }


        /*val ddHelper = ItemTouchHelper(DragAndDropHelper())

         ddHelper.attachToRecyclerView(binding.rvDepartment)
         ddHelper.attachToRecyclerView(binding.rvUnclassifiedItems)*/

        // Inflate the layout for this fragment
        return binding.root
    }


}