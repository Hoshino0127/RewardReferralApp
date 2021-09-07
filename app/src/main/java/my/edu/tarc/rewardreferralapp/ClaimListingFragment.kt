package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.adapter.ClaimListAdapter
import my.edu.tarc.rewardreferralapp.adapter.RecyclerViewAdapter
import my.edu.tarc.rewardreferralapp.data.Claim
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.data.ReferralInsurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentClaimListingBinding
import java.util.*

class ClaimListingFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val insuranceRef = database.getReference("Insurance")
    private val claimRef = database.getReference("Claim")
    private val referralID = "IR001"

    private lateinit var insuranceListener: ValueEventListener

    private lateinit var binding: FragmentClaimListingBinding

    private val claimListing: List<Claim> = listOf(
        Claim("CL001", Date("2022/02/25"),"KKB","Theft","ASDF",1234,"","",Date(),null,"Pending","IN001","IR001"),
        Claim("CL002",Date("2022/02/27"),"KKB2","OwnDamage","ASDF",1234,"","",Date(),null,"Accepted","IN001","IR001")
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_claim_listing, container, false)

        val insuranceList = ArrayList<Insurance>()

        val claimList = ArrayList<Claim>()

        claimRef.orderByChild("insuranceReferral").equalTo(referralID).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(claimSnapshot in snapshot.children){
                        claimList.add(claimSnapshot.getValue(Claim::class.java)!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        insuranceListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){

                    for (insuranceSnapshot in snapshot.children){

                        for(item in claimList){
                            if(item.insuranceID.equals(insuranceSnapshot.child("insuranceID").getValue().toString())){
                                //insuranceList.add(insuranceSnapshot.getValue(Insurance::class.java)!!)
                                val insuranceID: String = insuranceSnapshot.child("insuranceID").getValue().toString()
                                val insuranceName: String = insuranceSnapshot.child("insuranceName").getValue().toString()
                                val insuranceComp: String = insuranceSnapshot.child("insuranceComp").getValue().toString()
                                val insurancePlan: String = insuranceSnapshot.child("insurancePlan").getValue().toString()
                                var insuranceCoverage: ArrayList<String> = ArrayList<String>()
                                for(child in insuranceSnapshot.child("insuranceCoverage").children){
                                    insuranceCoverage.add(child.getValue().toString())

                                }
                                val insurance = Insurance(insuranceID,insuranceName,insuranceComp,insurancePlan,insuranceCoverage)
                                insuranceList.add(insurance)
                            }

                        }


                    }
                    changeView(claimList,insuranceList)
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        insuranceRef.addValueEventListener(insuranceListener)



        binding.tlClaimStatus.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var selectedClaims: ArrayList<Claim> = ArrayList<Claim>()
                when (tab?.position) {
                    0 -> {
                        selectedClaims = claimList
                    }
                    1 -> {
                        selectedClaims = claimList.filter{ s -> s.claimStatus == "Pending"} as ArrayList<Claim>
                    }
                    2 -> {
                        selectedClaims = claimList.filter{ s -> s.claimStatus == "Accepted"} as ArrayList<Claim>
                    }
                    3 -> {
                        selectedClaims = claimList.filter{ s -> s.claimStatus == "Rejected"} as ArrayList<Claim>
                    }
                }
                changeView(selectedClaims,insuranceList)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        return binding.root
    }

    fun changeView(claimList: List<Claim>, insuranceList: List<Insurance>){
        val claimAdapter = ClaimListAdapter(claimList, insuranceList,
            ClaimListAdapter.ViewListener { claimID ->

                val it = view
                if (it != null) {
                    Navigation.findNavController(it).navigate(ClaimListingFragmentDirections.actionClaimListingFragmentToClaimDetailsFragment(claimID, referralID))
                }
            })

        binding.claimRecyclerView.adapter = claimAdapter
        binding.claimRecyclerView.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        insuranceRef.removeEventListener(insuranceListener)
    }

}