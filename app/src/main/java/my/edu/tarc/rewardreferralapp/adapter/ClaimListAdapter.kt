package my.edu.tarc.rewardreferralapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.ClaimListingFragmentDirections
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.ReferralInsuranceListingFragmentDirections
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
        val claimID: TextView = binding.tvClaimIDCardview
        val insuranceName: TextView = binding.tvInsuranceNameCardview
        val insuranceApplyDate: TextView = binding.tvApplyDateCardview
        val claimStatus: TextView = binding.tvClaimStatusCardview
        val btnView: Button = binding.btnViewCardview
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
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        holder.claimID.text = currentClaim.claimID
        holder.insuranceComp.text = currentInsurance.insuranceComp
        holder.insuranceName.text = currentInsurance.insuranceName
        val dateStr: String = "Apply at : ${dateFormat.format(currentClaim.applyDateTime!!)}"
        holder.insuranceApplyDate.text = dateStr
        holder.claimStatus.text = currentClaim.claimStatus
        holder.bind(currentClaim!!,clickListener)
        if(currentClaim.claimStatus.equals("Pending")){
           holder.claimStatus.setTextColor(Color.parseColor("#EC512B"))
        }else if(currentClaim.claimStatus.equals("Accepted")){
            holder.claimStatus.setTextColor(Color.parseColor("#31B12C"))
        }else if(currentClaim.claimStatus.equals("Rejected")){
            holder.claimStatus.setTextColor(Color.parseColor("#F30E15"))
        }else{
            holder.claimStatus.setTextColor(Color.parseColor("#000000"))
        }
        println("From adapter: successfully bind claimID ${currentClaim.claimID}")
    }

    override fun getItemCount(): Int {
        return claimList.size
    }

    class ViewListener(val clickListener:(claimID: String) -> Unit){
        fun onClick(claim: Claim) = clickListener(claim.claimID!!)
    }


}