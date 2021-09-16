package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.adapter.ClaimListAdapter
import my.edu.tarc.rewardreferralapp.data.Claim
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentClaimListingBinding
import my.edu.tarc.rewardreferralapp.dialog.LoadingDialog
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import java.util.*

class ClaimListingFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private var insuranceRef = database.getReference("Insurance")
    private val claimRef = database.getReference("Claim")
    private var referralUID = ""
    private lateinit var loadingDlg: LoadingDialog

    private lateinit var insuranceListener: ValueEventListener
    private lateinit var claimListener: ValueEventListener

    private lateinit var binding: FragmentClaimListingBinding

    private var insuranceList: ArrayList<Insurance> = ArrayList()

    private var claimList: ArrayList<Claim> = ArrayList()

    /*private val claimListing: List<Claim> = listOf(
        Claim("CL001", Date("2022/02/25"),
            LatLng(3.157764,101.711861),"Theft","ASDF",1234,"","",Date(),null,"Pending","IN001","IR001"),
        Claim("CL002",Date("2022/02/27"),
            LatLng(3.157764,101.711861),"OwnDamage","ASDF",1234,"","",Date(),null,"Accepted","IN001","IR001")
    )*/


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_claim_listing, container, false)

        referralUID = CheckUser().getCurrentUserUID()!!

        claimList.clear()
        insuranceList.clear()

        loadingDlg = LoadingDialog(requireActivity())

        loadingDlg.showAlertDialog()
        claimListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(currentClaim in snapshot.children){
                        val claimUUID = currentClaim.child("claimUUID").getValue().toString()
                        val claimID = currentClaim.child("claimID").getValue().toString()
                        val dt = Date(currentClaim.child("accidentDateTime").child("time").getValue() as Long)
                        val accidentPlaceLong = currentClaim.child("accidentPlace").child("latitude").getValue() as Double
                        val accidentPlaceLat = currentClaim.child("accidentPlace").child("longitude").getValue() as Double
                        val accidentPlace: LatLng = LatLng(accidentPlaceLat,accidentPlaceLong)
                        val accidentType = currentClaim.child("accidentType").getValue().toString()
                        val accidentDesc = currentClaim.child("accidentDesc").getValue().toString()
                        val mileage = currentClaim.child("mileage").getValue().toString().toInt()
                        val imgMileage = currentClaim.child("imgMileage").getValue().toString()
                        val imgDamage = currentClaim.child("imgDamage").getValue().toString()
                        val applyDateTime: Date = Date(currentClaim.child("applyDateTime").child("time").getValue() as Long)
                        val approveDateTime: Date? = if(currentClaim.child("approveDateTime").getValue() == null){
                            null
                        }else{
                            Date(currentClaim.child("approveDateTime").child("time").getValue() as Long)
                        }
                        val claimStatus = currentClaim.child("claimStatus").getValue().toString()
                        val insuranceID = currentClaim.child("insuranceID").getValue().toString()
                        val referralUID = currentClaim.child("referralUID").getValue().toString()
                        val claim: Claim = Claim(claimUUID,claimID,dt,accidentPlace,accidentType,accidentDesc,mileage,imgMileage,imgDamage,applyDateTime,approveDateTime,claimStatus,insuranceID,referralUID)


                        claimList.add(claim)
                        getInsurance()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        claimRef.orderByChild("referralUID").equalTo(referralUID).addValueEventListener(claimListener)





        binding.tlClaimStatus.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                loadingDlg.showAlertDialog()
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

        binding.btnToViewClaim.setOnClickListener(){
            DetachListener()
            val action = ClaimListingFragmentDirections.actionClaimListingFragmentToAdminClaimListingFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }

    fun changeView(claimList: List<Claim>, insuranceList: List<Insurance>){
        val claimAdapter = ClaimListAdapter(claimList, insuranceList,
            ClaimListAdapter.ViewListener { claimUUID ->

                val it = view
                if (it != null) {
                    DetachListener()
                    Navigation.findNavController(it).navigate(ClaimListingFragmentDirections.actionClaimListingFragmentToClaimDetailsFragment(claimUUID))
                }
            })

        binding.claimRecyclerView.adapter = claimAdapter
        binding.claimRecyclerView.setHasFixedSize(true)
        loadingDlg.dismissAlertDialog()
    }

    override fun onDetach() {
        super.onDetach()
        DetachListener()
    }

    private fun getInsurance(){
        insuranceListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){

                    for (insuranceSnapshot in snapshot.children){

                        for(item in claimList){
                            if(item.insuranceID.equals(insuranceSnapshot.child("insuranceID").getValue().toString())){
                                //insuranceList.add(insuranceSnapshot.getValue(Insurance::class.java)!!)
                                val insuranceID: String = insuranceSnapshot.child("insuranceID").getValue().toString()
                                val insuranceName: String = insuranceSnapshot.child("insuranceName").getValue().toString()
                                val insuranceType: String = insuranceSnapshot.child("insuranceType").getValue().toString()
                                val insuranceComp: String = insuranceSnapshot.child("insuranceComp").getValue().toString()
                                val insurancePlan: String = insuranceSnapshot.child("insurancePlan").getValue().toString()
                                val insuranceCoverage: ArrayList<String> = ArrayList<String>()
                                for(child in insuranceSnapshot.child("insuranceCoverage").children){
                                    insuranceCoverage.add(child.getValue().toString())

                                }
                                val insurance = Insurance(
                                    insuranceID = insuranceID,
                                    insuranceName = insuranceName,
                                    insuranceComp = insuranceComp,
                                    insurancePlan = insurancePlan,
                                    insuranceCoverage = insuranceCoverage,
                                    insuranceType = insuranceType
                                )
                                println(insuranceID)
                                insuranceList.add(insurance)
                            }
                            println(item.claimID)
                        }


                    }
                    println(insuranceList)
                    changeView(claimList,insuranceList)
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        //insuranceRef = database.getReference("insurance")
        insuranceRef.addListenerForSingleValueEvent(insuranceListener)
    }

    private fun DetachListener(){
        insuranceRef.removeEventListener(insuranceListener)
        claimRef.removeEventListener(claimListener)
    }

}