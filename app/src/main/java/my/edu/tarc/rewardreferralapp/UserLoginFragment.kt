package my.edu.tarc.referral

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
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.databinding.FragmentUserLoginBinding


class UserLoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

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
        return binding.root
    }


}