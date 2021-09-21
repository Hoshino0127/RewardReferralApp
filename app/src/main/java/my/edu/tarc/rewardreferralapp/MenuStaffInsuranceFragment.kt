package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import my.edu.tarc.rewardreferralapp.databinding.FragmentMenuStaffInsuranceBinding

class MenuStaffInsuranceFragment : Fragment() {

    private lateinit var binding: FragmentMenuStaffInsuranceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_menu_staff_insurance, container, false)

        binding.btnManageInsurance.setOnClickListener(){
            val action = StaffDashboardFragmentDirections.actionStaffDashboardFragmentToListInsuranceFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnManageInsuranceApplication.setOnClickListener(){
            val action = StaffDashboardFragmentDirections.actionStaffDashboardFragmentToListInsuranceApplicationFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnToManageInsuranceCancelApplication.setOnClickListener(){
            val action = StaffDashboardFragmentDirections.actionStaffDashboardFragmentToListInsuranceCancelFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnToManageInsuranceClaim.setOnClickListener(){
            val action = StaffDashboardFragmentDirections.actionStaffDashboardFragmentToAdminClaimListingFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }

}