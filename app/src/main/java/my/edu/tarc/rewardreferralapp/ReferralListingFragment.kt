package my.edu.tarc.rewardreferralapp

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.adapter.ReferralListAdapter
import my.edu.tarc.rewardreferralapp.data.ReferralList
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferralListingBinding
import my.edu.tarc.rewardreferralapp.helper.MyButton
import my.edu.tarc.rewardreferralapp.helper.MyLottie
import my.edu.tarc.rewardreferralapp.helper.MySwipeHelper
import my.edu.tarc.rewardreferralapp.listener.MyButtonClickListener
import java.util.*
import kotlin.collections.ArrayList

class ReferralListingFragment : Fragment() {

    private var referralListing = ArrayList<ReferralList>()

    private var tempReferralListing = ArrayList<ReferralList>()
    lateinit var adapter : ReferralListAdapter

    private var tempbinding: FragmentReferralListingBinding? = null
    private val binding get() = tempbinding!!

    private var strMsg: String? = null

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.getReference("Referral")

    private var loadingDialog: Dialog?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        showLoading()

        tempbinding = FragmentReferralListingBinding.inflate(inflater,  container ,false)
        //println(referralListing)
        binding.shimmerViewContainer.startShimmer()

        binding.referralRecycler.setHasFixedSize(true)

        binding.btnBackReferralListing.setOnClickListener(){
            val action = ReferralListingFragmentDirections.actionReferralListingFragmentToStaffDashboardFragment()
            Navigation.findNavController(it).navigate(action)
        }

        //swipe delete
        val swipe = object: MySwipeHelper(requireActivity(), binding.referralRecycler, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(requireContext(), "Delete", 30, R.drawable.ic_baseline_delete_24, Color.parseColor("#F30E15"),
                    object: MyButtonClickListener {
                        override fun onClick(pos: Int) {

                            strMsg = referralListing[pos].name + " successfully deleted."

                            deleteData(referralListing[pos].name, object : FirebaseSuccessListener {
                                override fun onDataFound(isDataFetched: Boolean, Key: String) {
                                    if (isDataFetched && Key.isNotEmpty()){
                                        myRef.child(Key).removeValue()
                                    }
                                }
                            })
                            Toast.makeText(requireContext(), strMsg, Toast.LENGTH_SHORT).show()
                        }
                    }))
            }
        }

        //do search view
        binding.searchRefName.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!!.isNotEmpty()){
                    tempReferralListing.clear()

                    val searching = newText.lowercase(Locale.getDefault())
                    for(referral in referralListing){
                        if(referral.name.lowercase(Locale.getDefault()).contains(searching)) {
                            tempReferralListing.add(referral)
                        }
                    }
                    adapter = ReferralListAdapter(requireActivity(), tempReferralListing)
                    binding.referralRecycler.adapter = adapter

                    binding.referralRecycler.adapter!!.notifyDataSetChanged()
                }
                else{
                    tempReferralListing.clear()
                    tempReferralListing.addAll(referralListing)

                    binding.referralRecycler.adapter!!.notifyDataSetChanged()
                }
                return true
            }
        })
        loadData()
        return binding.root
    }

    //load data from firebase (Referral)
    private fun loadData(){
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    referralListing.clear()

                    for(referralSnapshot in snapshot.children){
                        val name: String = referralSnapshot.child("fullName").value.toString()
                        val contact: String = referralSnapshot.child("contactNo").value.toString()
                        val status: String = referralSnapshot.child("referralStatus").value.toString()

                        val referral = ReferralList(name, contact, status)
                        referralListing.add(referral)
                    }
                    tempReferralListing.addAll(referralListing)
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.referralRecycler.visibility = View.VISIBLE
                    binding.referralRecycler.adapter?.notifyDataSetChanged()
                }else{
                    referralListing.clear()
                    Handler().postDelayed ({
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                    }, 3000)
                    binding.referralRecycler.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        adapter = ReferralListAdapter(requireActivity(), referralListing)
        binding.referralRecycler.adapter = adapter
    }

    private fun deleteData(name : String?, dataFetched:FirebaseSuccessListener) {
        myRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    if (ds.exists()){
                        ds.key?.let { dataFetched.onDataFound(true, it) }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    interface FirebaseSuccessListener {
        fun onDataFound(isDataFetched: Boolean, Key: String)
    }

    private fun hideLoading() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }

}