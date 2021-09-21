package my.edu.tarc.rewardreferralapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.databinding.InsuranceItemCustLayoutBinding
import my.edu.tarc.rewardreferralapp.data.Insurance

class InsuranceCustAdapter (internal var insuranceList:List<Insurance>, val clickListener: ApplyListener) :
    RecyclerView.Adapter<InsuranceCustAdapter.myViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.insurance_item_cust_layout, parent, false)
        return myViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = insuranceList[position]

        holder.insuranceName.text = currentItem.insuranceName
        holder.insuranceComp.text = currentItem.insuranceComp
        holder.insurancePlan.text = currentItem.insurancePlan
        holder.insuranceType.text = currentItem.insuranceType
        var strCover : String? = ""
        val lastCover: String? = currentItem.insuranceCoverage?.last()
        for (insCover in currentItem.insuranceCoverage!!) {
            strCover += if(insCover == lastCover) {
                "$insCover"
            } else {
                "$insCover,\n"
            }
        }
        holder.insuranceCoverage.text = strCover
        holder.insurancePrice.text = currentItem.insurancePrice.toString()

        val Imgref: StorageReference =
            FirebaseStorage.getInstance().getReference("InsuranceStorage")
                .child(currentItem.insuranceImg.toString())

        Imgref.downloadUrl.addOnSuccessListener() {
            Glide
                .with(holder.imgInsurance.context)
                .load(it.toString())
                .into(holder.imgInsurance)
        }

        holder.bind(currentItem, clickListener)
    }

    override fun getItemCount(): Int {
        return insuranceList.size
    }

    class myViewHolder private constructor(val binding: InsuranceItemCustLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Insurance, clickListener: ApplyListener) {
            binding.insurance = item
            binding.executePendingBindings()
            binding.applyListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): myViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = InsuranceItemCustLayoutBinding.inflate(layoutInflater, parent, false)
                return myViewHolder(binding)
            }
        }

        val insuranceName: TextView = binding.tvCustInsuranceName
        val insuranceComp: TextView = binding.tvCustInsuranceComp
        val insurancePlan: TextView = binding.tvCustInsurancePlan
        val insuranceType: TextView = binding.tvCustInsuranceType
        val insuranceCoverage: TextView = binding.tvCustInsuranceCoverage
        val insurancePrice : TextView = binding.tvCustInsurancePrice2
        val imgInsurance: ImageView = binding.imgInsurance
    }

    class ApplyListener(val clickListener: (InsuranceID: String) -> Unit) {
        fun onClick(item: Insurance) =
            clickListener(item.insuranceID!!)
    }

}