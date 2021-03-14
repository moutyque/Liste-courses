package small.app.liste_courses.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import small.app.liste_courses.MainActivity
import small.app.liste_courses.UnclassifiedItemsAdapter
import small.app.liste_courses.databinding.FragmentMainBinding
import small.app.liste_courses.room.entities.Item


/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding
    private lateinit var activity: MainActivity
    var autoCompleteItems = MutableLiveData<List<Item>>()
    var unclassifiedItems: ArrayList<Item> = ArrayList()
    private lateinit var unclassifiedAdapter: UnclassifiedItemsAdapter
    private val backgroundScope = CoroutineScope(Job() + Dispatchers.IO)
    private val mainScope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        activity = requireActivity() as MainActivity
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        unclassifiedAdapter = UnclassifiedItemsAdapter(requireContext(), unclassifiedItems)

        autoCompleteItems.observe(viewLifecycleOwner, Observer { list ->
            val toTypedArray: Array<String> = list.map { i -> i.name }.toTypedArray()
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line, toTypedArray
            )
            binding.actvSelectionItem.setAdapter(adapter)
        })

        binding.ibAddItem.setOnClickListener {
            val name = binding.actvSelectionItem.text.toString()
            val item = if (autoCompleteItems.value!!.map { i -> i.name }.contains(name)) {
                autoCompleteItems.value!!.filter { item -> item.name == name }.get(0)
            } else {
                Item(name = name)
            }
            item.isUsed = true
            backgroundScope.launch {
                activity.repo.addItem(item)
                updateItemsList()
            }
            binding.actvSelectionItem.setText("")


        }
        binding.rvUnclassifiedItems.layoutManager =
            LinearLayoutManager(requireContext())
        binding.rvUnclassifiedItems.visibility = View.VISIBLE
        binding.rvUnclassifiedItems.setHasFixedSize(true)
        binding.rvUnclassifiedItems.adapter = unclassifiedAdapter


        updateItemsList()


        // Inflate the layout for this fragment
        return binding.root
    }

    /**
     * Remove an item from autoComplete and perhaps add it to unclassifiedItem
     * //TODO Optimization can be done by checking if the item that we get from the list as already been classified
     */
    private fun updateItemsList() {
        val job = backgroundScope.launch {
            Log.d("MainFragment", "updateItemsList")
            val list = activity.repo.getUnusedItems()
            mainScope.launch { autoCompleteItems.value = list }
            unclassifiedItems.clear()
            unclassifiedItems.addAll(activity.repo.getUnclassifiedItem())

            Log.d("MainFragment", "autoCompleteItems size ${list.size}")
            Log.d("MainFragment", "unclassifiedItems size ${unclassifiedItems.size}")
        }
        job.invokeOnCompletion {
            mainScope.launch {
                //unclassifiedAdapter = UnclassifiedItemsAdapter(requireContext(), unclassifiedItems)
                //binding.rvUnclassifiedItems.adapter = unclassifiedAdapter
                //unclassifiedAdapter.updateData(unclassifiedItems)
                unclassifiedAdapter.notifyDataSetChanged()

            }
        }


    }

    fun refreshFragment() {

        this.parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
    }

}