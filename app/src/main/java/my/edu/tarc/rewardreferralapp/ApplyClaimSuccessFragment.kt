package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentApplyClaimSuccessBinding
import java.text.SimpleDateFormat
import java.util.*

class ApplyClaimSuccessFragment : Fragment() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Insurance")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentApplyClaimSuccessBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_apply_claim_success, container, false)


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val action = ApplyClaimSuccessFragmentDirections.actionApplyClaimSuccessFragmentToHomepage()
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        binding.btnBackApplyClaimSuccess.setOnClickListener(){
            val action = ApplyClaimSuccessFragmentDirections.actionApplyClaimSuccessFragmentToReferralInsuranceListingFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnToClaimListing.setOnClickListener(){
            val action = ApplyClaimSuccessFragmentDirections.actionApplyClaimSuccessFragmentToClaimListingFragment()
            Navigation.findNavController(it).navigate(action)
        }


        return binding.root
    }


}