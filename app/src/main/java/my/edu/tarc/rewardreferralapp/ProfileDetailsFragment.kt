package my.edu.tarc.rewardreferralapp

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.databinding.FragmentProfileDetailsBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser

class ProfileDetailsFragment : Fragment() {

    private var tempReferral = ArrayList<Referral>()
    private var referral = ArrayList<Referral>()

    private var tempbinding: FragmentProfileDetailsBinding? = null
    private val binding get() = tempbinding!!

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val referralRef = database.getReference("Referral")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadData()

        tempbinding = FragmentProfileDetailsBinding.inflate(inflater,  container ,false)

        //go back to user profile
        binding.btnBackToUserProfile.setOnClickListener(){
            val action = ProfileDetailsFragmentDirections.actionProfileDetailsFragmentToUserProfileFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnBackProfileDetails.setOnClickListener(){
            val action = ProfileDetailsFragmentDirections.actionProfileDetailsFragmentToUserProfileFragment()
            Navigation.findNavController(it).navigate(action)
        }

        //update
        binding.btnUpdate.setOnClickListener(){
            val action = ProfileDetailsFragmentDirections.actionProfileDetailsFragmentToUpdateProfileDetailsFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }

    private fun loadData(){
        referralRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    referral.clear()
                        for (referralSnapshot in snapshot.children) {
                            if (referralSnapshot.child("referralUID").value.toString() == CheckUser().getCurrentUserUID()) {
                                val referralName: String = referralSnapshot.child("fullName").value.toString()
                                val referralNRIC: String = referralSnapshot.child("nric").value.toString()
                                val referralContact: String = referralSnapshot.child("contactNo").value.toString()
                                val referralEmail: String = referralSnapshot.child("email").value.toString()
                                val referralGender: String = referralSnapshot.child("gender").value.toString()
                                val referralAddress: String = referralSnapshot.child("address").value.toString()

                            with(binding) {
                                tviewFullName.text = referralName
                                tviewNRIC.text = referralNRIC
                                tviewPhone.text = referralContact
                                tviewEmail.text = referralEmail
                                tviewGender.text = referralGender
                                tviewAddress.text = referralAddress
                            }
//                        val referralData = Referral(referralName, referralContact, referralGender, referralNRIC, referralEmail, referralAddress)
//                        referral.add(referralData)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}