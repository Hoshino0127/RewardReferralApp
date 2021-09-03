package my.edu.tarc.rewardreferralapp

import android.os.Bundle
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
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.data.RecyclerViewAdapter
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferralInsuranceListingBinding
import java.util.*
import kotlin.collections.ArrayList


class ReferralInsuranceListingFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val insuranceRef = database.getReference("Insurance")
    private val ReferralInsuranceRef = database.getReference("ReferralInsurance")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentReferralInsuranceListingBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_referral_insurance_listing, container, false)


        val insuranceIDList = ArrayList<String>()
        val insuranceList = ArrayList<Insurance>()

        ReferralInsuranceRef

        insuranceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){

                    for (insuranceSnapshot in snapshot.children){

                        //insuranceList.add(insuranceSnapshot.getValue(Insurance::class.java)!!)
                        val insuranceID: String = insuranceSnapshot.child("insuranceID").getValue().toString()
                        val insuranceName: String = insuranceSnapshot.child("insuranceName").getValue().toString()
                        val insuranceComp: String = insuranceSnapshot.child("insuranceComp").getValue().toString()
                        val insurancePlan: String = insuranceSnapshot.child("insurancePlan").getValue().toString()
                        val insuranceReferral: String = insuranceSnapshot.child("insuranceReferral").getValue().toString()
                        val insuranceExpiryDate: Date = Date(insuranceSnapshot.child("insuranceExpiryDate").child("time").getValue() as Long)
                        println(insuranceSnapshot.child("insuranceCoverage").getValue().toString())
                        var insuranceCoverage: ArrayList<String> = ArrayList<String>()
                        for(child in insuranceSnapshot.child("insuranceCoverage").children){
                            insuranceCoverage.add(child.getValue().toString())
                            println(child.getValue().toString())
                        }
                        val insurance = Insurance(insuranceID,insuranceName,insuranceComp,insurancePlan,insuranceExpiryDate,insuranceReferral,insuranceCoverage)
                        insuranceList.add(insurance)
                        println(insuranceList)

                    }

                    binding.referralInsuranceRecyclerView.adapter?.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



        val insuranceAdapter = RecyclerViewAdapter(insuranceList,
            RecyclerViewAdapter.ClaimListener { insuranceID ->

                val it = view
                if (it != null) {
                    Navigation.findNavController(it).navigate(ReferralInsuranceListingFragmentDirections.actionReferralInsuranceListingFragmentToApplyClaimFragment(insuranceID))
                }
            })



        binding.referralInsuranceRecyclerView.adapter = insuranceAdapter
        binding.referralInsuranceRecyclerView.setHasFixedSize(true)

        binding.btnToApplyClaim.setOnClickListener(){

        }

        binding.btnToRenewInsurance.setOnClickListener(){
            val action = ReferralInsuranceListingFragmentDirections.actionReferralInsuranceListingFragmentToRenewInsuranceFragment()
            Navigation.findNavController(it).navigate(action)
        }



        return binding.root
    }






}