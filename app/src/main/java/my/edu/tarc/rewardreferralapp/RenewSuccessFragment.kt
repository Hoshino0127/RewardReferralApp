package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val action = RenewSuccessFragmentDirections.actionRenewSuccessFragmentToReferralInsuranceListingFragment()
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        val args = RenewSuccessFragmentArgs.fromBundle(requireArguments())
        val points = args.points
        val pointsAdded = args.pointsAdded

        val pointsStr = "${points}(+${pointsAdded})"
        binding.txtPointsEarned.text = pointsStr

        binding.btnToInsuranceListing.setOnClickListener(){
            val action = RenewSuccessFragmentDirections.actionRenewSuccessFragmentToReferralInsuranceListingFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }


}