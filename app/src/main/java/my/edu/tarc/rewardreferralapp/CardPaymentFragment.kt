package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import my.edu.tarc.rewardreferralapp.databinding.FragmentBankPaymentBinding
import my.edu.tarc.rewardreferralapp.databinding.FragmentCardPaymentBinding

class CardPaymentFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentCardPaymentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_card_payment, container, false)


        binding.txtExpiryDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
                if (start == 1 && start+added == 2 && p0?.contains('/') == false) {
                    binding.txtExpiryDate.setText(p0.toString() + "/")
                } else if (start == 3 && start-removed == 2 && p0?.contains('/') == true) {
                    binding.txtExpiryDate.setText(p0.toString().replace("/", ""))
                }
            }
        })

        return binding.root
    }


}