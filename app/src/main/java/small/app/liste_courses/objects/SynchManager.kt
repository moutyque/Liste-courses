package small.app.liste_courses.objects

import androidx.fragment.app.Fragment
import small.app.liste_courses.fragments.ListFragment
import small.app.liste_courses.fragments.ParamsFragment

object SyncManager {
    val listFrag = ListFragment()
    val paramFrag = ParamsFragment()

    private var isListCreated = false
    private var isParamCreated = false

    fun setCreated(fragment: Fragment) {
        when (fragment) {
            is ListFragment -> isListCreated = true
            is ParamsFragment -> isParamCreated = true
        }
        if (isListCreated && isParamCreated) {
            // listFrag.departmentsAdapter.synchroAction = paramFrag.departmentsAdapter
        }
    }


}