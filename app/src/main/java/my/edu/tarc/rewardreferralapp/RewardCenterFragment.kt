package my.edu.tarc.rewardreferralapp

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.adapter.RewardCenterAdapter
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardCenterBinding

class RewardCenterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rewardList : List <Reward> = listOf(
        Reward("RW0001","MyBee Umbrella","Umbrella from MyBee Company",500,
            "22/08/2021","22/08/2022",50),

            Reward("RW0002","MyBee Bottle","Bottle from MyBee Company",200,
                "22/08/2021","26/08/2022",10),

            Reward("RW0003","MyBee Wallet","Wallet from MyBee Company",300,
                "22/08/2021","29/08/2022",0)
        )

        val binding:FragmentRewardCenterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reward_center, container, false)

        val RewardAdapter = RewardCenterAdapter(rewardList, RewardCenterAdapter.ClaimListener{
            rewardID,rewardName,PointNeeded,Stock -> val it = view
            if (it != null) {
                val builder = AlertDialog.Builder(activity)


                if(Stock != 0) {

                    builder.setTitle("Confirm Claim")
                    builder.setMessage("Are you sure want to claim the reward $rewardName $rewardID $PointNeeded $Stock")

                    builder.setPositiveButton("Yes") { dialogInterface, which ->
                        Toast.makeText(context, "clicked yes", Toast.LENGTH_LONG).show()
                    }

                    builder.setNegativeButton("No") { dialogInterface, which ->
                        dialogInterface.dismiss()
                    }

                }else{

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

        binding.btnRCSearch.setOnClickListener(){

        }


        return binding.root
    }

}