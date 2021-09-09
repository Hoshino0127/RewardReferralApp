package my.edu.tarc.rewardreferralapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.databinding.RewardcenterListItemBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser

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
        val rewardcheck: CheckBox = binding.chkReward
        val rewardImg: ImageView = binding.imgRewardIcon

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
        holder.ViewButton.visibility = View.VISIBLE
        holder.rewardcheck.visibility = View.GONE
        holder.bind(currentReward!!, clickListener)

        var Imgref: StorageReference =
            FirebaseStorage.getInstance().getReference("RewardStorage")
                .child(currentReward.rewardImg.toString())

        Imgref.downloadUrl.addOnSuccessListener() {
            Glide
                .with(holder.rewardImg.context)
                .load(it.toString())
                .into(holder.rewardImg)
        }

    }

    override fun getItemCount(): Int {
        return rewardList.size
    }

    class ViewListener(val clickListener: (RewardID: String) -> Unit) {
        fun onClick(reward: Reward) =
            clickListener(reward.rewardID!!)
    }
}