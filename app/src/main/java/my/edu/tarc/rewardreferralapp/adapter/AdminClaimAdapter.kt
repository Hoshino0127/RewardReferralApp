package my.edu.tarc.rewardreferralapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.Claim
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.data.Referral

class AdminClaimAdapter (internal var context: Context, private var claimList:MutableList<Claim>, private var referralList:MutableList<Referral>, private var insuranceList:MutableList<Insurance>) :
    RecyclerView.Adapter<AdminClaimAdapter.myViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.admin_claim_item_layout, parent, false)
        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = claimList[position]
        var currentReferral: Referral = Referral()
        for(ref in referralList){
            if(ref.referralUID!!.equals(currentItem.referralUID)){
                currentReferral = ref
            }
        }

        var currentInsurance: Insurance = Insurance()
        for(ins in insuranceList){
            if(ins.insuranceID!!.equals(currentItem.insuranceID)){
                currentInsurance = ins
            }
        }

        holder.claimID.text = currentItem.claimID
        holder.insuranceName.text = currentInsurance.insuranceName
        holder.claimStatus.text = currentItem.claimStatus
        val referralDetailsStr: String = "Claim by : ${currentReferral.fullName}"
        holder.referralDetails.text =  referralDetailsStr
        when {
            currentItem.claimStatus.equals("Pending") -> {
                holder.claimStatus.setTextColor(Color.parseColor("#EC512B"))
            }
            currentItem.claimStatus.equals("Accepted") -> {
                holder.claimStatus.setTextColor(Color.parseColor("#31B12C"))
            }
            currentItem.claimStatus.equals("Rejected") -> {
                holder.claimStatus.setTextColor(Color.parseColor("#F30E15"))
            }
            else -> {
                holder.claimStatus.setTextColor(Color.parseColor("#000000"))
            }
        }
    }

    override fun getItemCount(): Int {
        return claimList.size
    }

    class myViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val claimID: TextView = itemView.findViewById(R.id.tvClaimID_AdminClaim)
        val insuranceName: TextView = itemView.findViewById(R.id.tvInsuranceName_AdminClaim)
        val claimStatus: TextView = itemView.findViewById(R.id.tvClaimStatus_AdminClaim)
        val referralDetails: TextView = itemView.findViewById(R.id.tvReferralDetails_AdminClaim)
    }
}