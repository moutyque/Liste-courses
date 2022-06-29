package small.app.shopping.list.fragments

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import small.app.shopping.list.databinding.DialogNewItemsBinding
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.viewmodels.FragmentViewModel
import java.util.*

class NewItemsDialogFragment(private val depName: String) : DialogFragment() {
    private lateinit var binding: DialogNewItemsBinding

    private lateinit var viewModel: FragmentViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogNewItemsBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this)[FragmentViewModel::class.java]

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

        binding.actItemNameInDep.setAdapter(suggestedItemsAdapter)

        binding.bValidItemName.setOnClickListener {
            //Add item
            viewModel.addItem(binding.actItemNameInDep.text.toString().trim(), depName)
            binding.actItemNameInDep.setText("")
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
