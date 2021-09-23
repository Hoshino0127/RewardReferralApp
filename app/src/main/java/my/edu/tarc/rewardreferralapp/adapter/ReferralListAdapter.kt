package my.edu.tarc.rewardreferralapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.data.ReferralList

class ReferralListAdapter(referList1: FragmentActivity, val referList: List<Referral>) : RecyclerView.Adapter<ReferralListAdapter.myViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.referral_list_item, parent, false)
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentRefer = referList[position]
        holder.name.text = currentRefer.fullName
        holder.contact.text =currentRefer.contactNo
        holder.status.text = currentRefer.referralStatus

        if(currentRefer.referralStatus.equals("Inactive")){
            holder.status.setTextColor(Color.parseColor("#FF9C33"))
        }else if(currentRefer.referralStatus.equals("Active")){
            holder.status.setTextColor(Color.parseColor("#31B12C"))
        }else{
            holder.status.setTextColor(Color.parseColor("#000000"))
        }
    }

    override fun getItemCount(): Int {
        return referList.size
    }

    inner class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.tvReferFullName)
        var contact: TextView = view.findViewById(R.id.tvRefContactNo)
        var status : TextView = view.findViewById(R.id.tvReferStatus)

    }
}
