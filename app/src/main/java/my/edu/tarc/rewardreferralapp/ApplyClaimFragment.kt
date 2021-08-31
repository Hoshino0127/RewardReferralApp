package my.edu.tarc.rewardreferralapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.rewardreferralapp.databinding.FragmentApplyClaimBinding
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferralInsuranceListingBinding

class ApplyClaimFragment : Fragment() {
    val db = Firebase.firestore



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentApplyClaimBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_apply_claim, container, false)

        val args = ApplyClaimFragmentArgs.fromBundle(requireArguments())
        binding.tvInsuranceID.text = args.insuranceID
        binding.btnApplyClaim.setOnClickListener(){
            if(CheckError()){
                if(ApplyClaim()) {
                    val action = ApplyClaimFragmentDirections.actionApplyClaimFragmentToApplyClaimSuccessFragment()
                    Navigation.findNavController(it).navigate(action)
                }
            }

        }

        return binding.root
    }

    private fun CheckError(): Boolean{
        return true
    }

    private fun ApplyClaim(): Boolean{
        // Create a new user with a first and last name
        val user = hashMapOf(
            "first" to "Lee",
            "last" to "Ken",
            "born" to 2000
        )

        // Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        return true
    }


}