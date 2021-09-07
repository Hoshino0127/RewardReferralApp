package my.edu.tarc.rewardreferralapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.data.RewardDelivery
import my.edu.tarc.rewardreferralapp.databinding.RewarddeliveryListItemBinding

class RewardDeliveryAdapter(val deliveryList: List<RewardDelivery>,val clickListener:CheckListener) :
    RecyclerView.Adapter<RewardDeliveryAdapter.ViewHolder>() {

    class ViewHolder private constructor(val binding:RewarddeliveryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RewardDelivery,clickListener:CheckListener) {
            binding.rewardDelivery = item
            binding.executePendingBindings()
            binding.checkListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RewarddeliveryListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        val deliveryID = binding.tvDeliveryID
        val applyDate = binding.tvDeliveryDate
        val deliveryStatus = binding.tvDeliveryStatus
        val btnCheck = binding.btnDeliveryCheck

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.rewarddelivery_list_item, parent, false
        )

        return ViewHolder.from(parent)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentDelivery = deliveryList[position]
        holder.deliveryID.text = "Delivery ID: "+currentDelivery.deliveryID
        holder.applyDate.text = "Apply Date: "+currentDelivery.applyDate

        if(currentDelivery.status == "Pending"){
            holder.deliveryID.text = "Pending to ship"
        }else{
            holder.deliveryID.text = "Shipped"
        }
        holder.bind(currentDelivery!!,clickListener)

    }


    override fun getItemCount(): Int {
        return deliveryList.size
    }

    class CheckListener(val clickListener: (DeliveryID: String) -> Unit) {
        fun onClick(rewardDelivery: RewardDelivery) =
            clickListener(rewardDelivery.deliveryID!!)
    }




}