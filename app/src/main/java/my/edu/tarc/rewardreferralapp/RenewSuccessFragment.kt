package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import my.edu.tarc.rewardreferralapp.databinding.FragmentRenewSuccessBinding

class RenewSuccessFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentRenewSuccessBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_renew_success, container, false)

        binding.btnToInsuranceListing.setOnClickListener(){
            val action = RenewSuccessFragmentDirections.actionRenewSuccessFragmentToReferralInsuranceListingFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }


}