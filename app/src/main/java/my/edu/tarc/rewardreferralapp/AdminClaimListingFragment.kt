package my.edu.tarc.rewardreferralapp

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.adapter.AdminClaimAdapter
import my.edu.tarc.rewardreferralapp.data.Claim
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
    private val referralRef = database.getReference("Referral")
    private val claimRef = database.getReference("Claim")
    private lateinit var claimListener: ValueEventListener
    private lateinit var referralListener: ValueEventListener

    private lateinit var binding: FragmentAdminClaimListingBinding

    private var claimList: ArrayList<Claim> = ArrayList()
    private var tempClaimList: ArrayList<Claim> = ArrayList()

    private var referralList: ArrayList<Referral> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_admin_claim_listing, container, false)

        val swipe = object: MySwipeHelper(requireActivity(), binding.rvClaimList, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(requireContext(), "Update", 30, R.drawable.ic_baseline_edit_24, Color.parseColor("#2266FF"),
                    object: MyButtonClickListener{
                        override fun onClick(pos: Int) {

                            val claimID = claimList[pos].claimID
                            val referralID = claimList[pos].insuranceReferral

                            val action = AdminClaimListingFragmentDirections.actionAdminClaimListingFragmentToApproveClaimFragment(claimID!!,referralID!!)
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

                if (newText!!.isNotEmpty()) {
                    tempClaimList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    for (claim in claimList) {
                        println(claim.claimID)
                        val combineText:String = claim.claimID!! + "-" + claim.insuranceReferral
                        if (combineText.lowercase(Locale.getDefault()).contains(search)) {
                            tempClaimList.add(claim)
                        }
                    }

                    adapter = AdminClaimAdapter(requireActivity(), tempClaimList, referralList)
                    binding.rvClaimList.adapter = adapter

                    binding.rvClaimList.adapter!!.notifyDataSetChanged()

                }

                return true
            }

        })

        loadReferral()
        loadData()
        onRefresh()

        return binding.root
    }

    private fun loadReferral(){
        referralListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    referralList.clear()
                    for(currentReferral in snapshot.children){
                        val referralID: String = currentReferral.child("referralID").getValue().toString()
                        val fullName: String = currentReferral.child("fullName").getValue().toString()
                        val referral: Referral = Referral(referralID = referralID, fullName = fullName)
                        referralList.add(referral)
                    }
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
                        val claim: Claim = Claim(claimID,dt,accidentPlace,accidentType,accidentDesc,mileage,imgMileage,imgDamage,applyDateTime,approveDateTime,claimStatus,insuranceID,insuranceReferral)


                        claimList.add(claim)

                    }

                    tempClaimList.addAll(claimList)
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.rvClaimList.visibility = View.VISIBLE
                    binding.rvClaimList.adapter?.notifyDataSetChanged()

                } else {
                    claimList.clear()
                    tempClaimList.clear()
                    binding.rvClaimList.visibility = View.INVISIBLE
                    binding.rvClaimList.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        claimRef.addListenerForSingleValueEvent(claimListener)

        adapter = AdminClaimAdapter(requireActivity(), claimList, referralList)
        binding.rvClaimList.adapter = adapter
    }

    private fun onRefresh() {
        binding.srlClaimList.setOnRefreshListener {
            loadData()
            Toast.makeText(requireContext(), "Refresh", Toast.LENGTH_SHORT).show()
            binding.srlClaimList.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        claimRef.removeEventListener(claimListener)
        referralRef.removeEventListener(referralListener)
    }


}