package my.edu.tarc.rewardreferralapp

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.database.*
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.adapter.RewardListAdapter
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardListingBinding
import my.edu.tarc.rewardreferralapp.helper.MyLottie

class RewardListingFragment : Fragment() {


    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val rewardRef = database.getReference("Reward")

    private lateinit var binding: FragmentRewardListingBinding
    private var rewardList = ArrayList<Reward>()
    var rewardSearchList = ArrayList<Reward>()

    private var loadingDialog: Dialog?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rewardSearchList.clear()
        rewardList.clear()

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_reward_listing, container, false)



        showLoading()
        getReward()

        binding.btnRLSearch.setOnClickListener {
            val iMm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            iMm.hideSoftInputFromWindow(view?.windowToken, 0)
            view?.clearFocus()

            getRewardBySearch()

        }

        binding.fbtnRLAddNewReward.setOnClickListener() {

            val action =
                RewardListingFragmentDirections.actionRewardListingFragmentToRewardEntryFragment("")
            Navigation.findNavController(requireView()).navigate(action)
        }

        hideLoading()

        return binding.root

    }

    private fun getReward() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            rewardRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    rewardList.clear()
                    if (snapshot.exists()) {

                        for (rewardSnapshot in snapshot.children) {
                            rewardList.add(rewardSnapshot.getValue(Reward::class.java)!!)

                        }

                        setAdapter(rewardList)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                }
            })
        }, 1500)
    }

    private fun getRewardBySearch() {

        val iMm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        iMm.hideSoftInputFromWindow(view?.windowToken, 0)
        view?.clearFocus()

        rewardSearchList =
            rewardList.filter { r ->
                r.rewardName!!.uppercase()
                    .contains(binding.ptRLRewardName.text.toString().uppercase())
            } as ArrayList<Reward>

        setAdapter(rewardSearchList)

    }

    private fun setAdapter(RewardAdapterList: ArrayList<Reward>) {
        val rewardAdapter =
            RewardListAdapter(RewardAdapterList, RewardListAdapter.ViewListener { rewardID ->
                val it = view
                if (it != null) {
                    val action =
                        RewardListingFragmentDirections.actionRewardListingFragmentToRewardEntryFragment(
                            rewardID
                        )
                    Navigation.findNavController(requireView()).navigate(action)
                }
            })
        binding.RewardListRV.adapter = rewardAdapter
        binding.RewardListRV.setHasFixedSize(true)
    }

    private fun hideLoading() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }

}