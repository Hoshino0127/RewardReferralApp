package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferralInsuranceListingBinding
import my.edu.tarc.rewardreferralapp.databinding.FragmentRenewInsuranceBinding

class RenewInsuranceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentRenewInsuranceBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_renew_insurance, container, false)

        binding.btnToPayment.setOnClickListener(){
            val action = RenewInsuranceFragmentDirections.actionRenewInsuranceFragmentToRenewPaymentFragment()
            Navigation.findNavController(it).navigate(action)
        }
        return binding.root
    }


}