package my.edu.tarc.rewardreferralapp

import android.app.Dialog
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
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import my.edu.tarc.rewardreferralapp.helper.MyLottie


class RewardDeliveryListFragment : Fragment() {

    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val rewdelref = database.getReference("RewardDelivery")
    private val refRewRef = database.getReference("RefferalReward")
    private var rewardDeliveryList = ArrayList<RewardDelivery>()
    private var refrewList = ArrayList<RefferalReward>()
    private lateinit var binding: FragmentRewardDeliveryListBinding
    private val referralID = CheckUser().getCurrentUserUID()

    private var loadingDialog: Dialog?= null


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

        showLoading()
        getDeliveryDetails()
        hideLoading()


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
                                if (!rewardDeliveryList.contains(
                                        Deliverysnap.getValue(
                                            RewardDelivery::class.java
                                        )
                                    )
                                ) {
                                    rewardDeliveryList.add(Deliverysnap.getValue(RewardDelivery::class.java)!!)
                                }
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
                            deliveryID,
                            "List"
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

    private fun hideLoading() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }


}