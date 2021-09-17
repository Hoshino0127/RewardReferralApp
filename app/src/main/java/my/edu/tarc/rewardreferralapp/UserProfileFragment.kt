package my.edu.tarc.rewardreferralapp

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.adapter.Card_Item_Adapter
import my.edu.tarc.rewardreferralapp.data.Card_Item_Model
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.databinding.FragmentUserProfileBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import java.util.*
import kotlin.collections.ArrayList


class UserProfileFragment : Fragment() {

    private var referral = ArrayList<Referral>()

    private var tempbinding: FragmentUserProfileBinding? = null
    private val binding get() = tempbinding!!

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val referralRef = database.getReference("Referral")

    private val cardItemAdapter = Card_Item_Adapter( //NEED TO CHANGE TO GET FROM THE
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadData()
        tempbinding = FragmentUserProfileBinding.inflate(inflater,  container ,false)

        val viewpager: ViewPager2 = binding.viewpagerInsurance
        viewpager.adapter = cardItemAdapter

        binding.btnInvite.setOnClickListener(){
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToReferFriendFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.referralbutton.setOnClickListener(){

        }

        binding.rewardbutton.setOnClickListener(){
            //go to jiho reward page
        }

        binding.profilebutton.setOnClickListener(){
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToProfileDetailsFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }

    private fun loadData(){
        val referralUID: String?  = CheckUser().getCurrentUserUID()
        referralRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    referral.clear()
                    if (referralUID != null) {
                        for (referralSnapshot in snapshot.children) {
                            val referralPoints: String =
                                referralSnapshot.child("points").value.toString()
                            val referralName: String =
                                referralSnapshot.child("fullName").value.toString()

                            binding.tvPoints.text = referralPoints
                            binding.tviewUserName.text = referralName
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

