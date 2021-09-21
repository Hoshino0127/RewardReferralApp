package my.edu.tarc.rewardreferralapp.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.data.ReferralInsurance
import my.edu.tarc.rewardreferralapp.databinding.InsuranceItemBinding
import java.text.SimpleDateFormat
import java.util.*


class RecyclerViewAdapter(
    val insuranceList: List<Insurance>,
    private val insuranceReferralList: List<ReferralInsurance>,
    val clickListener: ClaimListener,
    private val cancelListener: CancelListener,
    private val renewListener: RenewListener,
    ): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    class ViewHolder private constructor(val binding: InsuranceItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Insurance, item2: ReferralInsurance, clickListener: ClaimListener, cancelListener: CancelListener, renewListener: RenewListener) {
            binding.insurance = item
            binding.insuranceReferralID = item2.insuranceReferralID
            binding.executePendingBindings()
            binding.clickListener = clickListener
            binding.cancelListener = cancelListener
            binding.renewListener = renewListener
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
        val insuranceExpiryDate: TextView = binding.tvInsuranceExpiryDateCardview
        val imgInsuranceIcon: ImageView = binding.imgInsuranceIcon
        val btnCancel : Button = binding.btnCancelCardview
        val btnClaim: Button = binding.btnClaimCardview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.insurance_item,parent,false
        )

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy",Locale.US)

        val currentInsurance = insuranceList[position]
        var currentRefIns = ReferralInsurance()
        for(refIns in insuranceReferralList){
            if(refIns.insuranceID.equals(currentInsurance.insuranceID)){
                currentRefIns = refIns
            }
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val strComp = "Company: ${currentInsurance.insuranceComp}"
        val strPlan = "Plan: ${currentInsurance.insurancePlan}"
        val strType = "Type: ${currentInsurance.insuranceType}"
        val strExpiryDate = "Expires at: ${sdf.format(currentRefIns.insuranceExpiryDate!!)}"

        holder.insuranceName.text = currentInsurance.insuranceName
        holder.insuranceComp.text = strComp
        holder.insuranceType.text = strType
        holder.insurancePlan.text = strPlan
        holder.insuranceExpiryDate.text = strExpiryDate

        val Imgref: StorageReference =
            FirebaseStorage.getInstance().getReference("InsuranceStorage")
                .child(currentInsurance.insuranceImg.toString())

        Imgref.downloadUrl.addOnSuccessListener() {
            Glide
                .with(holder.imgInsuranceIcon.context)
                .load(it.toString())
                .into(holder.imgInsuranceIcon)
        }

        holder.bind(currentInsurance!!,currentRefIns, clickListener, cancelListener, renewListener)

        cal.time = Date()
        cal.add(Calendar.DAY_OF_MONTH,7)

        if(cal.time.after(currentRefIns.insuranceExpiryDate)){
            holder.insuranceExpiryDate.setTextColor(Color.parseColor("#ECAF2B"))
        }

        if(Date().after(currentRefIns.insuranceExpiryDate)){
            holder.btnClaim.visibility = View.GONE
            holder.insuranceExpiryDate.setTextColor(Color.parseColor("#F30E15"))
            holder.insuranceExpiryDate.setTypeface(Typeface.DEFAULT_BOLD)
        }

        if(currentRefIns.status == "Pending") {
            holder.btnCancel.visibility = View.GONE
        }
    }


    override fun getItemCount(): Int {
        return insuranceList.size
    }

    class ClaimListener(val clickListener: (insurance: String, insuranceReferralID: String) -> Unit) {
        fun onClick(insurance: Insurance, insuranceReferralID: String) = clickListener(insurance.insuranceID!!, insuranceReferralID)
    }

    class CancelListener(val clickListener: (insuranceReferralID: String) -> Unit){
        fun onClick(insuranceReferralID: String) = clickListener(insuranceReferralID)
    }

    class RenewListener(val clickListener: (insurance: String, insuranceReferralID: String) -> Unit){
        fun onClick(insurance: Insurance, insuranceReferralID: String) = clickListener(insurance.insuranceID!!, insuranceReferralID)
    }


}