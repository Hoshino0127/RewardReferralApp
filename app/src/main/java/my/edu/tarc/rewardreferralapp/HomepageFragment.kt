package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import my.edu.tarc.rewardreferralapp.databinding.FragmentHomepageBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser

class HomepageFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    if(auth.currentUser != null){
                        Firebase.auth.signOut()
                        logout()
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        val binding: FragmentHomepageBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_homepage, container, false)

        binding.imgProfile.setOnClickListener(){
            if(auth.currentUser != null){
                Firebase.auth.signOut()
                logout()
            }
        }

        binding.button.setOnClickListener(){
            val action = HomepageFragmentDirections.actionHomepageToReferralInsuranceListingFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.relativeLayout8.setOnClickListener(){
            val action = HomepageFragmentDirections.actionHomepageToClaimListingFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(!CheckUser().ifCurrentUserExists()){
            logout()
        }
    }

    fun logout(){
        Toast.makeText(requireContext(),"Successfully logged out",Toast.LENGTH_LONG)
        val action = HomepageFragmentDirections.actionHomepageToUserLoginFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }

}