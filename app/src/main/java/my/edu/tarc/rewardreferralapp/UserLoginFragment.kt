package my.edu.tarc.rewardreferralapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.databinding.FragmentUserLoginBinding


class UserLoginFragment : Fragment() {
    private lateinit var binding: FragmentUserLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        auth = FirebaseAuth.getInstance()
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_login, container, false)

        val checkBox = binding.chkboxShowPass

        checkBox.setOnClickListener(){
            if(checkBox.isChecked){
                binding.txtLoginPass.inputType = 1
            }else{
                binding.txtLoginPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        binding.btnToRegister.setOnClickListener(){
            val action =
                UserLoginFragmentDirections.actionUserLoginFragmentToUserRegisterFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnLogin.setOnClickListener(){
            if(errorFree()){
                val email = binding.txtLoginUsername.text.toString()
                val password = binding.txtLoginPass.text.toString()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            val user = auth.currentUser
                            updateUI(user)

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(requireContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    }

            }
        }

        return binding.root
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser:FirebaseUser? = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser:FirebaseUser?){
        if(currentUser != null){
            if(currentUser.isEmailVerified){
                val action = UserLoginFragmentDirections.actionUserLoginFragmentToHomepage()
                Navigation.findNavController(requireView()).navigate(action)
            }else{
                Toast.makeText(requireContext(), "Please verify your email.",
                    Toast.LENGTH_SHORT).show()
            }

        }else{

        }
    }

    private fun errorFree():Boolean{
        if(binding.txtLoginUsername.text.toString().isEmpty()){
            binding.txtLoginUsername.error = "Please enter your email"
            binding.txtLoginUsername.requestFocus()
            return false
        }

        if(!(Patterns.EMAIL_ADDRESS.matcher(binding.txtLoginUsername.text.toString()).matches())){
            binding.txtLoginUsername.error = "Please enter a valid email"
            binding.txtLoginUsername.requestFocus()
            return false
        }

        if(binding.txtLoginPass.text.toString().isEmpty()){
            binding.txtLoginPass.error = "Please enter your password"
            binding.txtLoginPass.requestFocus()
            return false
        }


        return true
    }



}