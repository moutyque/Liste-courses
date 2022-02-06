package small.app.shopping.list.adapters

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import small.app.shopping.list.R
import small.app.shopping.list.fragments.FullScreenListFragment

import small.app.shopping.list.fragments.ListFragment
import small.app.shopping.list.fragments.ParamsFragment
import small.app.shopping.list.objects.Utils

class PagerAdapter(
    private val nbTabs: Int,
    private val context: Context,
    fm: FragmentManager,
    behavior: Int
) : FragmentPagerAdapter(fm, behavior) {


    override fun getCount(): Int {
        return nbTabs
    }


    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FullScreenListFragment()
            1 -> ListFragment()
            2 -> ParamsFragment()
            else -> {
                Log.e(Utils.TAG, "How did you managed to call the tab : $position")
                throw Exception("Unknown position")
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.full_screen)
            1 -> context.getString(R.string.tabListName)
            2 -> context.getString(R.string.tabListNameParameters)
            else -> {
                Log.e(Utils.TAG, "How did you managed to call the tab : $position")
                throw Exception("Unknown tab")
            }
        }

    }


}