package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.databinding.FragmentUpdateProfileDetailsBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser


class UpdateProfileDetailsFragment : Fragment() {

    private var referral = ArrayList<Referral>()

    private var tempbinding: FragmentUpdateProfileDetailsBinding? = null
    private val binding get() = tempbinding!!

    private val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val referralRef = database.getReference("Referral")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadData()
        tempbinding = FragmentUpdateProfileDetailsBinding.inflate(inflater, container, false)

        binding.btnBackToProfileDetails.setOnClickListener() {
            val action =
                UpdateProfileDetailsFragmentDirections.actionUpdateProfileDetailsFragmentToProfileDetailsFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnSave.setOnClickListener() {
            updateDetails()
        }

        binding.btnResetValue.setOnClickListener() {
            binding.txtEditPhone.text.clear()
            binding.txtEditPhone.requestFocus()
            binding.txtMultiAddress.text.clear()
            binding.spGenders.setSelection(0)
        }
        binding.progressBarSave.visibility = View.GONE

        return binding.root
    }

    private fun updateDetails() {
        binding.progressBarSave.visibility = View.VISIBLE
        val contact: String = binding.txtEditPhone.text.toString()
        val address: String = binding.txtMultiAddress.text.toString()
        val gender: String = binding.spGenders.selectedItem.toString()
        val referralUID = CheckUser().getCurrentUserUID()

        val referral = mapOf<String, Any?>(
            "contactNo" to contact,
            "address" to address,
            "gender" to gender
        )

        referralRef.orderByChild("referralUID")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (updateSnapshot in snapshot.children) {
                        if (updateSnapshot.exists()) {
                            updateSnapshot.key?.let {
                                if (referralUID != null) {
                                    if (checkError()) {
                                        referralRef.child(referralUID).updateChildren(referral)
                                            .addOnSuccessListener {
                                                binding.progressBarSave.visibility = View.GONE
                                                Toast.makeText(
                                                    context,
                                                    "Updated successfully!",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }.addOnFailureListener {
                                                binding.progressBarSave.visibility = View.GONE
                                                Toast.makeText(
                                                    context,
                                                    "Unable to update details.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun loadData() {
        val referralUID: String? = CheckUser().getCurrentUserUID()
        referralRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    referral.clear()
                    if (referralUID != null) {
                        for (referralSnapshot in snapshot.children) {
                            val referralName: String =
                                referralSnapshot.child("fullName").value.toString()
                            val referralNric: String =
                                referralSnapshot.child("nric").value.toString()
                            val referralEmail: String =
                                referralSnapshot.child("email").value.toString()

                            binding.tvRefFullName.text = referralName
                            binding.tvRefNRIC.text = referralNric
                            binding.tvRefEmail.text = referralEmail
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun checkError(): Boolean {
        if (binding.txtEditPhone.text.toString().isEmpty()) {
            Toast.makeText(context, "Please enter a valid contact number", Toast.LENGTH_LONG).show()
            binding.txtEditPhone.requestFocus()
            return false
        }

        if (binding.txtMultiAddress.text.toString().isEmpty()) {
            Toast.makeText(context, "Please enter your address", Toast.LENGTH_LONG).show()
            binding.txtEditPhone.requestFocus()
            return false
        }

        if (binding.spGenders.selectedItem.toString().isEmpty()) {
            Toast.makeText(context, "Please select your gender", Toast.LENGTH_LONG).show()
            binding.spGenders.requestFocus()
            return false
        }
        return true
    }
}