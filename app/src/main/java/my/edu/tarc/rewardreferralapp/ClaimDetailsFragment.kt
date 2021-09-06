package my.edu.tarc.rewardreferralapp

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.adapter.ClaimFigureAdapter
import my.edu.tarc.rewardreferralapp.data.Claim
import my.edu.tarc.rewardreferralapp.data.ClaimFigure
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentClaimDetailsBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ClaimDetailsFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val insuranceRef = database.getReference("Insurance")
    private val referralInsuranceRef = database.getReference("ReferralInsurance")
    private val claimRef = database.getReference("Claim")
    private val claimFigureRef = database.getReference("ClaimFigure")

    private lateinit var binding: FragmentClaimDetailsBinding
    private lateinit var claimID: String
    private lateinit var referralID: String
    private var deductible: Double = 0.0
    private var claim: Claim = Claim()
    private var insurance: Insurance = Insurance()
    private var cfList: ArrayList<ClaimFigure> = ArrayList<ClaimFigure>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_claim_details, container, false)

        val args = ClaimDetailsFragmentArgs.fromBundle(requireArguments())
        claimID = args.claimID
        referralID = args.referralID

        //get deductible from referral
        deductible = 0.1

        /*val cf: ClaimFigure = ClaimFigure(claimID,"Figure 1",100.00)
        val cf2: ClaimFigure = ClaimFigure(claimID,"Figure 2",300.00)
        val claimFigureList: ArrayList<ClaimFigure> = ArrayList<ClaimFigure>()
        claimFigureList.add(cf)
        claimFigureList.add(cf2)

        for(cf in claimFigureList){
            claimFigureRef.push().setValue(cf)
        }*/

        claimRef.orderByChild("claimID").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(currentClaim in snapshot.children){
                        Log.v(TAG,currentClaim.child("claimID").getValue().toString())
                        if(currentClaim.child("claimID").getValue().toString().equals(claimID)){
                            val claimID = currentClaim.child("claimID").getValue().toString()
                            val dt = Date(currentClaim.child("accidentDateTime").child("time").getValue() as Long)
                            val accidentPlace = currentClaim.child("accidentPlace").getValue().toString()
                            val accidentType = currentClaim.child("accidentType").getValue().toString()
                            val accidentDesc = currentClaim.child("accidentDesc").getValue().toString()
                            val mileage = currentClaim.child("mileage").getValue().toString().toInt()
                            val imgMileage = currentClaim.child("imgMileage").getValue().toString()
                            val imgDamage = currentClaim.child("imgDamage").getValue().toString()
                            val applyDateTime: Date = Date(currentClaim.child("applyDateTime").child("time").getValue() as Long)
                            var approveDateTime: Date? = if(currentClaim.child("approveDateTime").getValue() == null){
                                null
                            }else{
                                Date(currentClaim.child("approveDateTime").child("time").getValue() as Long)
                            }
                            val claimStatus = currentClaim.child("claimStatus").getValue().toString()
                            val insuranceID = currentClaim.child("insuranceID").getValue().toString()
                            val insuranceReferral = currentClaim.child("insuranceReferral").getValue().toString()
                            claim = Claim(claimID,dt,accidentPlace,accidentType,accidentDesc,mileage,imgMileage,imgDamage,applyDateTime,approveDateTime,claimStatus,insuranceID,insuranceReferral)


                            updateView()


                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            insuranceRef.orderByChild("insuranceID").addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(currentInsurance in snapshot.children){
                            if(claim.insuranceID != null){claim.insuranceID}else{""}?.let {
                                Log.v(TAG,
                                    it
                                )
                            }
                            if(currentInsurance.child("insuranceID").getValue().toString().equals(claim.insuranceID)){
                                val insuranceID = currentInsurance.child("insuranceID").getValue().toString()
                                val insuranceName = currentInsurance.child("insuranceName").getValue().toString()
                                val insuranceComp = currentInsurance.child("insuranceComp").getValue().toString()
                                val insurancePlan = currentInsurance.child("insurancePlan").getValue().toString()
                                var insuranceCoverage: ArrayList<String> = ArrayList<String>()
                                for(child in currentInsurance.child("insuranceCoverage").children){
                                    insuranceCoverage.add(child.getValue().toString())
                                    println(child.getValue().toString())
                                }
                                insurance = Insurance(insuranceID,insuranceName,insuranceComp,insurancePlan,insuranceCoverage)

                                updateInsuranceView()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        },1000)


        handler.postDelayed({
            claimFigureRef.orderByChild("claimID").addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(currentCF in snapshot.children){
                            if(currentCF.child("claimID").getValue().toString().equals(claimID)){
                                val claimFigureName = currentCF.child("claimFigureName").getValue().toString()
                                val claimFigureAmount = currentCF.child("claimFigureAmount").getValue().toString().toDouble()
                                cfList.add(ClaimFigure(claimID,claimFigureName,claimFigureAmount))
                            }
                        }
                        Log.v(TAG,cfList.size.toString())
                        updateClaimFigure()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        },1000)

        binding.btnBack.setOnClickListener(){
            val action = ClaimDetailsFragmentDirections.actionClaimDetailsFragmentToClaimListingFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }

    fun updateInsuranceView(){
        var strCoverage = ""
        var rowCount = 0

        binding.tvCompTitle.text = insurance.insuranceComp
        binding.tvInsuranceID.text = insurance.insuranceID
        binding.tvInsuranceName.text = insurance.insuranceName
        binding.tvInsurancePlanName.text = insurance.insurancePlan
        if(!(insurance.insuranceCoverage.isNullOrEmpty())){
            for(str:String in insurance.insuranceCoverage!!){
                strCoverage += str + "\n"
                rowCount += 1
            }
        }
        binding.tvInsuranceCoverage.text = strCoverage
        var height = 350 + (binding.tvInsuranceCoverage.height* rowCount)

        val layout: RelativeLayout = binding.rlInsuranceDetails
        val params: ViewGroup.LayoutParams = layout.layoutParams
        params.height = height
        layout.layoutParams = params
        binding.invalidateAll()
    }

    fun updateView(){
        val format = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")

        binding.tvStatus.text = claim.claimStatus
        binding.tvClaimID.text = claim.claimID
        binding.tvAccidentDate.text = format.format(claim.accidentDateTime)
        binding.tvAccidentPlace.text = claim.accidentPlace
        binding.tvMileage.text = claim.mileage.toString()
        binding.tvAccidentDesc.text = claim.accidentDesc

        if (claim.approveDateTime != null){
            binding.tvApproveDate.text = "Waiting to be approved"
        }else{
            binding.tvApproveDate.text = format.format(claim.applyDateTime!!)
        }

        when (claim.claimStatus){
            "Pending" -> binding.tvStatus.setTextColor(Color.parseColor("#EC512B"))
            "Accepted" -> binding.tvStatus.setTextColor(Color.parseColor("#31B12C"))
            "Rejected" -> binding.tvStatus.setTextColor(Color.parseColor("#F30E15"))
            else -> binding.tvStatus.setTextColor(Color.parseColor("#000000"))
        }



    }

    fun updateClaimFigure(){
        var subtotal: Double = 0.0
        var deductibleAmt: Double = 0.0
        var total: Double = 0.0

        val adapter = ClaimFigureAdapter(requireContext(),cfList)
        binding.lvAmountDetails.adapter = adapter

        //set height
        var totalHeight: Int = 0;
        for (i in 0 until cfList.count()) {
            val listItem: View? = adapter.getView(i,null,binding.lvAmountDetails)
            listItem?.measure(0, 0)
            totalHeight += listItem?.getMeasuredHeight()!!

            subtotal += cfList[i].claimFigureAmount!!
        }
        val params: ViewGroup.LayoutParams = binding.lvAmountDetails.getLayoutParams()
        params.height = totalHeight + (binding.lvAmountDetails.getDividerHeight() * (adapter.getCount() - 1))
        binding.lvAmountDetails.setLayoutParams(params)
        binding.lvAmountDetails.requestLayout()

        deductibleAmt = subtotal * this.deductible
        total = subtotal - deductibleAmt
        binding.tvSubtotal.text = String.format("%.2f",subtotal)
        binding.tvDeductible.text = String.format("%.2f",deductibleAmt)
        binding.tvTotal.text = String.format("%.2f",total)
        binding.invalidateAll()




    }
}