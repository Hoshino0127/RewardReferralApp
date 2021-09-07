package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavAction
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.adapter.RewardMyAdapter
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardMyBinding
import com.google.firebase.database.*
import my.edu.tarc.rewardreferralapp.data.RefferalReward

class RewardMyFragment : Fragment() {

    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val rewardRef = database.getReference("Reward")
    val refRewRef = database.getReference("RefferalReward")
    val refferalRewardList = ArrayList<RefferalReward>()
    var rewardList = ArrayList<Reward>()
    val referralID = "R0001"
    lateinit var binding: FragmentRewardMyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reward_my, container, false)

        getPendingClaimReward()


        return binding.root

    }

    private fun getPendingClaimReward() {

        var qryRefRew: Query = refRewRef.orderByChild("refferalID").equalTo(referralID)
        qryRefRew.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (refrewsnap in snapshot.children) {

                        if (refrewsnap.child("status").getValue().toString() == "Pending") {
                            refferalRewardList.add(refrewsnap.getValue(RefferalReward::class.java)!!)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })

        rewardRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (rewardsnap in snapshot.children) {

                        for (item in refferalRewardList) {
                            if (item.rewardID == rewardsnap.child("rewardID").getValue()
                                    .toString()
                            ) {
                                rewardList.add(rewardsnap.getValue(Reward::class.java)!!)
                            }
                        }

                    }

                    showView()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })

    }


    private fun showView() {

        val rewardApter = RewardMyAdapter(
            rewardList,
            refferalRewardList
        )

        binding.MyRewardRV.adapter = rewardApter
        binding.MyRewardRV.setHasFixedSize(true)

        var layoutManager = LinearLayoutManager(getActivity())
        layoutManager.setReverseLayout(true)
        layoutManager.setStackFromEnd(true)
        binding.MyRewardRV.setLayoutManager(layoutManager)


        binding.fbtnNext.setOnClickListener() {
            var chkRewardList: ArrayList<String> = ArrayList()
            val data = rewardApter.getchekList()
            for (chkItem in data) {
                chkRewardList.add(chkItem.rewardClaimID!!)
            }

            var rewardClaimID: String = ""

            if (chkRewardList.size == 1) {
                rewardClaimID = chkRewardList[0]
            }

            val action =
                RewardMyFragmentDirections.actionRewardMyFragmentToRewardDeliveryDetailsFragment(
                    chkRewardList.toTypedArray(),
                    rewardClaimID
                )
            Navigation.findNavController(requireView()).navigate(action)
        }


    }

}