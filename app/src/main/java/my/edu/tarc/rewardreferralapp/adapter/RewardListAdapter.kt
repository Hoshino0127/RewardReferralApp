package my.edu.tarc.rewardreferralapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.databinding.RewardcenterListItemBinding

class RewardListAdapter(val rewardList: List<Reward>, val clickListener: ViewListener) :
    RecyclerView.Adapter<RewardListAdapter.ViewHolder>() {


    class ViewHolder private constructor(val binding: RewardcenterListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Reward, clickListener: ViewListener) {
            binding.reward = item
            binding.executePendingBindings()
            binding.viewListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RewardcenterListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        val RewardName: TextView = binding.tvRewardName
        val RewardDesc: TextView = binding.tvRewardDesc
        val AvailableDate: TextView = binding.tvAvailableDate
        val pointNeeded: TextView = binding.tvPointNeeded
        val Stock: TextView = binding.tvStock
        val ClaimButton: Button = binding.btnClaim
        val ViewButton: Button = binding.btnView
        val proceedButton: Button = binding.btnProceed

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.rewardcenter_list_item, parent, false
        )

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentReward = rewardList[position]
        holder.RewardName.text = currentReward.rewardName
        holder.RewardDesc.text = currentReward.rewardDesc
        holder.AvailableDate.text = "Available Before: " + currentReward.endDate
        holder.pointNeeded.text = "Point Needed: " + currentReward.pointNeeded.toString()
        holder.Stock.text = "Stock: " + currentReward.stock.toString()
        holder.ClaimButton.visibility = View.GONE
        holder.proceedButton.visibility = View.GONE
        holder.ViewButton.visibility = View.VISIBLE
        holder.bind(currentReward!!, clickListener)
    }

    override fun getItemCount(): Int {
        return rewardList.size
    }

    class ViewListener(val clickListener: (RewardID: String) -> Unit) {
        fun onClick(reward: Reward) =
            clickListener(reward.rewardID!!)
    }
}