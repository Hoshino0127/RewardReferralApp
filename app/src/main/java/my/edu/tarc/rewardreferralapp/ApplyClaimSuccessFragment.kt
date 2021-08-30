package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import my.edu.tarc.rewardreferralapp.databinding.FragmentApplyClaimBinding
import my.edu.tarc.rewardreferralapp.databinding.FragmentApplyClaimSuccessBinding

class ApplyClaimSuccessFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentApplyClaimSuccessBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_apply_claim_success, container, false)

        binding.btnToClaimListing.setOnClickListener(){
            val action = ApplyClaimSuccessFragmentDirections.actionApplyClaimSuccessFragmentToClaimListingFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }


}