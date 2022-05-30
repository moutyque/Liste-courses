package small.app.shopping.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import small.app.shopping.list.adapters.PagerAdapter
import small.app.shopping.list.databinding.ActivityMainBinding
import small.app.shopping.list.models.Department
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.room.Repository
import small.app.shopping.list.room.entities.Item

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

        Department(
            name = this.getString(R.string.default_category_name),
            isUsed = true,
            items = mutableListOf(),
            itemsCount = 0,
            order = 0
        ).apply {
            Utils.saveDepartment(this)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.adapter = PagerAdapter(
            binding.tabLayout.tabCount, this, supportFragmentManager,
            0
        )

        binding.tabLayout.setupWithViewPager(binding.viewPager)


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

        binding.tabLayout.setupWithViewPager(binding.viewPager)


        setContentView(binding.root)


    }

}