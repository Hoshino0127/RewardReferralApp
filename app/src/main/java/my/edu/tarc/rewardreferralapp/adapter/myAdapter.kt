package my.edu.tarc.rewardreferralapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.R


class myAdapter(internal var context: Context, internal var insuranceList:MutableList<Insurance>) :
    RecyclerView.Adapter<myAdapter.myViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.insurance_item, parent, false)
        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {

        val currentitem = insuranceList[position]

        holder.insuranceName.text = currentitem.insuranceName
        //holder.insuranceExpiryDate.text = currentitem.insuranceExpiryDate.toString()
        holder.insuranceComp.text = currentitem.insuranceComp
        holder.insurancePlan.text = currentitem.insurancePlan

    }

    override fun getItemCount(): Int {
        return insuranceList.size
    }

    class myViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val insuranceName: TextView = itemView.findViewById(R.id.tvInsuranceName_cardview)
        //val insuranceExpiryDate: TextView = itemView.findViewById(R.id.tvInsuranceExpiryDate_cardview)
        val insuranceComp: TextView = itemView.findViewById(R.id.tvInsuranceComp_cardview)
        val insurancePlan: TextView = itemView.findViewById(R.id.tvInsurancePlan_cardview)

    }

}