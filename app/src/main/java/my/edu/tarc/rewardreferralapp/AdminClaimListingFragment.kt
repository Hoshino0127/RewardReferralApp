package my.edu.tarc.rewardreferralapp

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.adapter.AdminClaimAdapter
import my.edu.tarc.rewardreferralapp.data.Claim
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.databinding.FragmentAdminClaimListingBinding
import my.edu.tarc.rewardreferralapp.helper.MyButton
import my.edu.tarc.rewardreferralapp.helper.MySwipeHelper
import my.edu.tarc.rewardreferralapp.listener.MyButtonClickListener
import java.util.*
import kotlin.collections.ArrayList


class AdminClaimListingFragment : Fragment() {

    private lateinit var adapter: AdminClaimAdapter

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private val claimRef = database.getReference("Claim")
    private val insuranceRef = database.getReference("Insurance")
    private val referralRef = database.getReference("Referral")

    private lateinit var claimListener: ValueEventListener
    private lateinit var insuranceListener: ValueEventListener
    private lateinit var referralListener: ValueEventListener

    private lateinit var binding: FragmentAdminClaimListingBinding

    private var claimList: ArrayList<Claim> = ArrayList()
    private var tempClaimList: ArrayList<Claim> = ArrayList()
    private var insuranceList: ArrayList<Insurance> = ArrayList()
    private var referralList: ArrayList<Referral> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_admin_claim_listing, container, false)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    //DetachListener()
                    //val action = AdminClaimListingFragmentDirections.actionAdminClaimListingFragmentToClaimListingFragment()
                    val action = AdminClaimListingFragmentDirections.actionAdminClaimListingFragmentToStaffDashboardFragment()
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        binding.btnBackListAdminClaim.setOnClickListener(){
            val action = AdminClaimListingFragmentDirections.actionAdminClaimListingFragmentToStaffDashboardFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        tempClaimList.clear()

        val swipe = object: MySwipeHelper(requireActivity(), binding.rvClaimList, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(requireContext(), "Update", 30, R.drawable.ic_baseline_edit_24, Color.parseColor("#2266FF"),
                    object: MyButtonClickListener{
                        override fun onClick(pos: Int) {

                            val claimUUID = claimList[pos].claimUUID
                            val referralUID = claimList[pos].referralUID

                            val action = AdminClaimListingFragmentDirections.actionAdminClaimListingFragmentToApproveClaimFragment(referralUID!!,claimUUID!!)
                            view?.let { Navigation.findNavController(it).navigate(action) }
                        }
                    }))
            }
        }

        binding.searchClaim.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempClaimList.clear()
                if (newText!!.isNotEmpty()) {

                    for (claim in claimList) {

                        tempClaimList = claimList.filter{c -> c.claimID!!.toUpperCase().contains(newText.toString().toUpperCase())} as ArrayList<Claim>
                    }

                    adapter = AdminClaimAdapter(requireActivity(), tempClaimList, referralList,insuranceList)
                    binding.rvClaimList.adapter = adapter
                    binding.rvClaimList.adapter!!.notifyDataSetChanged()

                }

                return true
            }

        })

        loadReferral()

        onRefresh()

        return binding.root
    }

    private fun loadReferral(){
        referralListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    referralList.clear()
                    for(currentReferral in snapshot.children){
                        val referralUID: String = currentReferral.child("referralUID").getValue().toString()
                        val fullName: String = currentReferral.child("fullName").getValue().toString()
                        val referral: Referral = Referral(fullName = fullName, referralUID = referralUID)
                        referralList.add(referral)
                    }
                    loadData()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        referralRef.addListenerForSingleValueEvent(referralListener)
    }

    private fun loadData() {

        claimListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    claimList.clear()
                    for (currentClaim in snapshot.children){
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

                    }

                    tempClaimList.addAll(claimList)

                }else{
                    claimList.clear()
                    tempClaimList.clear()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        claimRef.addListenerForSingleValueEvent(claimListener)

        insuranceListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    insuranceList.clear()
                    for(insSS in snapshot.children){
                        val insuranceID = insSS.child("insuranceID").getValue().toString()
                        val insuranceName = insSS.child("insuranceName").getValue().toString()
                        val insurance = Insurance(insuranceID = insuranceID, insuranceName = insuranceName)
                        insuranceList.add(insurance)
                    }
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.rvClaimList.visibility = View.VISIBLE
                    binding.rvClaimList.adapter?.notifyDataSetChanged()
                }else{

                    binding.rvClaimList.visibility = View.INVISIBLE
                    binding.rvClaimList.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }


        insuranceRef.addListenerForSingleValueEvent(insuranceListener)

        adapter = AdminClaimAdapter(requireActivity(), claimList, referralList, insuranceList)
        binding.rvClaimList.adapter = adapter
    }

    private fun onRefresh() {
        binding.srlClaimList.setOnRefreshListener {
            loadData()
            Toast.makeText(requireContext(), "Refresh", Toast.LENGTH_SHORT).show()
            binding.srlClaimList.isRefreshing = false
        }
    }

}