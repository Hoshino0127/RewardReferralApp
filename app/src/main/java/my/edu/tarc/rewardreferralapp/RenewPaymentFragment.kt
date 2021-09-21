package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.*
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.databinding.FragmentRenewPaymentBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import kotlin.math.roundToInt


class RenewPaymentFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val ReferralInsuranceref = database.getReference("ReferralInsurance")
    private val ReferralRef = database.getReference("Referral")
    private var referralUID: String = ""
    private var points: Int = 0
    private var uplinePoints: Int = 0
    private var hasUpline: Boolean = false
    private lateinit var referral: Referral

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentRenewPaymentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_renew_payment, container, false)

        referralUID = CheckUser().getCurrentUserUID()!!
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

        ReferralRef.child(referralUID).get().addOnSuccessListener {
            if(it.exists()){
                points = it.child("points").value.toString().toInt()
                referral = Referral(
                    referralUID = it.child("referralID").value.toString(),
                    points = it.child("points").value.toString().toInt(),
                    referralUpline = it.child("referralUpline").value.toString()
                )
                if(referral.referralUpline != "none"){
                    hasUpline = true
                    getUplinePoints(referral.referralUpline!!)
                }
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

        val args = RenewPaymentFragmentArgs.fromBundle(requireArguments())
        val pointsAdded:Int = args.pointsAdded
        val newExpiryDate = args.newExpiryDate
        val InsuranceReferralID = args.insuranceReferralID
        println(InsuranceReferralID)

        binding.btnMakePayment.setOnClickListener(){



            println("After pass: ${newExpiryDate.toString()}")

            val updateExpiryDate = mapOf(
                "time" to newExpiryDate.time.toLong()
            )


            ReferralInsuranceref.child(InsuranceReferralID).child("insuranceExpiryDate").updateChildren(updateExpiryDate).addOnSuccessListener {
                Toast.makeText(context, "Update Successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show()

            }

            points += pointsAdded
            println(points)

            val updatePoints = mapOf(
                "points" to points
            )




            ReferralRef.child(referralUID).updateChildren(updatePoints).addOnSuccessListener {
                Toast.makeText(context, "Update Successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show()

            }

            if(hasUpline){
                uplinePoints += (pointsAdded * 0.2).toDouble().roundToInt()
                val updateUplinePoints = mapOf(
                    "points" to uplinePoints
                )
                ReferralRef.child(referral.referralUpline!!).updateChildren(updateUplinePoints).addOnSuccessListener {
                    Toast.makeText(context, "Update Successfully", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show()

                }
            }

            val action = RenewPaymentFragmentDirections.actionRenewPaymentFragmentToRenewSuccessFragment(points,pointsAdded)
            Navigation.findNavController(it).navigate(action)

        }


        return binding.root
    }

    private fun getUplinePoints(referralUID:String){
        ReferralRef.orderByChild("referralUID").equalTo(referralUID).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(refSS in snapshot.children){

                        uplinePoints = refSS.child("points").value.toString().toInt()

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }



}