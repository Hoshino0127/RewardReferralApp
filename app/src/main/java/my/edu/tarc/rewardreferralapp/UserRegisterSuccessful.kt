package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import my.edu.tarc.rewardreferralapp.databinding.FragmentUserRegisterSuccessfulBinding

class UserRegisterSuccessful : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentUserRegisterSuccessfulBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_register_successful, container, false)


        binding.btnToLoginPage.setOnClickListener(){
            val action = UserRegisterSuccessfulDirections.actionUserRegisterSuccessfulToUserLoginFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }
}