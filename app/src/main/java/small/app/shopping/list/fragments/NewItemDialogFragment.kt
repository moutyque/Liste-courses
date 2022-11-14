package small.app.shopping.list.fragments

import android.R
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import small.app.shopping.list.databinding.DialogNewItemBinding
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.room.Repository
import small.app.shopping.list.viewmodels.FragmentViewModel

class NewItemDialogFragment(private val depName: String, private val storeName: String,private val repo: Repository) :
    DialogFragment() {
    private lateinit var binding: DialogNewItemBinding

    private lateinit var viewModel: FragmentViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogNewItemBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = FragmentViewModel(requireContext().applicationContext as Application,
            repo
        )

        val itemsName: ArrayList<String> = ArrayList()
        val suggestedItemsAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.simple_dropdown_item_1line,
            itemsName
        )

        //Setup the autocomplete item list
        viewModel.getUnusedItemsNameInDepartment(depName).observe(viewLifecycleOwner) {
            suggestedItemsAdapter.clear()
            suggestedItemsAdapter.addAll(it)

        }

        binding.actItemName.setAdapter(suggestedItemsAdapter)

        binding.bValidItemName.setOnClickListener {
            //Add item
            viewModel.addItem(binding.actItemName.text.toString().trim(), depName, storeName)
            binding.actItemName.setText("")
            Utils.hideKeyboardFrom(requireActivity(), binding.root)
            //Close dialog
            dismiss()

        }

        return binding.root
    }

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }

}
