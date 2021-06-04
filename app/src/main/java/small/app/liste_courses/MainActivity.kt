package small.app.liste_courses

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import small.app.liste_courses.adapters.PagerAdapter
import small.app.liste_courses.databinding.ActivityMainBinding
import small.app.liste_courses.objects.Utils
import small.app.liste_courses.room.Repository

class MainActivity : AppCompatActivity() {

    private lateinit var repo: Repository
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repo = Repository(context = this)
        Utils.repo = repo

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.viewPager.offscreenPageLimit = 2
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


        binding.viewPager.adapter = PagerAdapter(
            binding.tabLayout.tabCount, this, supportFragmentManager,
            0
        )

        binding.tabLayout.setupWithViewPager(binding.viewPager)
        setContentView(binding.root)

    }


}