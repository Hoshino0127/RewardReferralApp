package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.adapter.ReferralListAdapter
import my.edu.tarc.rewardreferralapp.adapter.UserReferralListAdapter
import my.edu.tarc.rewardreferralapp.data.ReferralList
import my.edu.tarc.rewardreferralapp.data.UserReferralList
import my.edu.tarc.rewardreferralapp.databinding.FragmentUserReferralListsBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import java.util.*
import kotlin.collections.ArrayList

class UserReferralListsFragment : Fragment() {

    private var userReferList = ArrayList<UserReferralList>()

    private var tempUserReferList = ArrayList<UserReferralList>()
    lateinit var adapter : UserReferralListAdapter

    private var tempbinding: FragmentUserReferralListsBinding? = null
    private val binding get() = tempbinding!!

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.getReference("Referral")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tempbinding = FragmentUserReferralListsBinding.inflate(inflater,  container ,false)
        //println(userReferList)
        binding.shimmerViewContainer.startShimmer()

        binding.userRefRecyclerView.setHasFixedSize(true)

        binding.btnBackReferralDownLine.setOnClickListener(){
            val action = UserReferralListsFragmentDirections.actionUserReferralListsFragmentToUserProfileFragment()
            Navigation.findNavController(it).navigate(action)
        }

        //do search view
        binding.searchUserRefName.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!!.isNotEmpty()){
                    tempUserReferList.clear()

                    val searching = newText.lowercase(Locale.getDefault())
                    for(UserReferral in userReferList){
                        if(UserReferral.name.lowercase(Locale.getDefault()).contains(searching)) {
                            tempUserReferList.add(UserReferral)
                        }
                    }
                    adapter = UserReferralListAdapter(requireActivity(), tempUserReferList)
                    binding.userRefRecyclerView.adapter = adapter

                    binding.userRefRecyclerView.adapter!!.notifyDataSetChanged()
                }
                else{
                    tempUserReferList.clear()
                    tempUserReferList.addAll(userReferList)

                    binding.userRefRecyclerView.adapter!!.notifyDataSetChanged()
                }
                return true
            }
        })
        loadData()
        return binding.root
    }

    //load user down line
    private fun loadData(){
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userReferList.clear()
                    for (referralSnapshot in snapshot.children) {
                        if(referralSnapshot.child("referralUpline").value.toString() != "" ) {
                            if (referralSnapshot.child("referralUpline").value.toString() != "none") {
                                if (referralSnapshot.child("referralUpline").value.toString() == CheckUser().getCurrentUserUID()){
                                    val name: String =
                                        referralSnapshot.child("fullName").value.toString()
                                    val contact: String =
                                        referralSnapshot.child("contactNo").value.toString()
                                    val email: String =
                                        referralSnapshot.child("email").value.toString()

                                    val userReferral = UserReferralList(name, contact, email)
                                    userReferList.add(userReferral)
                                }
                            }
                        }
                    }
                    tempUserReferList.addAll(userReferList)
                    binding.shimmerViewContainer.startShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.userRefRecyclerView.visibility = View.VISIBLE
                    binding.userRefRecyclerView.adapter?.notifyDataSetChanged()
                }else{
                    userReferList.clear()
                    Handler().postDelayed ({
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                    }, 3000)
                    binding.userRefRecyclerView.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        adapter = UserReferralListAdapter(requireActivity(), userReferList)
        binding.userRefRecyclerView.adapter = adapter
    }

}