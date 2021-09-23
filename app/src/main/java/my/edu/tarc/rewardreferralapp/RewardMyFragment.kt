package my.edu.tarc.rewardreferralapp

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import my.edu.tarc.rewardreferralapp.helper.MyLottie

class RewardMyFragment : Fragment() {

    private val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val rewardRef = database.getReference("Reward")
    private val refRewRef = database.getReference("RefferalReward")
    private val refferalRewardList = ArrayList<RefferalReward>()
    private var rewardList = ArrayList<Reward>()
    private val referralID = CheckUser().getCurrentUserUID()
    private lateinit var binding: FragmentRewardMyBinding
    private val handler = Handler(Looper.getMainLooper())

    private var loadingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        refferalRewardList.clear()
        rewardList.clear()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val action = RewardMyFragmentDirections.actionRewardMyFragmentToHomepage()
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reward_my, container, false)

        binding.btnBackRM.setOnClickListener(){
            val action = RewardMyFragmentDirections.actionRewardMyFragmentToUserProfileFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        showLoading()
        getPendingClaimReward()

        handler.postDelayed({
            hideLoading()
        }, 1500)


        return binding.root

    }

    private fun getPendingClaimReward() {


        handler.postDelayed({

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

        }, 1000)
    }


    private fun showView() {

        rewardList.sortByDescending { r -> r.rewardID }
        refferalRewardList.sortByDescending { r -> r.rewardID }

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
            if (data.size != 0) {
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
            } else {
                Toast.makeText(context, "Please seelct a reward to proceed", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    private fun hideLoading() {
        loadingDialog?.let { if (it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }

}