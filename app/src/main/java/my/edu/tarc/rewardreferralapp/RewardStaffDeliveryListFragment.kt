package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.adapter.RewardDeliveryAdapter
import my.edu.tarc.rewardreferralapp.data.Claim
import my.edu.tarc.rewardreferralapp.data.RewardDelivery
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardStaffDeliveryListBinding


class RewardStaffDeliveryListFragment : Fragment() {

    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val rewdelref = database.getReference("RewardDelivery")
    var rewardDeliveryList = ArrayList<RewardDelivery>()
    var selectedDeliveryList: ArrayList<RewardDelivery> = ArrayList<RewardDelivery>()
    lateinit var binding: FragmentRewardStaffDeliveryListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        selectedDeliveryList.clear()
        rewardDeliveryList.clear()

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_reward_staff_delivery_list,
            container,
            false
        )

        getAllDeliveryDetails()

        binding.tlRSDLStatus.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedDeliveryList.clear()
                when (tab?.position) {
                    0 -> {
                        selectedDeliveryList =
                            rewardDeliveryList.filter { r -> r.status == "Pending" } as ArrayList<RewardDelivery>
                    }
                    1 -> {
                        selectedDeliveryList =
                            rewardDeliveryList.filter { r -> r.status == "Shipped" } as ArrayList<RewardDelivery>
                    }
                    2 -> {
                        selectedDeliveryList =
                            rewardDeliveryList.filter { r -> r.status == "Completed" } as ArrayList<RewardDelivery>
                    }
                }
                setAdapter(selectedDeliveryList)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        return binding.root
    }

    private fun getAllDeliveryDetails() {

        rewdelref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (Deliverysnap in snapshot.children) {

                        rewardDeliveryList.add(Deliverysnap.getValue(RewardDelivery::class.java)!!)

                    }

                    selectedDeliveryList =
                        rewardDeliveryList.filter { r -> r.status == "Pending" } as ArrayList<RewardDelivery>
                    setAdapter(selectedDeliveryList)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun setAdapter(adapterDeliveryList: ArrayList<RewardDelivery>) {
        val rewDelAdapter = RewardDeliveryAdapter(
            adapterDeliveryList,
            RewardDeliveryAdapter.CheckListener { deliveryID ->
                val it = view
                if (it != null) {
                    val action =
                        RewardStaffDeliveryListFragmentDirections.actionRewardStaffDeliveryListFragmentToRewardStaffDeliveryDetails(
                            deliveryID
                        )
                    Navigation.findNavController(requireView()).navigate(action)
                }
            })

        binding.RSDLDeliveryRV.adapter = rewDelAdapter
        binding.RSDLDeliveryRV.setHasFixedSize(true)

        var layoutManager = LinearLayoutManager(getActivity())
        layoutManager.setReverseLayout(true)
        layoutManager.setStackFromEnd(true)
        binding.RSDLDeliveryRV.setLayoutManager(layoutManager)
    }


}