package my.edu.tarc.rewardreferralapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.Claim
import my.edu.tarc.rewardreferralapp.data.ClaimFigure
import my.edu.tarc.rewardreferralapp.databinding.ClaimFigureItemBinding
import my.edu.tarc.rewardreferralapp.databinding.ClaimItemBinding
import java.util.zip.Inflater


class ClaimFigureAdapter(private val context: Context, private val removable: Boolean, private val cfList: java.util.ArrayList<ClaimFigure>, private val removeListener: RemoveListener) : BaseAdapter() {
    private lateinit var tvClaimFigureName: TextView
    private lateinit var tvClaimFigureAmount: TextView
    private lateinit var imgCrossClaimFigure: ImageView

    override fun getCount(): Int {
        return cfList.size
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val binding:ClaimFigureItemBinding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.claim_figure_item,parent,false)
        binding.position = position
        binding.tvClaimFigureNameCardview.text = cfList[position].claimFigureName
        binding.tvClaimFigureAmountCardview.text = String.format("%.2f",cfList[position].claimFigureAmount)
        if(!removable){
            binding.imgCrossClaimFigure.visibility = View.GONE
        }else{
            binding.removeListener = removeListener
        }
        return binding.root
    }

    class RemoveListener(val clickListener:(position: Int) -> Unit){
        fun onClick(position: Int) = clickListener(position)
    }
}