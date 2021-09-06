package my.edu.tarc.rewardreferralapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.ClaimFigure


class ClaimFigureAdapter(private val context: Context, private val cfList: java.util.ArrayList<ClaimFigure>) : BaseAdapter() {
    private lateinit var tvClaimFigureName: TextView
    private lateinit var tvClaimFigureAmount: TextView

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
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.claim_figure_item, parent, false)
        tvClaimFigureName = convertView.findViewById(R.id.tvClaimFigureName_cardview)
        tvClaimFigureAmount = convertView.findViewById(R.id.tvClaimFigureAmount_cardview)
        tvClaimFigureName.text = cfList[position].claimFigureName
        tvClaimFigureAmount.text = String.format("%.2f",cfList[position].claimFigureAmount)
        return convertView
    }
}