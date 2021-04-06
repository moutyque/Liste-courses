package small.app.liste_courses.adapters.listenners

import android.util.Log
import small.app.liste_courses.adapters.DepartmentAdapter
import small.app.liste_courses.adapters.ObjectChange
import small.app.liste_courses.model.Department
import small.app.liste_courses.model.MainViewModel

class DepartmentChangeListener(val model : MainViewModel):
    IOnAdapterChangeListener<Department, DepartmentAdapter, DepartmentAdapter.DepartmentViewHolder> {

    private lateinit var adapter: DepartmentAdapter
    override fun onObjectCreated(a: Department) {
    }

    override fun onItemUpdate(a: Department, position: Int, list: MutableList<Department>,code: ObjectChange) {
        when (code) {
            ObjectChange.CLASSIFIED -> {
                //A new item has been classified in this department
                model.updateDepartmentsList(a)
            }
            else -> Log.d("DepartmentChangeListener","Code $code not managed")
        }
    }

    override fun onItemDelete(a: Department) {
    }

    override fun setAdapter(adapter: DepartmentAdapter) {
        this.adapter = adapter
    }

    override fun getAdapter(): DepartmentAdapter {
        return adapter
    }


}
