package small.app.liste_courses.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.ListFragment
import small.app.liste_courses.fragments.ParamsFragment
import small.app.liste_courses.objects.SyncManager

class PagerAdapter(private val nbTabs: Int, fm: FragmentManager, behavior: Int) :
    FragmentPagerAdapter(fm, behavior) {

    private val listFrag = ListFragment()
    private val paramFrag = ParamsFragment()
    override fun getCount(): Int {
        return nbTabs
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SyncManager.listFrag
            1 -> SyncManager.paramFrag
            else -> {
                Log.e("PagerAdapter", "How did you managed to call the tab : $position")
                throw Exception("Unknown tab")
            }
        }

    }

}