package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.*
import my.edu.tarc.rewardreferralapp.data.RefferalReward
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.data.RewardDelivery
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardRedeemSuccessBinding

class RewardRedeemSuccessFragment : Fragment() {

    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private val deliveryRef = database.getReference("RewardDelivery")
    val rewardRef = database.getReference("Reward")
    val refRewRef = database.getReference("RefferalReward")

    private lateinit var deliveryID: String
    private lateinit var binding: FragmentRewardRedeemSuccessBinding

    private var refferalRewardList: ArrayList<RefferalReward> = ArrayList<RefferalReward>()
    private var rewardList: ArrayList<Reward> = ArrayList<Reward>()
    private var rewardDelivery: RewardDelivery = RewardDelivery()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val args = RewardRedeemSuccessFragmentArgs.fromBundle(requireArguments())
        deliveryID = args.deliveryID

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_reward_redeem_success,
            container,
            false
        )

        getDeliveryDetail()

        binding.btnRRSReturnHomepage.setOnClickListener {
            //return home page
        }





        return binding.root
    }

    private fun getDeliveryDetail() {


        val qryDelivery: Query = deliveryRef.orderByChild("deliveryID").equalTo(deliveryID)

        qryDelivery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (deliverysnap in snapshot.children) {
                    rewardDelivery = deliverysnap.getValue(RewardDelivery::class.java)!!
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

                        refferalRewardList.add(refrewsnap.getValue(RefferalReward::class.java)!!)

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

        if(rewardDelivery.status == "Pending"){
            binding.tvRRSShowDetail.text = getString(R.string.DeliveryPending)
        }else{
            binding.tvRRSShowDetail.text = getString(R.string.DeliveryOTW)
        }



        binding.tvRRSRewardText.text = rewardListText
        binding.tvRRSAddressDetails.text = addressText


    }

}