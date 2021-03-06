package my.edu.tarc.rewardreferralapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.data.Claim
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.databinding.FragmentUserRegisterBinding
import java.util.*

class UserRegisterFragment : Fragment() {
    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private lateinit var binding: FragmentUserRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val referralRef = database.getReference("Referral")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_register, container, false)

        binding.btnBackAccRegister.setOnClickListener(){
            val action = UserRegisterFragmentDirections.actionUserRegisterFragmentToUserLoginFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnRegister.setOnClickListener(){
            if(errorFree()){
                val email = binding.txtEmail.text.toString()
                val password = binding.txtPassword.text.toString()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser

                            user!!.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "createUserWithEmail:success")
                                        addReferral(user.uid)
                                    }
                                }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(requireContext(), "Registration failed, please try again later",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        return binding.root
    }

    fun errorFree(): Boolean{
        if(binding.txtEmail.text.toString().isEmpty()){
            binding.txtEmail.error = "Please enter your email"
            binding.txtEmail.requestFocus()
            return false
        }

        if(!(Patterns.EMAIL_ADDRESS.matcher(binding.txtEmail.text.toString()).matches())){
            binding.txtEmail.error = "Please enter a valid email"
            binding.txtEmail.requestFocus()
            return false
        }

        if(binding.txtPassword.text.toString().isEmpty()){
            binding.txtPassword.error = "Please enter your password"
            binding.txtPassword.requestFocus()
            return false
        }

        if(binding.txtConfPass.text.toString().isEmpty()){
            binding.txtConfPass.error = "Please confirm your password"
            binding.txtConfPass.requestFocus()
            return false
        }

        if(!(binding.txtPassword.text.toString().equals(binding.txtConfPass.text.toString()))){
            binding.txtPassword.error = "Password does not match"
            binding.txtPassword.requestFocus()
            return false
        }
        return true
    }

    private fun addReferral(userUID: String){
        referralRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rnd: Random = Random()
                var code: Int = rnd.nextInt(999999)
                var invitationCode: String = String.format("%06d",code)

                val gender: String = binding.spGender.selectedItem.toString()
                val refStatus = "Active"
                val refFullName: String = binding.txtFullName.text.toString()
                val refNRIC: String = binding.txtNRIC.text.toString()
                val refContact: String = binding.txtContact.text.toString()
                val refEmail: String = binding.txtEmail.text.toString()
                val refAddress: String = binding.txtAddress.text.toString()
                val deductible = 0.1
                val points = 0
                val refUpLine = "none"

                val referral = Referral(userUID, refStatus, refFullName, gender, refNRIC, refContact, refEmail, refAddress, deductible, points, invitationCode, refUpLine)
                referralRef.child(referral.referralUID!!).setValue(referral).addOnSuccessListener {
                    val action = UserRegisterFragmentDirections.actionUserRegisterFragmentToUserRegisterSuccessful()
                    Navigation.findNavController(requireView()).navigate(action)
                }.addOnFailureListener{
                    Toast.makeText(requireContext(),"Register failed",Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }
        })
    }
}