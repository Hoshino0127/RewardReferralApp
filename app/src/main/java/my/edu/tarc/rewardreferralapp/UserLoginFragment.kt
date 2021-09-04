package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
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

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        auth = FirebaseAuth.getInstance()
        val binding: FragmentUserLoginBinding =
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
            val action = UserLoginFragmentDirections.actionUserLoginFragmentToHomepage()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser:FirebaseUser? = auth.currentUser
        updateUI(currentUser)
    }

    fun updateUI(currentUser:FirebaseUser?){

    }



}