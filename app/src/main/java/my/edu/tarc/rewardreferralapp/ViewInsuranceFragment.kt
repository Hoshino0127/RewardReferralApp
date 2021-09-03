package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.adapter.InsuranceListRecyclerViewAdapter
import my.edu.tarc.rewardreferralapp.databinding.FragmentViewInsuranceBinding
import java.text.SimpleDateFormat

class ViewInsuranceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val insuranceList: List<Insurance> = listOf(
            Insurance("IN001","Car insurance","Etiqa","Plan A", SimpleDateFormat("dd/MM/yyyy").parse("22/08/2022"),"IR001"),
            Insurance("IN002","Motor insurance","Prudential","Plan C", SimpleDateFormat("dd/MM/yyyy").parse("25/04/2022"),"IR001"),
            Insurance("IN003","Truck insurance","Etiqa","Plan D", SimpleDateFormat("dd/MM/yyyy").parse("15/06/2022"),"IR001"),
            Insurance("IN004","Van insurance","Prudential","Plan B", SimpleDateFormat("dd/MM/yyyy").parse("07/04/2023"),"IR001")
        )

        // Inflate the layout for this fragment
        val binding: FragmentViewInsuranceBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_view_insurance, container, false)

        val insuranceListAdapter = InsuranceListRecyclerViewAdapter(insuranceList)

        binding.rvInsurance.adapter = insuranceListAdapter
        binding.rvInsurance.setHasFixedSize(true)

        return binding.root
    }

}