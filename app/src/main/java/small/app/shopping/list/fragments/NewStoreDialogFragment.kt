package small.app.shopping.list.fragments

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import small.app.shopping.list.databinding.DialogNewStoreBinding
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.room.Repository
import small.app.shopping.list.viewmodels.FragmentViewModel

class NewStoreDialogFragment(private val repo: Repository) : DialogFragment() {
    private lateinit var binding: DialogNewStoreBinding

    private lateinit var viewModel: FragmentViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogNewStoreBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = FragmentViewModel(requireContext().applicationContext as Application,
            repo
        )

        binding.bValidItemName.setOnClickListener {
            //Add item
            viewModel.addStore(binding.actStoreName.text.toString().trim())
            binding.actStoreName.setText("")
            Utils.hideKeyboardFrom(requireActivity(), binding.root)
            //Close dialog
            dismiss()

        }

        return binding.root
    }

    companion object {
        const val TAG = "NewStoreConfirmationDialog"
    }

}
