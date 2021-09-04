package my.edu.tarc.referral

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.adapter.Card_Item_Adapter
import my.edu.tarc.rewardreferralapp.data.Card_Item_Model
import my.edu.tarc.rewardreferralapp.databinding.FragmentUserProfileBinding

class UserProfileFragment : Fragment() {

    private val cardItemAdapter = Card_Item_Adapter(
        listOf(
            Card_Item_Model(
                "PRUDENTIAL",
                "Motor Insurance",
                "Accepted",
                R.drawable.prudential
            ),
            Card_Item_Model(
                "ETIQA",
                "Car Insurance",
                "Pending",
                R.drawable.etiqa
            ),
            Card_Item_Model(
                "AIA Insurance",
                "Truck Insurance",
                "Pending",
                R.drawable.aia
            ),
            Card_Item_Model(
                "Great Eastern",
                "Truck Insurance",
                "Pending",
                R.drawable.great_eastern
            )
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentUserProfileBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false)

        val viewpager: ViewPager2 = binding.viewpagerInsurance
        viewpager.adapter = cardItemAdapter
        return binding.root
    }
}

