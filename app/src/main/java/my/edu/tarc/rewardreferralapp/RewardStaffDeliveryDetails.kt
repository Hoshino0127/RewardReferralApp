package my.edu.tarc.rewardreferralapp

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.database.*
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.data.RefferalReward
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.data.RewardDelivery
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardStaffDeliveryDetailsBinding


class RewardStaffDeliveryDetails : Fragment() {


    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private val deliveryRef = database.getReference("RewardDelivery")
    private val rewardRef = database.getReference("Reward")
    private val refRewRef = database.getReference("RefferalReward")
    private val refRef = database.getReference("Referral")
    private var deliveryID: String = ""
    private var refferalID: String = ""

    private var refferalRewardList: ArrayList<RefferalReward> = ArrayList<RefferalReward>()
    private var rewardList: ArrayList<Reward> = ArrayList<Reward>()
    private var rewardDelivery: RewardDelivery = RewardDelivery()
    private var refferal: Referral = Referral()
    private lateinit var binding: FragmentRewardStaffDeliveryDetailsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val args = RewardStaffDeliveryDetailsArgs.fromBundle(requireArguments())
        deliveryID = args.deliveryID

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_reward_staff_delivery_details,
            container,
            false
        )


        getDetails()

        binding.btnRSDBack.setOnClickListener {
            val action = RewardStaffDeliveryDetailsDirections.actionRewardStaffDeliveryDetailsToRewardStaffDeliveryListFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.btnRSDComplete.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirm update")
            builder.setMessage("Are you the reward is shipped. You won't able to update the status again.")

            builder.setPositiveButton("Yes") { dialogInterface, which ->
                updateDelivery()
            }
            builder.setNegativeButton("No") { dialogInterface, which ->
                dialogInterface.dismiss()
            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        return binding.root
    }

    private fun getDetails() {

        val qryDelivery: Query = deliveryRef.orderByChild("deliveryID").equalTo(deliveryID)

        qryDelivery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (deliverysnap in snapshot.children) {
                        rewardDelivery = deliverysnap.getValue(RewardDelivery::class.java)!!
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })

        var qryRefRew: Query =
            refRewRef.orderByChild("deliveryID").equalTo(deliveryID)
        qryRefRew.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (refrewsnap in snapshot.children) {

                        refferalID = refrewsnap.child("refferalID").getValue().toString()
                        refferalRewardList.add(refrewsnap.getValue(RefferalReward::class.java)!!)

                    }

                    var qryRef: Query = refRef.orderByChild("referralUID").equalTo(refferalID)
                    qryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (refsnap in snapshot.children) {
                                    refferal = refsnap.getValue(Referral::class.java)!!
                                }

                                var referralText: String = ""
                                referralText += "${refferal.fullName},\n"
                                referralText += "${refferal.contactNo},\n"
                                referralText += "${refferal.email},\n"
                                binding.tvRSDRefDetail.text = referralText
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })

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
                    showText()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun showText() {
        var rewardListText: String = ""
        var addressText: String = ""


        for (item in rewardList) {
            rewardListText += "${item.rewardName}\n"
        }

        addressText += "${rewardDelivery.address1},\n"

        if (rewardDelivery.address2 != "") {
            addressText += "${rewardDelivery.address2},\n"
        }

        if (rewardDelivery.address3 != "") {
            addressText += "${rewardDelivery.address3},\n"
        }

        addressText += "${rewardDelivery.city},\n"
        addressText += "${rewardDelivery.state},\n"
        addressText += "${rewardDelivery.postCode}\n"




        if (rewardDelivery.status == "Pending") {
            binding.tvRSDShowStatus.text = getString(R.string.StaffDeliveryPending)
        } else {
            binding.tvRSDShowStatus.text = getString(R.string.StaffDeliveryCompleted)
        }


        binding.tvRSDRewardText.text = rewardListText
        binding.tvRSDAddressDetails.text = addressText


    }

    private fun updateDelivery(){

        val upDelivery = mapOf<String, Any?>(
            "status" to "Completed"
        )

        deliveryRef.child(deliveryID).updateChildren(upDelivery).addOnSuccessListener(){
            Toast.makeText(context, "Delivery updated successful", Toast.LENGTH_LONG).show()
            val action = RewardStaffDeliveryDetailsDirections.actionRewardStaffDeliveryDetailsToRewardStaffDeliveryListFragment()
            Navigation.findNavController(requireView()).navigate(action)

        }.addOnFailureListener {
            Toast.makeText(context, "Delivery updated fail", Toast.LENGTH_LONG).show()
        }


    }

}