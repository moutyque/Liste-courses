package small.app.shopping.list.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import small.app.shopping.list.adapters.DepartmentsParamsAdapter
import small.app.shopping.list.callback.SimpleItemTouchHelperCallback
import small.app.shopping.list.databinding.FragmentParamsBinding
import small.app.shopping.list.viewmodels.FragmentViewModel

class ParamsFragment : Fragment() {

    private lateinit var binding: FragmentParamsBinding
    lateinit var departmentsAdapter: DepartmentsParamsAdapter
    private lateinit var viewModel: FragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentParamsBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(FragmentViewModel::class.java)
        setupDepartmentsRV()
        return binding.root
    }

    private fun setupDepartmentsRV() {
        //Create the department adapter
        departmentsAdapter = DepartmentsParamsAdapter(
            requireContext()
        )

        //Setup departments recycler view
        binding.rvDepartments.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvDepartments.adapter = departmentsAdapter

        viewModel.getAllDepartments().observe(viewLifecycleOwner, {


            if (it?.isEmpty() == true) {
                binding.rvDepartments.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                binding.rvDepartments.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
                departmentsAdapter.updateList(it)
            }


        })


        val callback = SimpleItemTouchHelperCallback(
            departmentsAdapter,
            SimpleItemTouchHelperCallback.Direction.VERTICAL
        )
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvDepartments)


    }


}