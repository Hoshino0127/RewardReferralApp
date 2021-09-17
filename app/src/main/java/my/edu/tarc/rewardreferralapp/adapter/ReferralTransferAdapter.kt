package my.edu.tarc.rewardreferralapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.data.ReferralTransfer
import my.edu.tarc.rewardreferralapp.databinding.ReferraltransferListItemBinding


class ReferralTransferAdapter(
    val refUID: String,
    val refTransList: List<ReferralTransfer>,
    val refList: List<Referral>
) :
    RecyclerView.Adapter<ReferralTransferAdapter.ViewHolder>() {

    private var refFilterList: ArrayList<Referral> = ArrayList<Referral>()
    private var referral: Referral = Referral()

    class ViewHolder private constructor(val binding: ReferraltransferListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ReferraltransferListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        val transferDetails = binding.tvTransferDetails
        val pointTransfer = binding.tvPointTransfer
        val transferDate = binding.tvTransferDate


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.referraltransfer_list_item, parent, false
        )

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        refFilterList.clear()
        val currRefTransfer = refTransList[position]

        if (refUID == currRefTransfer.referralDonorID) {
            refFilterList =
                refList.filter { r -> r.referralUID == currRefTransfer.referralRecipientID } as ArrayList<Referral>
            referral = refFilterList[0]
            holder.transferDetails.text = "Transfer to ${referral.fullName}"
            holder.pointTransfer.text = "-${currRefTransfer.pointTransfer} points"
            holder.pointTransfer.setTextColor(Color.parseColor("#F30E15"))

        } else {
            refFilterList =
                refList.filter { r -> r.referralUID == currRefTransfer.referralDonorID } as ArrayList<Referral>
            referral = refFilterList[0]
            holder.transferDetails.text = "Received from ${referral.fullName}"
            holder.pointTransfer.text = "+${currRefTransfer.pointTransfer} points"
            holder.pointTransfer.setTextColor(Color.parseColor("#31B12C"))
        }

        holder.transferDate.text = currRefTransfer.transferDate
    }

    override fun getItemCount(): Int {
        return refTransList.size
    }
}