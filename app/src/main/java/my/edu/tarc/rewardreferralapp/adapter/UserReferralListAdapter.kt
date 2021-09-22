package my.edu.tarc.rewardreferralapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.UserReferralList

//class UserReferralListAdapter(referList1: FragmentActivity, val referList: List<UserReferralList>) : RecyclerView.Adapter<ReferralListAdapter.myViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_referral_list_item, parent, false)
//        return myViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
//        holder.id.text = referList.get(position).id
//        holder.name.text = referList.get(position).name
//        //email and contect
//
//    }
//
//    override fun getItemCount(): Int {
//        return referList.size
//    }
//
//    inner class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        var id : TextView = view.findViewById(R.id.tvReferID)
//        var name: TextView = view.findViewById(R.id.tvRefName)
//        var status : TextView
//
//        init{
//            status = view.findViewById(R.id.tvReferStatus)
//
//        }
//    }
//}