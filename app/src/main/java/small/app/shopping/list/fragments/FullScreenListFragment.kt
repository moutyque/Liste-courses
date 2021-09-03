package small.app.shopping.list.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import small.app.shopping.list.adapters.DepartmentsFullScreenAdapter
import small.app.shopping.list.databinding.FragmentParamsBinding
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.viewmodels.FragmentViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FullScreenListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FullScreenListFragment : Fragment() {
    private lateinit var binding: FragmentParamsBinding
    lateinit var adapter: DepartmentsFullScreenAdapter
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
        adapter = DepartmentsFullScreenAdapter(
            requireContext()
        )


        //Setup departments recycler view
        binding.rvDepartments.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvDepartments.adapter = adapter

        viewModel.getUsedDepartment().observe(viewLifecycleOwner, {


            if (it?.isEmpty() == true) {
                binding.rvDepartments.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE


            } else {

                binding.rvDepartments.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE

                adapter.updateList(Utils.getDepartmentWithUsedItems(it).toList())
            }


        })



    }
}