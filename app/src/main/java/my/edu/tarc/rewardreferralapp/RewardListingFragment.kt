package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.adapter.RewardListAdapter
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardListingBinding

class RewardListingFragment : Fragment() {

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

        val binding: FragmentRewardListingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reward_listing, container, false)

        val rewardAdapter = RewardListAdapter(rewardList, RewardListAdapter.ViewListener{
            rewardID -> val it = view
            if (it != null) {
                Toast.makeText(context, "$rewardID list", Toast.LENGTH_LONG).show()
            }
        })
        binding.RewardListRV.adapter = rewardAdapter
        binding.RewardListRV.setHasFixedSize(true)

        return binding.root

    }

}