package my.edu.tarc.rewardreferralapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.data.Card_Item_Model
import my.edu.tarc.rewardreferralapp.R

class Card_Item_Adapter (private val carItems: List<Card_Item_Model>) : RecyclerView.Adapter<Card_Item_Adapter.cardItemHolder>(){

    inner class cardItemHolder(view: View) : RecyclerView.ViewHolder(view){

        private val title = view.findViewById<TextView>(R.id.tvTitle)
        private val description = view.findViewById<TextView>(R.id.tvDescription)
        val status = view.findViewById<TextView>(R.id.tvAcceptorReject)
        private val image = view.findViewById<ImageView>(R.id.imgvInsurance)

        fun bind( car_Item_Model: Card_Item_Model){
            title.text = car_Item_Model.title
            description.text = car_Item_Model.description
            status.text = car_Item_Model.status
            image.setImageResource(car_Item_Model.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cardItemHolder {
        return cardItemHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.user_profile_card_items, parent, false)
        )
    }

    override fun onBindViewHolder(holder: cardItemHolder, position: Int) {
        val currentCard = carItems[position]
        holder.bind(carItems[position])

        if(currentCard.status.equals("Accepted")){
            holder.status.setTextColor(Color.parseColor("#31B12C"))
        }else if(currentCard.status.equals("Pending")){
            holder.status.setTextColor(Color.parseColor("#EC512B"))
        }else{
            holder.status.setTextColor(Color.parseColor("#000000"))
        }
    }

    override fun getItemCount(): Int {
        return carItems.size
    }
}