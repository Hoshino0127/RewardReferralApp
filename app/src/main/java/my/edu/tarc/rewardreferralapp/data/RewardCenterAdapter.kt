package my.edu.tarc.rewardreferralapp.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.databinding.RewardcenterListItemBinding
import java.text.SimpleDateFormat

class RewardCenterAdapter(val rewardList: List<Reward>, val clickListener: ClaimListener) :
    RecyclerView.Adapter<RewardCenterAdapter.ViewHolder>() {

    class ViewHolder private constructor(val binding: RewardcenterListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Reward, clickListener: ClaimListener) {
            binding.reward = item
            binding.executePendingBindings()
            binding.claimListener = clickListener
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
        val btnView: Button = binding.btnView
        val btnProceed:Button = binding.btnProceed


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.rewardcenter_list_item, parent, false
        )

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentReward = rewardList[position]
        holder.RewardName.text = currentReward.RewardName
        holder.RewardDesc.text = currentReward.RewardDesc
        holder.AvailableDate.text = "Available Before: " + currentReward.EndDate
        holder.pointNeeded.text = "Point Needed: " + currentReward.PointNeeded.toString()
        holder.Stock.text = "Stock: " + currentReward.Stock.toString()
        holder.btnView.visibility = View.GONE
        holder.btnProceed.visibility = View.GONE
        holder.bind(currentReward!!, clickListener)

    }

    override fun getItemCount(): Int {
        return rewardList.size
    }

    class ClaimListener(val clickListener: (RewardID: String, RewardName: String, PointNeeded: Int,Stock:Int) -> Unit) {
        fun onClick(reward: Reward) =
            clickListener(reward.RewardID, reward.RewardName, reward.PointNeeded,reward.Stock)
    }
}