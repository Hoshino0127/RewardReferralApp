package my.edu.tarc.rewardreferralapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.Insurance

class InsuranceListRecyclerViewAdapter(private var InsuranceList: List<Insurance>) : RecyclerView.Adapter<InsuranceListRecyclerViewAdapter.myViewHolder>() {

    class myViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val insuranceName: TextView ?= itemView.findViewById(R.id.tvInsuranceListName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.insurance_list_item,parent,false
        )

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentProduct = InsuranceList[position]
        holder.insuranceName?.text = currentProduct.insuranceName
    }

    override fun getItemCount(): Int {
        return InsuranceList.size
    }

    fun setInsuranceList(InsuranceList: List<Insurance>) {
        this.InsuranceList = InsuranceList
        notifyDataSetChanged()
    }
}