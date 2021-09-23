package my.edu.tarc.rewardreferralapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.databinding.InsuranceCancelItemLayoutBinding
import my.edu.tarc.rewardreferralapp.data.CancelInsurance
import my.edu.tarc.rewardreferralapp.data.ReferralInsurance
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CancelInsuranceApplicationAdapter(internal var cancelInsuranceApplicationList: List<CancelInsurance>,
        val acceptListener: AcceptListener,
        val rejectListener: RejectListener
) :
    RecyclerView.Adapter<CancelInsuranceApplicationAdapter.myViewHolder>() {

    private var tempRefInsList = ArrayList<ReferralInsurance>()
    private val database = FirebaseDatabase.getInstance()
    private val referralInsRef = database.getReference("ReferralInsurance")

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.insurance_cancel_item_layout, parent, false)
        return myViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = cancelInsuranceApplicationList[position]
        val format = SimpleDateFormat("dd-MMM-yyyy")

        holder.cancelInsuranceID.text = currentItem.cancelInsuranceUID
        holder.referralInsuranceID.text = currentItem.insuranceReferralUID
        holder.appliedDate.text = format.format(currentItem.appliedDate)
        holder.cancelReason.text = currentItem.reason

        referralInsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    tempRefInsList.clear()
                    for (refInsSnapshot in snapshot.children){

                        val insuranceReferralID: String = refInsSnapshot.child("insuranceReferralID").getValue().toString()
                        val insuranceID: String = refInsSnapshot.child("insuranceID").getValue().toString()
                        val referralUID: String = refInsSnapshot.child("referralUID").getValue().toString()
                        val insuranceExpiryDate: Date = Date(refInsSnapshot.child("insuranceExpiryDate").child("time").getValue() as Long)
                        val status: String = refInsSnapshot.child("status").getValue().toString()

                        val refIns: ReferralInsurance = ReferralInsurance(insuranceReferralID,insuranceID,referralUID,insuranceExpiryDate,status)

                        tempRefInsList.add(refIns)
                    }

                    for(insList in tempRefInsList) {
                        if(insList.insuranceReferralID.equals(currentItem.insuranceReferralUID)) {
                            holder.cancelInsuranceStatus.text = insList.status
                            if(insList.status.equals("Pending")){
                                holder.cancelInsuranceStatus.setTextColor(Color.parseColor("#EC512B"))
                            } else if (insList.status.equals("Cancelled")) {
                                holder.cancelInsuranceStatus.setTextColor(Color.parseColor("#F30E15"))
                                holder.btnApprove.visibility = View.GONE
                                holder.btnReject.visibility = View.GONE
                            } else if (insList.status.equals("Active")) {
                                holder.cancelInsuranceStatus.setTextColor(Color.parseColor("#31B12C"))
                                holder.btnApprove.visibility = View.GONE
                                holder.btnReject.visibility = View.GONE
                            }

                        }
                    }



                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val isExpandable : Boolean = cancelInsuranceApplicationList[position].expandable
        holder.expandableLayout.visibility = if (isExpandable) View.VISIBLE else View.GONE

        holder.toExpandLayout.setOnClickListener() {
            val currentItem = cancelInsuranceApplicationList[position]
            currentItem.expandable = !currentItem.expandable
            notifyItemChanged(position)
        }

        holder.bind(currentItem!!, acceptListener, rejectListener)

    }

    override fun getItemCount(): Int {
        return cancelInsuranceApplicationList.size
    }

    class myViewHolder private constructor(val binding: InsuranceCancelItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CancelInsurance, acceptListener: AcceptListener, rejectListener: RejectListener) {
            binding.cancelApplicationUID = item.cancelInsuranceUID
            binding.insuranceReferralUID = item.insuranceReferralUID
            binding.executePendingBindings()
            binding.acceptListener = acceptListener
            binding.rejectListener = rejectListener
        }

        companion object {
            fun from(parent: ViewGroup): myViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = InsuranceCancelItemLayoutBinding.inflate(layoutInflater, parent, false)
                return myViewHolder(binding)
            }
        }

        val referralInsuranceID: TextView = binding.tvCancelInsuranceReferralID
        val appliedDate: TextView = binding.tvAppliedDate
        val cancelInsuranceID : TextView = binding.tvCancelInsuranceID
        val cancelReason : TextView = binding.tvCancelInsuranceReason
        val cancelInsuranceStatus : TextView = binding.tvCancelInsuranceStatus

        val btnApprove : Button = binding.btnApprove
        val btnReject : Button = binding.btnReject

        val expandableLayout: ConstraintLayout = binding.expandableLayout
        val toExpandLayout: ConstraintLayout = binding.toExpandLayout
    }

    class AcceptListener(val clickListener: (cancelApplicationID: String, insuranceReferralUID : String) -> Unit) {
        fun onClick(cancelApplicationID: String, insuranceReferralUID : String) = clickListener(cancelApplicationID, insuranceReferralUID)
    }

    class RejectListener(val clickListener: (cancelApplicationID: String, insuranceReferralUID : String) -> Unit){
        fun onClick(cancelApplicationID: String, insuranceReferralUID : String) = clickListener(cancelApplicationID, insuranceReferralUID)
    }
}