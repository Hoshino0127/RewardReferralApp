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
import com.google.firebase.database.*
import my.edu.tarc.rewardreferralapp.adapter.RewardDeliveryAdapter
import my.edu.tarc.rewardreferralapp.data.RefferalReward
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.data.RewardDelivery
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardDeliveryListBinding
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardDeliveyDetailsBinding


class RewardDeliveryListFragment : Fragment() {

    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val rewdelref = database.getReference("RewardDelivery")
    val refRewRef = database.getReference("RefferalReward")
    var rewardDeliveryList = ArrayList<RewardDelivery>()
    var refrewList = ArrayList<RefferalReward>()
    lateinit var binding: FragmentRewardDeliveryListBinding
    val referralID = "R0001"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_reward_delivery_list,
            container,
            false
        )

        getDeliveryDetails()


        return binding.root
    }

    private fun getDeliveryDetails() {

        var qryrefrew: Query = refRewRef.orderByChild("refferalID").equalTo(referralID)
        qryrefrew.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (refrewsnap in snapshot.children) {
                        refrewList.add(refrewsnap.getValue(RefferalReward::class.java)!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })

        rewdelref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (Deliverysnap in snapshot.children) {

                        for (item in refrewList) {
                            if (item.deliveryID == Deliverysnap.child("deliveryID").getValue()
                                    .toString()
                            ) {
                                rewardDeliveryList.add(Deliverysnap.getValue(RewardDelivery::class.java)!!)
                            }
                        }

                    }
                    setRecycleView()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })


    }

    private fun setRecycleView() {
        val rewDelAdapter = RewardDeliveryAdapter(
            rewardDeliveryList,
            RewardDeliveryAdapter.CheckListener { deliveryID ->
                val it = view
                if (it != null) {
                    val action =
                        RewardDeliveryListFragmentDirections.actionRewardDeliveryListFragmentToRewardRedeemSuccessFragment(
                            deliveryID
                        )
                    Navigation.findNavController(requireView()).navigate(action)
                }

            })

        binding.RDLDeliveryRV.adapter = rewDelAdapter
        binding.RDLDeliveryRV.setHasFixedSize(true)

        var layoutManager = LinearLayoutManager(getActivity())
        layoutManager.setReverseLayout(true)
        layoutManager.setStackFromEnd(true)
        binding.RDLDeliveryRV.setLayoutManager(layoutManager)

    }


}