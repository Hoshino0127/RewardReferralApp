package my.edu.tarc.rewardreferralapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.R

class InsuranceAdapter(internal var context: Context, internal var insuranceList:List<Insurance>) :
    RecyclerView.Adapter<InsuranceAdapter.myViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.insurance_item_layout, parent, false)
        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = insuranceList[position]
        val strMsg:String = currentItem.insuranceComp + "-" + currentItem.insuranceName + " (" + currentItem.insurancePlan + ")"
        holder.insuranceName.text = strMsg
    }

    override fun getItemCount(): Int {
        return insuranceList.size
    }

    class myViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val insuranceName: TextView = itemView.findViewById(R.id.tvInsuranceName)
    }

}