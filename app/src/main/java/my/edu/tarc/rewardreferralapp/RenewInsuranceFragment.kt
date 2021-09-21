package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.renderscript.Sampler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.databinding.FragmentRenewInsuranceBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class RenewInsuranceFragment : Fragment() {

    val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val myreferaalinsurance = database.getReference("ReferralInsurance")
    val myInsurance = database.getReference("Insurance")
    val format = SimpleDateFormat("dd/MM/yyyy",Locale.US)
    val ExpireDateString = format.format(Date())
    private var insuranceID: String = ""
    private var insuranceReferralID: String = ""
    private var year: Int = 1
    private var currentPrice = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentRenewInsuranceBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_renew_insurance, container, false)


        val args = RenewInsuranceFragmentArgs.fromBundle(requireArguments())
        insuranceID = args.insuranceID
        insuranceReferralID = args.insuranceReferralID

        var durationStr: String = year.toString() + if(year == 1){"year"}else{"years"}
        binding.tvPlanDuration.text = durationStr

        myInsurance.orderByChild("insuranceID").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(insuranceSS in snapshot.children){
                        if(insuranceSS.child("insuranceID").value.toString() == insuranceID){
                            binding.tvCompTitle.text = insuranceSS.child("insuranceComp").value.toString()
                            //binding.tvInsuranceID.text = insuranceSS.child("insuranceID").value.toString()
                            binding.tvInsuranceName.text = insuranceSS.child("insuranceName").value.toString()
                            binding.tvInsurancePlanName.text = insuranceSS.child("insurancePlan").value.toString()
                            val insurancePrice:Double = insuranceSS.child("insurancePrice").value.toString().toDouble()
                            binding.tvTotalRenewInsurance.text = String.format("%.2f",insurancePrice)
                            currentPrice = insurancePrice
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


        var dt:Date = Date()
        var calendar = Calendar.getInstance()

        myreferaalinsurance.child(insuranceReferralID).get().addOnSuccessListener {
            dt = Date(it.child("insuranceExpiryDate").child("time").value as Long)
            binding.tvExpiryDate.text = format.format(dt)

            calendar.time = dt
            calendar.add(Calendar.YEAR, 1)

            /*binding.tvNextExpiryDate.text = calendar.get(Calendar.DAY_OF_MONTH).toString()+"/"+calendar.get(Calendar.MONTH).toString()+"/"+calendar.get(Calendar.YEAR).toString()*/
            binding.tvNextExpiryDate.text = format.format(calendar.time)

        }

        binding.btnToPayment.setOnClickListener(){

            val points = (currentPrice.roundToInt() * 0.1).toInt()
            println(points)
            val action = RenewInsuranceFragmentDirections.actionRenewInsuranceFragmentToRenewPaymentFragment(calendar.time,insuranceReferralID, points)
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }



}