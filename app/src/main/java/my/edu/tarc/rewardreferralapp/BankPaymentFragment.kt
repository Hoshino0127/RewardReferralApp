package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import my.edu.tarc.rewardreferralapp.databinding.FragmentBankPaymentBinding

class BankPaymentFragment : Fragment() {

    private lateinit var binding: FragmentBankPaymentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentBankPaymentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_bank_payment, container, false)

        activity?.let {
            ArrayAdapter.createFromResource(
                it.applicationContext,
                R.array.bank_selection_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                binding.spBankSelection.adapter = adapter
            }
        }

        return binding.root
    }

    fun getBankSelection(): String {
        return binding.spBankSelection.selectedItem.toString()
    }



}