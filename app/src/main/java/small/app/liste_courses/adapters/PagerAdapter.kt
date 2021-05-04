package small.app.liste_courses.adapters

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import small.app.liste_courses.R

import small.app.liste_courses.fragments.ListFragment
import small.app.liste_courses.fragments.ParamsFragment

class PagerAdapter(private val nbTabs :Int, private val context: Context, fm: FragmentManager, behavior: Int) : FragmentPagerAdapter (fm,behavior){




    override fun getCount(): Int {
        return  nbTabs
    }


    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ListFragment()
            1 -> ParamsFragment()
            else -> {
                Log.e("PagerAdapter", "How did you managed to call the tab : $position")
                throw Exception("Unknown position")
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.tabListName)
            1 -> context.getString(R.string.tabListNameParameters)
            else -> {
                Log.e("PagerAdapter", "How did you managed to call the tab : $position")
                throw Exception("Unknown tab")
            }
        }

    }


}