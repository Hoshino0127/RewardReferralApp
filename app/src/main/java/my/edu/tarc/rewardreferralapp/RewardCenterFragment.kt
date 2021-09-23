package my.edu.tarc.rewardreferralapp

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.*
import my.edu.tarc.rewardreferralapp.adapter.RewardCenterAdapter
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.data.RefferalReward
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardCenterBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import my.edu.tarc.rewardreferralapp.helper.MyLottie
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RewardCenterFragment : Fragment() {

    private val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val rewardRef = database.getReference("Reward")
    private val refRewRef = database.getReference("RefferalReward")
    private val refRef = database.getReference("Referral")
    private var rewardList = ArrayList<Reward>()
    private var rewardSearchList = ArrayList<Reward>()
    private var refferal: Referral = Referral()
    private lateinit var binding: FragmentRewardCenterBinding
    private val referralID = CheckUser().getCurrentUserUID()

    private var loadingDialog: Dialog?= null
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rewardSearchList.clear()
        rewardList.clear()

        showLoading()
        getDetails()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val action = RewardCenterFragmentDirections.actionRewardCenterFragmentToUserProfileFragment()
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_reward_center, container, false)


        binding.btnBackRewardCenter.setOnClickListener(){
            val action = RewardCenterFragmentDirections.actionRewardCenterFragmentToUserProfileFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.btnRCSearch.setOnClickListener() {

            //press button clear keyboard and input
            val iMm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            iMm.hideSoftInputFromWindow(view?.windowToken, 0)
            view?.clearFocus()

            //search reward
            rewardSearchList =
                rewardList.filter { r ->
                    r.rewardName!!.uppercase()
                        .contains(binding.ptRCRewardName.text.toString().uppercase())
                } as ArrayList<Reward>

            setAdapter(rewardSearchList)

        }


        return binding.root
    }

    private fun getDetails() {

        var qryRef: Query = refRef.orderByChild("referralUID").equalTo(referralID)

        qryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (refSnap in snapshot.children) {
                        refferal = refSnap.getValue(Referral::class.java)!!
                    }
                    binding.tvRCMypoint.text = refferal.points.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })


        var todayDate: Date
        var startDate: Date
        var endDate: Date
        val format = SimpleDateFormat("dd/MM/yyyy")

        todayDate = format.parse(format.format(Date()))

        //get reward
        var qryReward: Query = rewardRef.orderByChild("status").equalTo("Active")

        qryReward.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rewardList.clear()
                if (snapshot.exists()) {

                    for (rewardSnapshot in snapshot.children) {

                        startDate =
                            format.parse(rewardSnapshot.child("startDate").getValue().toString())
                        endDate =
                            format.parse(rewardSnapshot.child("endDate").getValue().toString())

                        if (todayDate >= startDate && todayDate <= endDate) {
                            rewardList.add(rewardSnapshot.getValue(Reward::class.java)!!)
                        }
                    }

                    setAdapter(rewardList)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun InsertRefferalReward(refferalID: String, rewardID: String, status: String) {

        var newID: String = UUID.randomUUID().toString()

        val rr = RefferalReward(newID, refferalID, rewardID, status, "")

        refRewRef.child(newID).setValue(rr).addOnSuccessListener() {
            Toast.makeText(context, "Redeemed successful", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Redeemed unsuccessful", Toast.LENGTH_LONG).show()
        }


    }

    private fun setAdapter(rewardAdapterList: ArrayList<Reward>) {
        val RewardAdapter = RewardCenterAdapter(
            rewardAdapterList,
            RewardCenterAdapter.ClaimListener { rewardID, rewardName, PointNeeded, Stock ->
                val it = view
                if (it != null) {

                    //set Dialog
                    val builder = AlertDialog.Builder(activity)

                    if (Stock != 0) {

                        builder.setTitle("Confirm Claim")
                        builder.setMessage("Are you sure want to claim the reward - $rewardName with $PointNeeded point")

                        builder.setPositiveButton("Yes") { dialogInterface, which ->


                            val currentPoint = Integer.valueOf(binding.tvRCMypoint.text.toString())

                            if (currentPoint >= PointNeeded) {

                                InsertRefferalReward(referralID!!, rewardID, "Pending")

                                val upRefPoint = mapOf<String, Any?>(
                                    "points" to currentPoint - PointNeeded
                                )


                                val upReward = mapOf<String, Any?>(
                                    "stock" to Stock - 1
                                )

                                rewardRef.child(rewardID).updateChildren(upReward)
                                    .addOnSuccessListener {
                                        refRef.child(refferal.referralUID.toString())
                                            .updateChildren(upRefPoint)
                                            .addOnSuccessListener() {

                                                Toast.makeText(
                                                    context,
                                                    "Redeemed Successful",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                val action =
                                                    RewardCenterFragmentDirections.actionRewardCenterFragmentToRewardMyFragment()
                                                Navigation.findNavController(requireView())
                                                    .navigate(action)

                                            }
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "update reward fail",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                    }


                            } else {

                                dialogInterface.dismiss()

                                Toast.makeText(
                                    context,
                                    "You have not enough point to redeem this reward",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }

                        builder.setNegativeButton("No") { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }

                    } else {

                        builder.setTitle("Out of stock")
                        builder.setMessage("This reward already out of stock")

                        builder.setNegativeButton("Back") { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }

                    }

                    val alertDialog: AlertDialog = builder.create()
                    // Set other dialog properties
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                }
            })



        binding.RewardCenterRV.adapter = RewardAdapter
        binding.RewardCenterRV.setHasFixedSize(true)

        handler.postDelayed({
            hideLoading()
        }, 200)
    }

    private fun hideLoading() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }


}