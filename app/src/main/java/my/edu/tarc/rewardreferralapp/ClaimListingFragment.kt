package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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

    private lateinit var insuranceListener: ValueEventListener
    private lateinit var claimListener: ValueEventListener

    private lateinit var binding: FragmentClaimListingBinding

    private var insuranceList: ArrayList<Insurance> = ArrayList()

    private var claimList: ArrayList<Claim> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_claim_listing, container, false)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val action = ClaimListingFragmentDirections.actionClaimListingFragmentToHomepage()
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        binding.btnBackClaimListing.setOnClickListener(){
            val action = ClaimListingFragmentDirections.actionClaimListingFragmentToHomepage()
            Navigation.findNavController(requireView()).navigate(action)
        }

        referralUID = CheckUser().getCurrentUserUID()!!

        loadData()

        binding.tlClaimStatus.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                //loadingDlg.showAlertDialog()
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
                    Navigation.findNavController(it).navigate(ClaimListingFragmentDirections.actionClaimListingFragmentToClaimDetailsFragment(claimUUID))
                }
            })

        binding.claimRecyclerView.adapter = claimAdapter
        binding.claimRecyclerView.setHasFixedSize(true)

    }

    private fun loadData(){
        claimListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    claimList.clear()
                    for(currentClaim in snapshot.children){
                        val claimUUID = currentClaim.child("claimUUID").value.toString()
                        val claimID = currentClaim.child("claimID").value.toString()
                        val dt = Date(currentClaim.child("accidentDateTime").child("time").value as Long)
                        val accidentPlaceLong = currentClaim.child("accidentPlace").child("latitude").value as Double
                        val accidentPlaceLat = currentClaim.child("accidentPlace").child("longitude").value as Double
                        val accidentPlace: LatLng = LatLng(accidentPlaceLat,accidentPlaceLong)
                        val accidentType = currentClaim.child("accidentType").value.toString()
                        val accidentDesc = currentClaim.child("accidentDesc").value.toString()
                        val mileage = currentClaim.child("mileage").value.toString().toInt()
                        val imgMileage = currentClaim.child("imgMileage").value.toString()
                        val imgDamage = currentClaim.child("imgDamage").value.toString()
                        val applyDateTime: Date = Date(currentClaim.child("applyDateTime").child("time").value as Long)
                        val approveDateTime: Date? = if(currentClaim.child("approveDateTime").value == null){
                            null
                        }else{
                            Date(currentClaim.child("approveDateTime").child("time").value as Long)
                        }
                        val claimStatus = currentClaim.child("claimStatus").value.toString()
                        val insuranceID = currentClaim.child("insuranceID").value.toString()
                        val referralUID = currentClaim.child("referralUID").value.toString()
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

    }


    private fun getInsurance(){
        insuranceListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    insuranceList.clear()
                    for (insuranceSnapshot in snapshot.children){

                        for(item in claimList){
                            if(item.insuranceID.equals(insuranceSnapshot.child("insuranceID").value.toString())){
                                val insuranceID: String = insuranceSnapshot.child("insuranceID").value.toString()
                                val insuranceName: String = insuranceSnapshot.child("insuranceName").value.toString()
                                val insuranceType: String = insuranceSnapshot.child("insuranceType").value.toString()
                                val insuranceComp: String = insuranceSnapshot.child("insuranceComp").value.toString()
                                val insurancePlan: String = insuranceSnapshot.child("insurancePlan").value.toString()
                                val insuranceCoverage: ArrayList<String> = ArrayList<String>()
                                for(child in insuranceSnapshot.child("insuranceCoverage").children){
                                    insuranceCoverage.add(child.value.toString())

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
                    changeView(claimList,insuranceList)
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        insuranceRef.addListenerForSingleValueEvent(insuranceListener)
    }


}