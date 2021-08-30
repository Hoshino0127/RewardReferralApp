package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import my.edu.tarc.rewardreferralapp.databinding.FragmentApplyClaimBinding
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferralInsuranceListingBinding

class ApplyClaimFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentApplyClaimBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_apply_claim, container, false)

        val args = ApplyClaimFragmentArgs.fromBundle(requireArguments())
        binding.tvInsuranceID.text = args.insuranceID
        binding.btnApplyClaim.setOnClickListener(){
            if(CheckError()){
                if(ApplyClaim()) {
                    val action = ApplyClaimFragmentDirections.actionApplyClaimFragmentToApplyClaimSuccessFragment()
                    Navigation.findNavController(it).navigate(action)
                }
            }

        }

        return binding.root
    }

    private fun CheckError(): Boolean{
        return true
    }

    private fun ApplyClaim(): Boolean{
        return true
    }


}