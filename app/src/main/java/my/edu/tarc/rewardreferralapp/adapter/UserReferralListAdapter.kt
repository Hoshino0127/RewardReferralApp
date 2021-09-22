package my.edu.tarc.rewardreferralapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.UserReferralList

class UserReferralListAdapter(UserReferList1: FragmentActivity, val UserReferList: List<UserReferralList>) : RecyclerView.Adapter<UserReferralListAdapter.myViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_referral_list_item, parent, false)
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.name.text = UserReferList.get(position).name
        holder.email.text = UserReferList.get(position).email
        holder.contact.text = UserReferList.get(position).contactNo
    }

    override fun getItemCount(): Int {
        return UserReferList.size
    }

    inner class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.tvReferralName)
        var email: TextView = view.findViewById(R.id.tvReferralEmail)
        var contact: TextView = view.findViewById(R.id.tvReferralContact)

    }
}