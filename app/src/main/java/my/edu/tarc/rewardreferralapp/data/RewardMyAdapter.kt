package my.edu.tarc.rewardreferralapp.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.databinding.RewardcenterListItemBinding

class RewardMyAdapter(val rewardList: List<Reward>, val clickListener: ProceedListener) :
    RecyclerView.Adapter<RewardMyAdapter.ViewHolder>() {

    class ViewHolder private constructor(val binding: RewardcenterListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Reward, clickListener: ProceedListener) {
            binding.reward = item
            binding.executePendingBindings()
            binding.proceedListener = clickListener
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
        val btnClaim: Button = binding.btnClaim
        val btnView: Button = binding.btnView
        val btnProceed: Button = binding.btnProceed

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.rewardcenter_list_item, parent, false
        )

        return RewardMyAdapter.ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentReward = rewardList[position]
        holder.RewardName.text = currentReward.rewardName
        holder.RewardDesc.text = currentReward.rewardDesc
        holder.AvailableDate.visibility = View.GONE
        holder.pointNeeded.visibility = View.GONE
        holder.Stock.visibility = View.GONE
        holder.btnClaim.visibility = View.GONE
        holder.btnView.visibility = View.GONE
        holder.btnProceed.visibility = View.VISIBLE
        holder.bind(currentReward!!, clickListener)
    }

    override fun getItemCount(): Int {
        return rewardList.size
    }

    class ProceedListener(val clickListener: (RewardID: String, RewardName: String) -> Unit) {
        fun onClick(reward: Reward) =
            clickListener(reward.rewardID!!, reward.rewardName!!)
    }
}