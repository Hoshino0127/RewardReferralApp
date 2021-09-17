package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentViewInsuranceBinding

class ViewInsuranceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        // Inflate the layout for this fragment
        val binding: FragmentViewInsuranceBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_view_insurance, container, false)


        return binding.root
    }

}