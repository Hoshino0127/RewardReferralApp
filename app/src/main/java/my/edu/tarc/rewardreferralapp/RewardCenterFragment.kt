package my.edu.tarc.rewardreferralapp

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.*
import my.edu.tarc.rewardreferralapp.adapter.RewardCenterAdapter
import my.edu.tarc.rewardreferralapp.data.RefferalReward
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardCenterBinding


class RewardCenterFragment : Fragment() {

    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val rewardRef = database.getReference("Reward")
    val refRewRef = database.getReference("RefferalReward")
    var rewardList = ArrayList<Reward>()
    val referralID = "R0001"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentRewardCenterBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_reward_center, container, false)


        //set point
        binding.tvRCMypoint.text = "300"

        val RewardAdapter = RewardCenterAdapter(
            rewardList,
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

                                InsertRefferalReward(referralID, rewardID, "Pending")

                                val upReward = mapOf<String, Any?>(
                                    "stock" to Stock - 1
                                )

                                //update member point

                                rewardRef.child(rewardID).updateChildren(upReward)
                                    .addOnSuccessListener {

                                        Toast.makeText(
                                            context,
                                            "Redeemed Successful",
                                            Toast.LENGTH_LONG
                                        ).show()


//                                        var frg: Fragment? =
//                                            fragmentManager?.findFragmentByTag("RewardCenterFragment")
//                                        val ft: FragmentTransaction? =
//                                            fragmentManager?.beginTransaction()
//                                        if (ft != null) {
//                                            ft.detach(frg!!)
//                                            ft.attach(frg!!)
//                                            ft.commit()
//                                        }


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

        //Search button function
        binding.btnRCSearch.setOnClickListener() {

            //press button clear keyboard and input
            val iMm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            iMm.hideSoftInputFromWindow(view?.windowToken, 0)
            view?.clearFocus()

            var qry: Query = rewardRef.orderByChild("rewardName")
                .startAt(binding.ptRCRewardName.text.toString())
                .endAt(binding.ptRCRewardName.text.toString() + "\uf8ff")

            qry.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    rewardList.clear()
                    if (snapshot.exists()) {
                        for (rewardSnapshot in snapshot.children) {
                            rewardList.add(rewardSnapshot.getValue(Reward::class.java)!!)

                        }
                        binding.RewardCenterRV.adapter?.notifyDataSetChanged()
                    } else {
                        Toast.makeText(context, "Reward Not Found", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                }
            })

        }

        //add to recycle view
        rewardRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rewardList.clear()
                if (snapshot.exists()) {

                    for (rewardSnapshot in snapshot.children) {
                        rewardList.add(rewardSnapshot.getValue(Reward::class.java)!!)

                    }
                    binding.RewardCenterRV.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }
        })


        return binding.root
    }

    private fun InsertRefferalReward(refferalID:String,rewardID:String,status:String) {

        var newID: String = ""

        refRewRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    newID = "RR" + "%04d".format(snapshot.childrenCount + 1)

                } else {

                    newID = "RR0001"

                }

                val rr = RefferalReward(newID,refferalID,rewardID,status)

                refRewRef.child(newID).setValue(rr).addOnSuccessListener(){
                    Toast.makeText(context, "Redeemed successful", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    Toast.makeText(context, "Redeemed unsuccessful", Toast.LENGTH_LONG).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })

    }


}