package my.edu.tarc.rewardreferralapp

import android.os.Bundle
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
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val insuranceRef = database.getReference("Insurance")
    private val referralInsuranceRef = database.getReference("ReferralInsurance")
    private lateinit var loadingDlg: LoadingDialog
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



        //insuranceList.clear()
        //tempInsuranceList.clear()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    //DetachListener()
                    val action = ReferralInsuranceListingFragmentDirections.actionReferralInsuranceListingFragmentToHomepage()
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)



        loadData()


        binding.searchInsuranceReferral.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                loadingDlg.showAlertDialog()
                tempInsuranceList.clear()
                if (newText!!.isNotEmpty()) {
                    tempInsuranceList =
                        insuranceList.filter { r -> r.insuranceName.toString().toUpperCase().contains(newText.toString().toUpperCase()) } as ArrayList<Insurance>

                }else{
                    tempInsuranceList = insuranceList
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
                    //DetachListener()
                    Navigation.findNavController(it).navigate(ReferralInsuranceListingFragmentDirections.actionReferralInsuranceListingFragmentToApplyClaimFragment(insuranceID,insuranceReferralID))
                }
            },
            RecyclerViewAdapter.CancelListener{ insuranceReferralID ->
                val it = view
                if(it != null){
                    //DetachListener()
                    Navigation.findNavController(it).navigate(ReferralInsuranceListingFragmentDirections.actionReferralInsuranceListingFragmentToCancelInsuranceFragment(insuranceReferralID))
                }
            }
        )



        binding.referralInsuranceRecyclerView.adapter = insuranceAdapter
        binding.referralInsuranceRecyclerView.setHasFixedSize(true)
        binding.referralInsuranceRecyclerView.adapter?.notifyDataSetChanged()

    }


    override fun onDetach() {
        super.onDetach()
        //DetachListener()
    }


    /*private fun DetachListener(){
        insuranceRef.removeEventListener(insuranceListener)
        referralInsuranceRef.removeEventListener(referralInsuranceListener)
    }*/


    private fun loadData(){
        loadingDlg = LoadingDialog(requireActivity())
        loadingDlg.showAlertDialog()

        referralInsuranceRef.orderByChild("referralUID").equalTo(referralUID).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    referralInsuranceList.clear()
                    for(referralInsSnapshot in snapshot.children){
                        if(referralInsSnapshot.child("status").getValue()!!.equals("Active")){
                            val insuranceReferralID: String = referralInsSnapshot.child("insuranceReferralID").getValue().toString()
                            val insuranceID: String = referralInsSnapshot.child("insuranceID").getValue().toString()
                            val referralUID: String = referralInsSnapshot.child("referralUID").getValue().toString()
                            val insuranceExpiryDate: Date = Date(referralInsSnapshot.child("insuranceExpiryDate").child("time").getValue() as Long)
                            val status: String = referralInsSnapshot.child("status").getValue().toString()
                            val refIns: ReferralInsurance = ReferralInsurance(insuranceReferralID,insuranceID,referralUID,insuranceExpiryDate,status)
                            referralInsuranceList.add(refIns)
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        insuranceRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    insuranceList.clear()
                    for (insuranceSnapshot in snapshot.children){

                        for(item in referralInsuranceList){
                            if(item.insuranceID.equals(insuranceSnapshot.child("insuranceID").getValue().toString())){
                                //insuranceList.add(insuranceSnapshot.getValue(Insurance::class.java)!!)
                                val insuranceID: String = insuranceSnapshot.child("insuranceID").getValue().toString()
                                val insuranceName: String = insuranceSnapshot.child("insuranceName").getValue().toString()
                                val insuranceType: String = insuranceSnapshot.child("insuranceType").getValue().toString()
                                val insuranceComp: String = insuranceSnapshot.child("insuranceComp").getValue().toString()
                                val insurancePlan: String = insuranceSnapshot.child("insurancePlan").getValue().toString()
                                //println(insuranceSnapshot.child("insuranceCoverage").getValue().toString())
                                var insuranceCoverage: ArrayList<String> = ArrayList<String>()
                                for(child in insuranceSnapshot.child("insuranceCoverage").children){
                                    insuranceCoverage.add(child.getValue().toString())
                                    //println(child.getValue().toString())
                                }
                                val insurance = Insurance(
                                    insuranceID = insuranceID,
                                    insuranceName = insuranceName,
                                    insuranceComp = insuranceComp,
                                    insurancePlan = insurancePlan,
                                    insuranceCoverage = insuranceCoverage,
                                    insuranceType = insuranceType
                                )
                                insuranceList.add(insurance)
                            }

                        }


                    }

                }
                changeView(insuranceList,referralInsuranceList)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        loadingDlg.dismissAlertDialog()
    }


}