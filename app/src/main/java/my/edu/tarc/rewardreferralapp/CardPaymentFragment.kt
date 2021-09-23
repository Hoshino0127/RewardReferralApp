package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.rewardreferralapp.data.Card
import my.edu.tarc.rewardreferralapp.databinding.ActivityMainBinding
import my.edu.tarc.rewardreferralapp.databinding.FragmentCardPaymentBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser

class CardPaymentFragment : Fragment() {

    private lateinit var binding: FragmentCardPaymentBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment



        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_card_payment, container, false)


        binding.cbUseSavedDetails.setOnClickListener(){
            database = FirebaseDatabase.getInstance().getReference("Card")
            database.child(CheckUser().getCurrentUserUID()!!).get().addOnSuccessListener {
                if(it.exists()){
                    val cardHolderName: String = it.child("cardHolderName").value.toString()
                    val cardNo: String = it.child("cardNo").value.toString()
                    val expiryDate: String = it.child("expiredDate").value.toString()
                    val cvv: String = it.child("cvv").value.toString()
                    binding.txtCardholderName.setText(if(cardHolderName == "null"){""}else{cardHolderName})
                    binding.txtCardNo.setText(if(cardNo == "null"){""}else{cardNo})
                    binding.txtExpiryDate.setText(if(expiryDate == "null"){""}else{expiryDate})
                    binding.txtCVV.setText(if(cvv == "null"){""}else{cvv})
                }
            }
        }


        binding.cbSaveDetails.setOnClickListener{

            if(binding.cbSaveDetails.isChecked){
                if(errorFree()){
                    val cardHolderName = binding.txtCardholderName.text.toString()
                    val CardNo = binding.txtCardNo.text.toString()
                    val ExpireDate = binding.txtExpiryDate.text.toString()
                    val Cvv = binding.txtCVV.text.toString()

                    database = FirebaseDatabase.getInstance().getReference("Card")
                    val Card= Card(cardHolderName,CardNo, ExpireDate, Cvv)

                    database.child(CheckUser().getCurrentUserUID()!!).setValue(Card).addOnSuccessListener {

                        Toast.makeText(context, "successfully Saved",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener{
                        Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }


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

    private fun errorFree():Boolean{
        if (binding.txtCardholderName.text.isEmpty()){
            binding.txtCardholderName.error = "Card Holder Name cannot be empty"
            return false
        }
        if (binding.txtCardNo.text.isEmpty()){
            binding.txtCardNo.error = "Card Number cannot be empty"
            return false
        }
        if (binding.txtExpiryDate.text.isEmpty()){
            binding.txtExpiryDate.error = "Card Expiry Date cannot be empty"
            return false
        }
        if (binding.txtCVV.text.isEmpty()){
            binding.txtCVV.error = "Card CVV Date cannot be empty"
            return false
        }
        return true
    }


}