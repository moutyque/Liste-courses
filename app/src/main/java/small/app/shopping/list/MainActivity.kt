package small.app.shopping.list

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import small.app.shopping.list.adapters.PagerAdapter
import small.app.shopping.list.databinding.ActivityMainBinding
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.room.Repository

/*
TODO : instead of hide show view, hide only the top bar and call the adapter on items 2
 */
class MainActivity : AppCompatActivity() {

    private lateinit var repo: Repository
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repo = Repository(context = this)
        Utils.repo = repo

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.adapter = PagerAdapter(
            binding.tabLayout.tabCount, supportFragmentManager, lifecycle
        )



        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //Not used
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //Not used
            }

        })
        setContentView(binding.root)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.full_screen)
                1 -> getString(R.string.tabListName)
                2 -> getString(R.string.tabListNameParameters)
                else -> {
                    Log.e(Utils.TAG, "How did you managed to call the tab : $position")
                    throw Exception("Unknown tab")
                }
            }
        }.attach()



        setContentView(binding.root)


    }

}