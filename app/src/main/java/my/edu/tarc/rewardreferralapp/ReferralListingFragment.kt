package my.edu.tarc.rewardreferralapp

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.adapter.ReferralListAdapter
import my.edu.tarc.rewardreferralapp.data.ReferralList
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferralListingBinding
import my.edu.tarc.rewardreferralapp.helper.MyButton
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
    //private val myRefList = database.getReference("ReferralList")
    private val myRef = database.getReference("Referral")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        tempbinding = FragmentReferralListingBinding.inflate(inflater,  container ,false)

        //button add navigate to add fragment
//        binding.btnInsertNew.setOnClickListener() {
//            val action = ReferralListingFragmentDirections.actionReferralListingFragmentToAddNewReferralFragment()
//            Navigation.findNavController(it).navigate(action)
//            //insertValue()
//        }
        //println(referralListing)
        binding.referralRecycler.setHasFixedSize(true)


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
                        val referID: String = referralSnapshot.child("referralID").value.toString()
                        val name: String = referralSnapshot.child("fullName").value.toString()
                        val status: String = referralSnapshot.child("referralStatus").value.toString()

                        val referral = ReferralList(referID, name, status)
                        referralListing.add(referral)
                    }
                    tempReferralListing.addAll(referralListing)
                    binding.referralRecycler.visibility = View.VISIBLE
                    binding.referralRecycler.adapter?.notifyDataSetChanged()
                }else{
                    referralListing.clear()
                    binding.referralRecycler.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        adapter = ReferralListAdapter(requireActivity(), referralListing)
        binding.referralRecycler.adapter = adapter
    }

    //insert data into firebase
//    private fun insertValue(){
//        val refList: List<ReferralList> = listOf(
//            ReferralList("Lau Pin Jian", "Inactive"),
//            ReferralList("Woon Cui Yen", "Active"),
//            ReferralList("Lee Kok Ken", "Active"),
//            ReferralList("Soo Ji Ho", "Inactive"),
//            ReferralList("Lim Chan How", "Active"),
//            ReferralList("Tang Kok Hou", "Active"),
//            ReferralList("Chan Kin Lam", "Inactive")
//        )
//        //push into firebase
//        for(referral in refList){
//            myRef.push().setValue(referral)
//        }
//    }

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
}