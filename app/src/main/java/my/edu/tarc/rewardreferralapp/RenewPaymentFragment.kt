package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import my.edu.tarc.rewardreferralapp.databinding.FragmentRenewPaymentBinding


class RenewPaymentFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentRenewPaymentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_renew_payment, container, false)


        activity?.let {
            ArrayAdapter.createFromResource(
                it.applicationContext,
                R.array.payment_method_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                binding.spPaymentMethod.adapter = adapter
            }
        }

        binding.spPaymentMethod?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                for (fragment in childFragmentManager.getFragments()) {
                    childFragmentManager.beginTransaction().remove(fragment).commit()
                }
                val selected = parent?.getItemAtPosition(position)
                if(selected == "Online banking"){
                    val bankPaymentFragment = BankPaymentFragment()
                    val fragmentTransaction = childFragmentManager.beginTransaction().apply {
                        add(R.id.fragmentPaymentContainer, bankPaymentFragment)
                        commit()
                    }
                }else{
                    val cardPaymentFragment = CardPaymentFragment()
                    childFragmentManager.beginTransaction().apply {
                        add(R.id.fragmentPaymentContainer, cardPaymentFragment)
                        commit()
                    }
                }
                binding.invalidateAll()
            }

        }

        binding.btnMakePayment.setOnClickListener(){

        }


        return binding.root
    }





}