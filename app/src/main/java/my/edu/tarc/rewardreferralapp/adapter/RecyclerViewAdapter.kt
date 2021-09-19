package my.edu.tarc.rewardreferralapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.data.ReferralInsurance
import my.edu.tarc.rewardreferralapp.databinding.InsuranceItemBinding
import java.text.SimpleDateFormat
import java.util.*


class RecyclerViewAdapter(
    val insuranceList: List<Insurance>,
    val insuranceReferralList: List<ReferralInsurance>,
    val clickListener: ClaimListener,
    val cancelListener: CancelListener
    ): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    class ViewHolder private constructor(val binding: InsuranceItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Insurance, item2: ReferralInsurance, clickListener: ClaimListener, cancelListener: CancelListener) {
            binding.insurance = item
            binding.insuranceReferralID = item2.insuranceReferralID
            binding.executePendingBindings()
            binding.clickListener = clickListener
            binding.cancelListener = cancelListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = InsuranceItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        val insuranceType: TextView = binding.tvInsuranceTypeCardview
        val insuranceComp: TextView = binding.tvInsuranceCompCardview
        val insuranceName: TextView = binding.tvInsuranceNameCardview
        val insurancePlan: TextView = binding.tvInsurancePlanCardview
        //val insuranceExpiryDate: TextView = binding.tvInsuranceExpiryDateCardview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.insurance_item,parent,false
        )

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentInsurance = insuranceList[position]
        var currentRefIns: ReferralInsurance = ReferralInsurance()
        for(refIns in insuranceReferralList){
            if(refIns.insuranceID.equals(currentInsurance.insuranceID)){
                currentRefIns = refIns
            }
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val strComp: String = "Company: \n${currentInsurance.insuranceComp}"
        val strPlan: String = "Plan: \n${currentInsurance.insurancePlan}"
        val strType: String = "Type: \n${currentInsurance.insuranceType}"
        holder.insuranceName.text = currentInsurance.insuranceName
        holder.insuranceComp.text = strComp
        holder.insuranceType.text = strType
        holder.insurancePlan.text = strPlan
        //holder.insuranceExpiryDate.text = dateFormat.format(currentInsurance.insuranceExpiryDate)
        holder.bind(currentInsurance!!,currentRefIns, clickListener, cancelListener)
        // bind image into the image view
        //holder.imgInsuranceIcon.setImageResource(currentInsurance.img)
    }

    override fun getItemCount(): Int {
        return insuranceList.size
    }

    class ClaimListener(val clickListener: (insuranceID: String, insuranceReferralID: String) -> Unit) {
        fun onClick(insurance: Insurance, insuranceReferralID: String) = clickListener(insurance.insuranceID!!, insuranceReferralID)
    }

    class CancelListener(val clickListener: (insuranceReferralID: String) -> Unit){
        fun onClick(insuranceReferralID: String) = clickListener(insuranceReferralID)
    }


}