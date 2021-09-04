package my.edu.tarc.rewardreferralapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.Claim
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.databinding.ClaimItemBinding
import java.text.SimpleDateFormat

class ClaimListAdapter(val claimList: List<Claim>, val insuranceList: List<Insurance>, val clickListener: ViewListener):RecyclerView.Adapter<ClaimListAdapter.ViewHolder>() {
    class ViewHolder private constructor(val binding: ClaimItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: Claim, clickListener: ViewListener){
            binding.claim = item
            binding.executePendingBindings()
            binding.clickListener = clickListener
        }

        companion object{
            fun from(parent: ViewGroup):ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ClaimItemBinding.inflate(layoutInflater,parent,false)
                return ViewHolder(binding)
            }
        }

        val insuranceComp: TextView = binding.tvInsuranceCompCardview
        val insuranceID: TextView = binding.tvInsuranceIDCardview
        val insuranceName: TextView = binding.tvInsuranceNameCardview
        val insuranceApplyDate: TextView = binding.tvApplyDateCardview
        val claimStatus: TextView = binding.tvClaimStatusCardview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.claim_item,parent,false
        )
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ClaimListAdapter.ViewHolder, position: Int) {
        val currentClaim = claimList[position]
        var currentInsurance: Insurance = Insurance()
        for (insurance in insuranceList){
            if(insurance.insuranceID.equals(currentClaim.insuranceID)){
                currentInsurance = insurance
            }
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        holder.insuranceID.text = currentInsurance.insuranceID
        holder.insuranceComp.text = currentInsurance.insuranceComp
        holder.insuranceName.text = currentInsurance.insuranceName
        holder.insuranceApplyDate.text = dateFormat.format(currentClaim.accidentDateTime)
        holder.claimStatus.text = currentClaim.claimStatus
    }

    override fun getItemCount(): Int {
        return claimList.size
    }

    class ViewListener(val clickListener: (claimID: String) -> Unit){
        fun onClick(claim: Claim) = clickListener(claim.claimID!!)
    }

}