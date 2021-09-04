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
import my.edu.tarc.rewardreferralapp.adapter.RecyclerViewAdapter
import my.edu.tarc.rewardreferralapp.data.ReferralInsurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferralInsuranceListingBinding
import java.util.*
import kotlin.collections.ArrayList


class ReferralInsuranceListingFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val insuranceRef = database.getReference("Insurance")
    private val ReferralInsuranceRef = database.getReference("ReferralInsurance")
    private val referralID = "IR001"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentReferralInsuranceListingBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_referral_insurance_listing, container, false)


        val referralInsuranceList = ArrayList<ReferralInsurance>()
        val insuranceList = ArrayList<Insurance>()
        

        ReferralInsuranceRef.orderByChild("insuranceReferral").equalTo(referralID).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(referralInsSnapshot in snapshot.children){
                        referralInsuranceList.add(referralInsSnapshot.getValue(ReferralInsurance::class.java)!!)
                    }
                    println(referralInsuranceList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        insuranceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){

                    for (insuranceSnapshot in snapshot.children){

                        for(item in referralInsuranceList){
                            if(item.insuranceID.equals(insuranceSnapshot.child("insuranceID").getValue().toString())){
                                //insuranceList.add(insuranceSnapshot.getValue(Insurance::class.java)!!)
                                val insuranceID: String = insuranceSnapshot.child("insuranceID").getValue().toString()
                                val insuranceName: String = insuranceSnapshot.child("insuranceName").getValue().toString()
                                val insuranceComp: String = insuranceSnapshot.child("insuranceComp").getValue().toString()
                                val insurancePlan: String = insuranceSnapshot.child("insurancePlan").getValue().toString()
                                println(insuranceSnapshot.child("insuranceCoverage").getValue().toString())
                                var insuranceCoverage: ArrayList<String> = ArrayList<String>()
                                for(child in insuranceSnapshot.child("insuranceCoverage").children){
                                    insuranceCoverage.add(child.getValue().toString())
                                    println(child.getValue().toString())
                                }
                                val insurance = Insurance(insuranceID,insuranceName,insuranceComp,insurancePlan,insuranceCoverage)
                                insuranceList.add(insurance)
                                println(insuranceList)
                            }

                        }


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
                    Navigation.findNavController(it).navigate(ReferralInsuranceListingFragmentDirections.actionReferralInsuranceListingFragmentToApplyClaimFragment(insuranceID,referralID))
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