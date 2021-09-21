package my.edu.tarc.rewardreferralapp

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import my.edu.tarc.rewardreferralapp.databinding.FragmentStaffDashboardBinding

class StaffDashboardFragment : Fragment() {

    private lateinit var binding: FragmentStaffDashboardBinding

    private var toolbar: Toolbar? = null
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null

    private var insuranceFragment: MenuStaffInsuranceFragment? = null
    private var referralFragment: MenuStaffReferralFragment? = null
    private var rewardFragment: MenuStaffRewardFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentStaffDashboardBinding.inflate(inflater, container ,false)

        binding.tabLayout.setupWithViewPager(binding.viewPager)
        val viewPagerAdapter = ViewPagerAdapter(requireActivity().supportFragmentManager, 0)
        insuranceFragment = MenuStaffInsuranceFragment()
        referralFragment = MenuStaffReferralFragment()
        rewardFragment = MenuStaffRewardFragment()
        viewPagerAdapter.addFragment(insuranceFragment, "Insurance")
        viewPagerAdapter.addFragment(referralFragment, "Referral")
        viewPagerAdapter.addFragment(rewardFragment, "Reward")
        binding.viewPager!!.adapter = viewPagerAdapter

        return binding.root
    }

/*
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)
        exploreFragment = ExploreFragment()
        flightsFragment = FlightsFragment()
        travelFragment = TravelFragment()
        tabLayout!!.setupWithViewPager(viewPager)
        viewPagerAdapter.addFragment(exploreFragment, "Explore")
        viewPagerAdapter.addFragment(flightsFragment, "Flights")
        viewPagerAdapter.addFragment(travelFragment, "Travel")
        viewPager!!.adapter = viewPagerAdapter
        tabLayout!!.getTabAt(0).setIcon(R.drawable.ic_baseline_explore_24)
        tabLayout!!.getTabAt(1).setIcon(R.drawable.ic_baseline_flight_24)
        tabLayout!!.getTabAt(2).setIcon(R.drawable.ic_baseline_card_travel_24)
        val badgeDrawable = tabLayout!!.getTabAt(0)!!.orCreateBadge
        badgeDrawable.isVisible = true
        badgeDrawable.number = 12
    }
*/

    private class ViewPagerAdapter(fm: FragmentManager, behavior: Int) :
        FragmentPagerAdapter(fm, behavior) {
        private val fragments: MutableList<Fragment> = ArrayList()
        private val fragmentTitle: MutableList<String> = ArrayList()
        fun addFragment(fragment: Fragment?, title: String) {
            fragments.add(fragment!!)
            fragmentTitle.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        @Nullable
        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitle[position]
        }
    }
}