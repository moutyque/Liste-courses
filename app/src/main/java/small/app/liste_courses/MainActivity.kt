package small.app.liste_courses

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import com.google.android.material.tabs.TabLayout
import small.app.liste_courses.adapters.PagerAdapter
import small.app.liste_courses.databinding.ActivityMainBinding
import small.app.liste_courses.objects.Utils
import small.app.liste_courses.room.Repository

class MainActivity : AppCompatActivity() {

    private lateinit var repo: Repository
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        repo = Repository(context = this)
        Utils.repo = repo

        binding = ActivityMainBinding.inflate(layoutInflater)



        setContentView(binding.root)

    }

    override fun onResume() {


        super.onResume()
        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.setCurrentItem(tab!!.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        binding.viewPager.adapter = PagerAdapter(binding.tabLayout.tabCount,supportFragmentManager,
            0)
    }



}