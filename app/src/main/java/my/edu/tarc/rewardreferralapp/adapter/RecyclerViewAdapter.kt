package my.edu.tarc.rewardreferralapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.databinding.InsuranceItemBinding
import java.text.SimpleDateFormat


class RecyclerViewAdapter(val insuranceList: List<Insurance>,val clickListener: ClaimListener): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    class ViewHolder private constructor(val binding: InsuranceItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Insurance, clickListener: ClaimListener) {
            binding.insurance = item
            binding.executePendingBindings()
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = InsuranceItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        val insuranceID: TextView = binding.tvInsuranceIDCardview
        val insuranceComp: TextView = binding.tvInsuranceCompCardview
        val insuranceName: TextView = binding.tvInsuranceNameCardview
        val insurancePlan: TextView = binding.tvInsurancePlanCardview
        val insuranceExpiryDate: TextView = binding.tvInsuranceExpiryDateCardview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.insurance_item,parent,false
        )

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentInsurance = insuranceList[position]
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        holder.insuranceName.text = currentInsurance.insuranceName
        holder.insuranceComp.text = currentInsurance.insuranceComp
        holder.insuranceID.text = currentInsurance.insuranceID
        holder.insurancePlan.text = currentInsurance.insurancePlan
        holder.insuranceExpiryDate.text = dateFormat.format(currentInsurance.insuranceExpiryDate)
        holder.bind(currentInsurance!!, clickListener)
        // bind image into the image view
        //holder.imgInsuranceIcon.setImageResource(currentInsurance.img)
    }

    override fun getItemCount(): Int {
        return insuranceList.size
    }

    class ClaimListener(val clickListener: (insuranceID: String) -> Unit) {
        fun onClick(insurance: Insurance) = clickListener(insurance.insuranceID!!)
    }


}