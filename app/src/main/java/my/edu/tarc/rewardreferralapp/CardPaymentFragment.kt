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

class CardPaymentFragment : Fragment() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        var binding: FragmentCardPaymentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_card_payment, container, false)

        binding = FragmentCardPaymentBinding.inflate(layoutInflater)


        binding.cbUseSavedDetails.setOnClickListener{

            val cardHolderName = binding.txtCardholderName.text.toString()
            val CardNo = binding.txtCardNo.text.toString()
            val ExpireDate = binding.txtExpiryDate.text.toString()
            val Cvv = binding.txtCVV.text.toString()

            database = FirebaseDatabase.getInstance().getReference("Card")
            val Card= Card(cardHolderName,CardNo, ExpireDate, Cvv)

            database.child(cardHolderName).setValue(Card).addOnSuccessListener {

                Toast.makeText(context, "successfully Saved",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()

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


}