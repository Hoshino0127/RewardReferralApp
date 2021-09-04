package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferralListingBinding

class ReferralListingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentReferralListingBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_referral_listing, container, false)

        return binding.root

//        binding.btnDelete.setOnClickListener() {
//            val action = ReferralInsuranceListingFragmentDirections.actionReferralInsuranceListingFragmentToApplyClaimFragment()
//            Navigation.findNavController(it).navigate(action)
    }
}