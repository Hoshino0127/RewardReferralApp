package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import my.edu.tarc.rewardreferralapp.databinding.FragmentCancelInsuranceBinding


class CancelInsuranceFragment : Fragment() {

    private lateinit var binding: FragmentCancelInsuranceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_cancel_insurance, container, false)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val action = CancelInsuranceFragmentDirections.actionCancelInsuranceFragmentToReferralInsuranceListingFragment()
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        val args = CancelInsuranceFragmentArgs.fromBundle(requireArguments())
        var insuranceReferralID: String = args.insuranceReferralID

        binding.tvInsuranceReferralIDCancelIns.text = insuranceReferralID

        return binding.root
    }


}