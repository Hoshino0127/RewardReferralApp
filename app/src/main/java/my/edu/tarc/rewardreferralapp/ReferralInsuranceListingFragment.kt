package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.renderscript.Sampler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.adapter.RecyclerViewAdapter
import my.edu.tarc.rewardreferralapp.data.ReferralInsurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferralInsuranceListingBinding
import my.edu.tarc.rewardreferralapp.dialog.LoadingDialog
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import java.util.*
import kotlin.collections.ArrayList


class ReferralInsuranceListingFragment : Fragment() {
    private lateinit var binding: FragmentReferralInsuranceListingBinding

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val insuranceRef = database.getReference("Insurance")
    private val referralInsuranceRef = database.getReference("ReferralInsurance")

    private lateinit var referralUID: String
    private lateinit var insuranceListener: ValueEventListener
    private lateinit var referralInsuranceListener: ValueEventListener

    private var insuranceList = ArrayList<Insurance>()
    private var referralInsuranceList = ArrayList<ReferralInsurance>()
    private var tempInsuranceList = ArrayList<Insurance>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_referral_insurance_listing, container, false)

        referralUID = CheckUser().getCurrentUserUID()!!

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val action = ReferralInsuranceListingFragmentDirections.actionReferralInsuranceListingFragmentToHomepage()
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        binding.btnBackReferralInsuranceListing.setOnClickListener(){
            val action = ReferralInsuranceListingFragmentDirections.actionReferralInsuranceListingFragmentToHomepage()
            Navigation.findNavController(requireView()).navigate(action)
        }

        loadData()

        binding.searchInsuranceReferral.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempInsuranceList.clear()
                if (newText!!.isNotEmpty()) {
                    tempInsuranceList =
                        insuranceList.filter { r -> r.insuranceName.toString().toUpperCase().contains(newText.toString().toUpperCase()) } as ArrayList<Insurance>

                }

                changeView(tempInsuranceList,referralInsuranceList)

                return true
            }

        })

        return binding.root
    }

    private fun changeView(insuranceList: ArrayList<Insurance>, referralInsuranceList: ArrayList<ReferralInsurance>){
        val insuranceAdapter = RecyclerViewAdapter(insuranceList,referralInsuranceList,
            RecyclerViewAdapter.ClaimListener { insuranceID, insuranceReferralID ->

                val it = view
                if (it != null) {
                    Navigation.findNavController(it).navigate(ReferralInsuranceListingFragmentDirections.actionReferralInsuranceListingFragmentToApplyClaimFragment(insuranceID,insuranceReferralID))
                }
            },
            RecyclerViewAdapter.CancelListener{ insuranceReferralID ->
                val it = view
                if(it != null){
                    Navigation.findNavController(it).navigate(ReferralInsuranceListingFragmentDirections.actionReferralInsuranceListingFragmentToCancelInsuranceCustFragment(insuranceReferralID))
                }
            }
        )

        binding.referralInsuranceRecyclerView.adapter = insuranceAdapter
        binding.referralInsuranceRecyclerView.setHasFixedSize(true)
        binding.referralInsuranceRecyclerView.adapter?.notifyDataSetChanged()

    }


    private fun loadData(){


        referralInsuranceListener = object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    referralInsuranceList.clear()
                    for(referralInsSnapshot in snapshot.children){
                        val insuranceExpiryDate = Date(referralInsSnapshot.child("insuranceExpiryDate").child("time").value as Long)
                        if(referralInsSnapshot.child("status").value!! == "Active" && !(Date().after(insuranceExpiryDate))){
                            val insuranceReferralID: String = referralInsSnapshot.child("insuranceReferralID").value.toString()
                            val insuranceID: String = referralInsSnapshot.child("insuranceID").value.toString()
                            val referralUID: String = referralInsSnapshot.child("referralUID").value.toString()
                            val status: String = referralInsSnapshot.child("status").value.toString()
                            val refIns = ReferralInsurance(insuranceReferralID,insuranceID,referralUID,insuranceExpiryDate,status)
                            referralInsuranceList.add(refIns)
                        }
                    }
                    getInsurance()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        referralInsuranceRef.orderByChild("referralUID").equalTo(referralUID).addValueEventListener(referralInsuranceListener)

    }

    private fun getInsurance(){
        insuranceListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    insuranceList.clear()
                    for (insuranceSnapshot in snapshot.children) {

                        for (item in referralInsuranceList) {
                            if (item.insuranceID.equals(
                                    insuranceSnapshot.child("insuranceID").value.toString()
                                )
                            ) {
                                val insuranceID: String =
                                    insuranceSnapshot.child("insuranceID").value.toString()
                                val insuranceName: String =
                                    insuranceSnapshot.child("insuranceName").value.toString()
                                val insuranceType: String =
                                    insuranceSnapshot.child("insuranceType").value.toString()
                                val insuranceComp: String =
                                    insuranceSnapshot.child("insuranceComp").value.toString()
                                val insurancePlan: String =
                                    insuranceSnapshot.child("insurancePlan").value.toString()
                                val insuranceCoverage: ArrayList<String> = ArrayList<String>()
                                for (child in insuranceSnapshot.child("insuranceCoverage").children) {
                                    insuranceCoverage.add(child.value.toString())
                                }
                                val insurancePrice: Double = insuranceSnapshot.child("insurancePrice").value.toString().toDouble()
                                val insuranceImg: String = insuranceSnapshot.child("insuranceImg").value.toString()
                                val insurance = Insurance(insuranceID, insuranceName, insuranceComp, insurancePlan, insuranceCoverage, insurancePrice, insuranceType,insuranceImg)
                                insuranceList.add(insurance)
                            }

                        }


                    }
                    changeView(insuranceList,referralInsuranceList)


                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        insuranceRef.addListenerForSingleValueEvent(insuranceListener)
    }


}