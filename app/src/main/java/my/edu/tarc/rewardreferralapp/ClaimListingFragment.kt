package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.adapter.ClaimListAdapter
import my.edu.tarc.rewardreferralapp.adapter.RecyclerViewAdapter
import my.edu.tarc.rewardreferralapp.data.Claim
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentClaimListingBinding
import java.util.*

class ClaimListingFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val insuranceRef = database.getReference("Insurance")

    private val claimListing: List<Claim> = listOf(
        Claim("CL001", Date("2022/02/25"),"KKB","Theft","ASDF",1234,"","",null,"Pending","IN001","IR001"),
        Claim("CL002",Date("2022/02/27"),"KKB2","OwnDamage","ASDF",1234,"","",null,"Accepted","IN002","IR001")
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentClaimListingBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_claim_listing, container, false)

        val insuranceList = ArrayList<Insurance>()

        insuranceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){

                    for (insuranceSnapshot in snapshot.children){

                        for(item in claimListing){
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

                    binding.claimRecyclerView.adapter?.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        val insuranceAdapter = ClaimListAdapter(claimListing, insuranceList,
            ClaimListAdapter.ViewListener { claimID ->

                val it = view
                if (it != null) {
                    Navigation.findNavController(it).navigate(ClaimListingFragmentDirections.actionClaimListingFragmentToClaimDetailsFragment(claimID))
                }
            })



        binding.claimRecyclerView.adapter = insuranceAdapter
        binding.claimRecyclerView.setHasFixedSize(true)


        return binding.root
    }

}