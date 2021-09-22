package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import my.edu.tarc.rewardreferralapp.databinding.FragmentMenuStaffReferralBinding
import my.edu.tarc.rewardreferralapp.databinding.FragmentMenuStaffRewardBinding

class MenuStaffReferralFragment : Fragment() {
    private lateinit var binding: FragmentMenuStaffReferralBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_menu_staff_referral, container, false)

        binding.btnManageRefListings.setOnClickListener(){
            val action = StaffDashboardFragmentDirections.actionStaffDashboardFragmentToReferralListingFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }

}