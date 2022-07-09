package small.app.shopping.list.fragments

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import small.app.shopping.list.R
import small.app.shopping.list.adapters.DepartmentsParamsAdapter
import small.app.shopping.list.callback.SimpleItemTouchHelperCallback
import small.app.shopping.list.databinding.FragmentParamsBinding
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.objects.Utils.TAG
import small.app.shopping.list.objects.Utils.save
import small.app.shopping.list.objects.Utils.saveAndUse
import small.app.shopping.list.objects.Utils.setupNamesDD
import small.app.shopping.list.objects.Utils.setupStoreListener
import small.app.shopping.list.room.converters.DepartmentConverter
import small.app.shopping.list.room.converters.ItemConverter
import small.app.shopping.list.room.converters.StoreConverter
import small.app.shopping.list.viewmodels.FragmentViewModel
import java.io.*

class ParamsFragment : Fragment() {

    private lateinit var binding: FragmentParamsBinding
    private lateinit var departmentsAdapter: DepartmentsParamsAdapter
    private lateinit var viewModel: FragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentParamsBinding.inflate(inflater)
        viewModel =
            FragmentViewModel(requireContext().applicationContext as Application, Utils.repo)
        setupDepartmentsRV()

        binding.btnExport.setOnClickListener {
            createFile()
        }

        binding.btnImport.setOnClickListener {
            openFile()
        }

        binding.ibDeleteStore.setOnClickListener {
            viewModel.deleteCurrentStore()
        }

        return binding.root
    }
    private val get = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { importList(it) }
    }
    private fun openFile() {
        get.launch("application/json")
    }
    private val create = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let { exportList(it) }
    }
    private fun createFile() {
        create.launch("export.json")
    }


    private fun exportList(uri: Uri) {
        println(uri)
        val contentResolver: ContentResolver = requireContext().contentResolver
        try {
            val export = JsonObject()
            val stores = JsonArray()
            val deps = JsonArray()
            val items = JsonArray()

            runBlocking(Dispatchers.IO) {
                Utils.getAllStoreAsJson().forEach { stores.add(it) }
                Utils.getAllDepartmentAsJson().forEach { deps.add(it) }
                Utils.getAllItemsAsJson().forEach { items.add(it) }
                export.add("stores", stores)
                export.add("departments", deps)
                export.add("items", items)
            }
            contentResolver.openFileDescriptor(uri, "w")?.use { parcelFileDescriptor ->
                FileOutputStream(parcelFileDescriptor.fileDescriptor).use { fos ->
                    fos.write(
                        Gson().toJson(export).toByteArray()
                    )
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

    private fun importList(uri: Uri) {
        val gson = Gson()
        val jsonString = readTextFromUri(uri)

        val export = gson.fromJson(jsonString, Export::class.java)
        Log.d(TAG, "file content : $jsonString")
        val depConverter = DepartmentConverter()
        val itemConverter = ItemConverter()
        val storeConverter = StoreConverter()
        viewModel.imports(
        export.stores.map { storeConverter.toStore(it) },
        export.departments.map { depConverter.toDepartment(it) },
        export.items.map { itemConverter.toItem(it) }
        )
        Toast.makeText(requireContext(), getString(R.string.import_done), Toast.LENGTH_LONG).show()

    }

    internal data class Export(
        val stores: List<String>,
        val departments: List<String>,
        val items: List<String>
    )


    private fun setupDepartmentsRV() {
        //Create the department adapter
        departmentsAdapter = DepartmentsParamsAdapter(
            requireContext()
        )

        //Setup departments recycler view
        binding.rvDepartment.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvDepartment.adapter = departmentsAdapter

        viewModel.getUsedDepartment().observe(viewLifecycleOwner) {
            if (it?.isEmpty() == true) {
                binding.rvDepartment.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                binding.rvDepartment.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
                departmentsAdapter.updateList(it)
            }
        }


        val callback = SimpleItemTouchHelperCallback(
            departmentsAdapter,
            SimpleItemTouchHelperCallback.Direction.VERTICAL
        )
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvDepartment)

        binding.sStoresParam.setupStoreListener(
            viewModel,
            viewLifecycleOwner,
            setupNamesDD(viewModel.fetchStoreNames())
        )


    }

}

