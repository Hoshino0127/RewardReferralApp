package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import my.edu.tarc.rewardreferralapp.databinding.FragmentApproveClaimAmountBinding

class ApproveClaimAmountFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentApproveClaimAmountBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_approve_claim_amount, container, false)

        binding.btnConfirm.setOnClickListener(){
            
        }

        return binding.root
    }


}