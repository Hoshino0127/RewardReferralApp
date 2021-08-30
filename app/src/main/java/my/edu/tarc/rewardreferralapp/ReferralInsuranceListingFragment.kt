package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.data.RecyclerViewAdapter
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferralInsuranceListingBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class ReferralInsuranceListingFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val InsuranceList: List<Insurance> = listOf(
            Insurance("IN001","Car insurance","Etiqa","Plan A", SimpleDateFormat("dd/MM/yyyy").parse("22/08/2022"),"IR001"),
            Insurance("IN002","Motor insurance","Prudential","Plan C", SimpleDateFormat("dd/MM/yyyy").parse("25/04/2022"),"IR001"),
            Insurance("IN003","Truck insurance","Etiqa","Plan D", SimpleDateFormat("dd/MM/yyyy").parse("15/06/2022"),"IR001"),
            Insurance("IN004","Van insurance","Prudential","Plan B", SimpleDateFormat("dd/MM/yyyy").parse("07/04/2023"),"IR001")
        )

        // Inflate the layout for this fragment
        val binding: FragmentReferralInsuranceListingBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_referral_insurance_listing, container, false)
        val productAdapter = RecyclerViewAdapter(InsuranceList,
            RecyclerViewAdapter.ClaimListener { insuranceID ->

                val it = view
                if (it != null) {
                    Navigation.findNavController(it).navigate(ReferralInsuranceListingFragmentDirections.actionReferralInsuranceListingFragmentToApplyClaimFragment(insuranceID))
                }
            })



        binding.referralInsuranceRecyclerView.adapter = productAdapter
        binding.referralInsuranceRecyclerView.setHasFixedSize(true)

        binding.btnToApplyClaim.setOnClickListener(){

        }

        binding.btnToRenewInsurance.setOnClickListener(){
            val action = ReferralInsuranceListingFragmentDirections.actionReferralInsuranceListingFragmentToRenewInsuranceFragment()
            Navigation.findNavController(it).navigate(action)
        }



        return binding.root
    }


}