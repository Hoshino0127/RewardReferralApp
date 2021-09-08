package my.edu.tarc.rewardreferralapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.skydoves.balloon.createBalloon
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.databinding.FragmentHomepageBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser


class HomepageFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()

    private var doubleBackToExitPressedOnce = false
    private val mHandler: Handler = Handler()
    private val mRunnable =
        Runnable { doubleBackToExitPressedOnce = false }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    if(auth.currentUser != null){
                        if(!doubleBackToExitPressedOnce){
                            doubleBackToExitPressedOnce = true
                            Toast.makeText(requireContext(),"Click back one more time to exit",Toast.LENGTH_SHORT).show()
                            mHandler.postDelayed(mRunnable, 2000);
                        }else{
                            activity?.finishAffinity()
                        }

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
        val intent: Intent = Intent(requireContext(),LoginActivity::class.java)
        startActivity(intent)
    }



}