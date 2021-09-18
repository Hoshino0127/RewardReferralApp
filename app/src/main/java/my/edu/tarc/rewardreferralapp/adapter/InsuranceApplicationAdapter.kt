package my.edu.tarc.rewardreferralapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.data.InsuranceApplication
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.databinding.InsuranceApplicationItemLayoutBinding
import my.edu.tarc.rewardreferralapp.data.Insurance
import java.text.SimpleDateFormat

class InsuranceApplicationAdapter (internal var insuranceApplicationList:List<InsuranceApplication>, val clickListener: ViewListener) :
    RecyclerView.Adapter<InsuranceApplicationAdapter.myViewHolder>() {

    private var tempInsList = ArrayList<Insurance>()
    private val database = FirebaseDatabase.getInstance()
    private val insuranceRef = database.getReference("Insurance")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.insurance_application_item_layout, parent, false)
        return myViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = insuranceApplicationList[position]
        val format = SimpleDateFormat("dd-MMM-yyyy")

        insuranceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    tempInsList.clear()
                    for (insuranceSnapshot in snapshot.children){

                        val insuranceID: String = insuranceSnapshot.child("insuranceID").value.toString()
                        val insuranceName: String = insuranceSnapshot.child("insuranceName").value.toString()
                        val insuranceComp: String = insuranceSnapshot.child("insuranceComp").value.toString()
                        val insurancePlan: String = insuranceSnapshot.child("insurancePlan").value.toString()
                        val insuranceType: String = insuranceSnapshot.child("insuranceType").value.toString()
                        var insuranceCoverage: ArrayList<String> = ArrayList<String>()
                        for(child in insuranceSnapshot.child("insuranceCoverage").children){
                            insuranceCoverage.add(child.value.toString())
                        }
                        val insurancePrice: String = insuranceSnapshot.child("insurancePrice").value.toString()

                        val insurance = Insurance(insuranceID,insuranceName,insuranceComp,insurancePlan,insuranceCoverage,insurancePrice.toDouble() ,insuranceType)

                        tempInsList.add(insurance)

                    }

                    for(insList in tempInsList) {
                        if(insList.insuranceID.equals(currentItem.insuranceID)) {
                            holder.insuranceComp.text = insList.insuranceComp
                            holder.insuranceName.text = insList.insuranceName
                        }
                    }

                } else {
                    tempInsList.clear()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        holder.applicationID.text = currentItem.applicationID
        holder.applicationStatus.text = currentItem.applicationStatus
        holder.insuranceID.text = currentItem.insuranceID
        holder.appliedDate.text = format.format(currentItem.applicationAppliedDate)

        val isExpandable : Boolean = insuranceApplicationList[position].expandable
        holder.expandableLayout.visibility = if (isExpandable) View.VISIBLE else View.GONE

        holder.toExpandLayout.setOnClickListener() {
            val currentItem = insuranceApplicationList[position]
            currentItem.expandable = !currentItem.expandable
            notifyItemChanged(position)
        }

        if(currentItem.applicationStatus.equals("Pending")){
            holder.applicationStatus.setTextColor(Color.parseColor("#EC512B"))
        } else if (currentItem.applicationStatus.equals("Rejected")) {
            holder.applicationStatus.setTextColor(Color.parseColor("#F30E15"))
        } else if (currentItem.applicationStatus.equals("Accepted"))  {
            holder.applicationStatus.setTextColor(Color.parseColor("#31B12C"))
        } else {
            holder.applicationStatus.setTextColor(Color.parseColor("#F30E15"))
        }

        holder.bind(currentItem!!, clickListener)
    }

    override fun getItemCount(): Int {
        return insuranceApplicationList.size
    }

    class myViewHolder private constructor(val binding: InsuranceApplicationItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InsuranceApplication, clickListener: ViewListener) {
            binding.insApp = item
            binding.executePendingBindings()
            binding.viewListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): myViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = InsuranceApplicationItemLayoutBinding.inflate(layoutInflater, parent, false)
                return myViewHolder(binding)
            }
        }

        val applicationID: TextView = binding.tvInsuranceApplicationID
        val appliedDate: TextView = binding.tvAppliedDate
        val insuranceID: TextView = binding.tvApplicationInsuranceID
        val insuranceComp: TextView = binding.tvApplicationInsuranceComp
        val insuranceName: TextView = binding.tvApplicationInsuranceName
        val applicationStatus: TextView = binding.tvApplicationStatusText

        val expandableLayout: ConstraintLayout = binding.expandableLayout
        val toExpandLayout: ConstraintLayout = binding.toExpandLayout

    }

    class ViewListener(val clickListener: (ApplicationID: String, InsuranceID: String) -> Unit) {
        fun onClick(item: InsuranceApplication) =
            clickListener(item.applicationID!!, item.insuranceID!!)
    }



}
