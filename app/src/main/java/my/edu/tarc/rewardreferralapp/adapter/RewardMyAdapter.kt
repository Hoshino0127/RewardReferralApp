package my.edu.tarc.rewardreferralapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.RefferalReward
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.databinding.RewardcenterListItemBinding


class RewardMyAdapter(val rewardList: List<Reward>,val refrewList:List<RefferalReward>) :
    RecyclerView.Adapter<RewardMyAdapter.ViewHolder>() {



    private var chekedList: ArrayList<RefferalReward> = ArrayList<RefferalReward>()

    class ViewHolder private constructor(val binding: RewardcenterListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

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
        val chkReward:CheckBox = binding.chkReward

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.rewardcenter_list_item, parent, false
        )

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentReward = rewardList[position]
        val currRefRew = refrewList[position]
        holder.RewardName.text = currentReward.rewardName
        holder.RewardDesc.text = currentReward.rewardDesc
        holder.AvailableDate.visibility = View.GONE
        holder.pointNeeded.visibility = View.GONE
        holder.Stock.visibility = View.GONE
        holder.btnClaim.visibility = View.GONE
        holder.btnView.visibility = View.GONE
        holder.chkReward.visibility = View.VISIBLE

        holder.chkReward.tag = position

        holder.chkReward.setOnClickListener{
            val chkpostition = holder.chkReward.tag as Int

            if(holder.chkReward.isChecked){
                chekedList.add(refrewList[chkpostition])
            }else{
                chekedList.remove(refrewList[chkpostition])
            }
        }
    }

    fun getchekList() = chekedList

    override fun getItemCount(): Int {
        return rewardList.size
    }
}