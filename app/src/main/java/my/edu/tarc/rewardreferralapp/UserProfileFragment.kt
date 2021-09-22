package my.edu.tarc.rewardreferralapp

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.animation.keyframe.ValueCallbackKeyframeAnimation
import com.google.firebase.database.*
import my.edu.tarc.rewardreferralapp.adapter.Card_Item_Adapter
import my.edu.tarc.rewardreferralapp.data.Card_Item_Model
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.data.InsuranceApplication
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.databinding.FragmentListInsuranceBinding
import my.edu.tarc.rewardreferralapp.databinding.FragmentUserProfileBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import my.edu.tarc.rewardreferralapp.helper.MyLottie
import java.util.*
import kotlin.collections.ArrayList

class UserProfileFragment : Fragment() {

    private val cardItemList = ArrayList<Card_Item_Model>()
    private var referral = ArrayList<Referral>()
    private var insApplicationList = ArrayList<InsuranceApplication>()
    private var insuranceList = ArrayList<Insurance>()

    private lateinit var viewpager: ViewPager2
    private lateinit var binding : FragmentUserProfileBinding
    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val referralRef = database.getReference("Referral")

    private val insuranceApplicationRef = database.getReference("InsuranceApplication")
    private val insuranceRef = database.getReference("Insurance")

    private var loadingDialog: Dialog?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadData()
        showLoading()
        binding = FragmentUserProfileBinding.inflate(inflater,  container ,false)

        viewpager = binding.viewpagerInsurance
        viewpager.adapter = Card_Item_Adapter(cardItemList)

        binding.btnInvite.setOnClickListener(){
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToReferFriendFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnBackUserProfile.setOnClickListener(){
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToHomepage()
            Navigation.findNavController(it).navigate(action)
        }

        binding.relativeLRefeList.setOnClickListener(){
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToUserReferralListsFragment()
            Navigation.findNavController(it).navigate(action)
        }

        //go to rewardCenterFragment
        binding.relativeLReward.setOnClickListener(){
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToRewardCenterFragment()
            Navigation.findNavController(it).navigate(action)
        }

        //go to rewardMyFragment
        binding.btnRedeemReward.setOnClickListener(){
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToRewardMyFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.relativeLProfile.setOnClickListener(){
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToProfileDetailsFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.relativeLCode.setOnClickListener(){
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToRefEnterCodeFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }

    private fun loadData(){
        //val referralUID: String?  = CheckUser().getCurrentUserUID()
        referralRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    referral.clear()
                    for (referralSnapshot in snapshot.children) {
                        if (referralSnapshot.child("referralUID").value.toString() == CheckUser().getCurrentUserUID()) {
                            val referralPoints: String = referralSnapshot.child("points").value.toString()
                            val referralName: String = referralSnapshot.child("fullName").value.toString()

                            binding.tvPoints.text = referralPoints
                            binding.tviewUserName.text = referralName
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        loadCardView()
    }

    //compare insurance and insurance application insuranceID
    private fun loadCardView() {
        //get insurance application
        insuranceApplicationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (insuranceSnapshot in snapshot.children) { //go in insuranceApplication
                        if (insuranceSnapshot.child("referralUID").value.toString() == CheckUser().getCurrentUserUID()) { //compare referralID
                            //then get the value that i needed here
                            val applicationID: String = insuranceSnapshot.child("applicationID").value.toString()
                            val applicationAppliedDate: Date = Date(insuranceSnapshot.child("applicationAppliedDate").child("time").value as Long)
                            val insuranceID: String = insuranceSnapshot.child("insuranceID").value.toString()
                            val referralID: String = insuranceSnapshot.child("referralUID").value.toString()
                            val insuranceStatus: String = insuranceSnapshot.child("applicationStatus").value.toString()

                            val insApp = InsuranceApplication(
                                applicationID,
                                insuranceID,
                                referralID,
                                applicationAppliedDate,
                                insuranceStatus,
                                false,
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                false
                            )
                            insApplicationList.add(insApp)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        //get insurance and compare insuranceID
        insuranceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    insuranceList.clear()
                    for (insuranceSnapshot in snapshot.children) {
                        for (insAppList in insApplicationList) {
                            //compare insuranceID here
                            if (insAppList.insuranceID.equals(insuranceSnapshot.child("insuranceID").getValue().toString())) {
                                val insuranceID: String =
                                    insuranceSnapshot.child("insuranceID").value.toString()
                                val insuranceName: String =
                                    insuranceSnapshot.child("insuranceName").value.toString()
                                val insuranceComp: String =
                                    insuranceSnapshot.child("insuranceComp").value.toString()
                                val insurancePlan: String =
                                    insuranceSnapshot.child("insurancePlan").value.toString()
                                val insuranceType: String =
                                    insuranceSnapshot.child("insuranceType").value.toString()
                                val insuranceCoverage: ArrayList<String> = ArrayList<String>()
                                for (child in insuranceSnapshot.child("insuranceCoverage").children) {
                                    insuranceCoverage.add(child.value.toString())
                                }
                                val insurancePrice: String =
                                    insuranceSnapshot.child("insurancePrice").value.toString()
                                val insuranceImg: String =
                                    insuranceSnapshot.child("insuranceImg").value.toString()

                                val insurance = Insurance(
                                    insuranceID,
                                    insuranceName,
                                    insuranceComp,
                                    insurancePlan,
                                    insuranceCoverage,
                                    insurancePrice.toDouble(),
                                    insuranceType,
                                    insuranceImg
                                )

                                insuranceList.add(insurance)

                            }
                        }
                    }


                    //loop the insurance list and insurance application list to get the value
                    for(insAppList in insApplicationList) {
                        for (insList in insuranceList) {
                            //if both insuranceID in Insurance and InsuranceApplication is same then get the value from each of them (insList, insAppList)
                            if(insAppList.insuranceID.equals(insList.insuranceID)) {
                                if(insList.insuranceComp == "Prudential") {
                                    cardItemList.add(Card_Item_Model(insList.insuranceComp.toString(), insList.insuranceName.toString(),insAppList.applicationStatus.toString(), R.drawable.prudential))
                                } else if (insList.insuranceComp == "AIA") {
                                    cardItemList.add(Card_Item_Model(insList.insuranceComp.toString(), insList.insuranceName.toString(),insAppList.applicationStatus.toString(), R.drawable.aia))
                                } else if (insList.insuranceComp == "Great Eastern") {
                                    cardItemList.add(Card_Item_Model(insList.insuranceComp.toString(), insList.insuranceName.toString(),insAppList.applicationStatus.toString(), R.drawable.great_eastern))
                                } else if (insList.insuranceComp == "Etiqa") {
                                    cardItemList.add(Card_Item_Model(insList.insuranceComp.toString(), insList.insuranceName.toString(),insAppList.applicationStatus.toString(), R.drawable.etiqa))
                                } else {
                                    cardItemList.add(Card_Item_Model(insList.insuranceComp.toString(), insList.insuranceName.toString(),insAppList.applicationStatus.toString(), R.drawable.default_img))
                                }
                            }
                        }
                    }

                    viewpager.adapter = Card_Item_Adapter(cardItemList)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun hideLoading() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }
}