package small.app.shopping.list.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import small.app.shopping.list.fragments.FullScreenListFragment
import small.app.shopping.list.fragments.ListFragment
import small.app.shopping.list.fragments.ParamsFragment
import small.app.shopping.list.objects.Utils

class PagerAdapter(
    private val nbTabs: Int,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {


    override fun getItemCount(): Int {
        return nbTabs
    }

    override fun createFragment(position: Int): Fragment {
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


}