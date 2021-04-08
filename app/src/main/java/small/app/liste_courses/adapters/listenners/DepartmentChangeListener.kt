package small.app.liste_courses.adapters.listenners

import android.util.Log
import androidx.lifecycle.ViewModel
import small.app.liste_courses.adapters.DepartmentsAdapter
import small.app.liste_courses.adapters.ObjectChange
import small.app.liste_courses.model.Department
import small.app.liste_courses.model.MainViewModel

class DepartmentChangeListener(val model: MainViewModel) :
    IOnAdapterChangeListener<Department, DepartmentsAdapter, DepartmentsAdapter.DepartmentViewHolder> {

    private lateinit var adapter: DepartmentsAdapter
    override fun onObjectCreated(a: Department) {
    }

    override fun onObjectUpdate(
        a: Department,
        position: Int,
        list: MutableList<Department>,
        code: ObjectChange
    ) {
        when (code) {
            ObjectChange.CLASSIFIED -> {
                //A new item has been classified in this department
                model.updateDepartmentsList(a)
            }
            else -> Log.d("DepartmentChangeListener", "Code $code not managed")
        }
    }

    override fun onObjectDelete(a: Department) {
    }

    override fun setAdapter(adapter: DepartmentsAdapter) {
        this.adapter = adapter
    }

    override fun getAdapter(): DepartmentsAdapter {
        return adapter
    }

    override fun getModel(): ViewModel {
        return model
    }


}
