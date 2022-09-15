package small.app.shopping.list.adapters

import android.R
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import small.app.shopping.list.enums.SIUnit
import small.app.shopping.list.enums.SIUnit.Companion.unitList
import small.app.shopping.list.objects.Utils.saveAndUse
import small.app.shopping.list.room.entities.Item

class UnitAdapter(context: Context) : AdapterView.OnItemSelectedListener {
    private var initialized = false
    val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
        context,
        R.layout.simple_dropdown_item_1line,
        unitList()
    )

    var actions: IUnitSelect? = null

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        actions?.let {
            when (initialized) {
                true -> {
                    it.getItem().apply {
                        unit = SIUnit.fromValue(unitList()[position])
                        saveAndUse()
                    }
                }
                false -> {
                    it.initUnit()
                    initialized = true
                }
            }
        }
            ?: throw KotlinNullPointerException("The member actions of type IUnitSelect should be initialized")
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
        //Not needed
    }

}

interface IUnitSelect {
    fun getItem(): Item
    fun initUnit()
}