package my.edu.tarc.referral

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import my.edu.tarc.rewardreferralapp.R
import my.edu.tarc.rewardreferralapp.databinding.FragmentUserRegisterBinding

class UserRegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentUserRegisterBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_register, container, false)

//        binding.btnRegister.setOnClickListener(){
//          val action = UserRegisterFragmentDirections.actionUserRegisterFragmentToUserRegisterSuccessful()
//            Navigation.findNavController(it).navigate(action)
//        }

        return binding.root
    }
}