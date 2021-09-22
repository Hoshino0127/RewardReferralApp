package my.edu.tarc.rewardreferralapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.renderscript.Sampler
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
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
import java.lang.Exception


class HomepageFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private lateinit var binding: FragmentHomepageBinding

    lateinit var toggle : ActionBarDrawerToggle
    lateinit var drawerLayout : DrawerLayout

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val referralRef = database.getReference("Referral")
    private lateinit var referralListener: ValueEventListener

    private lateinit var referralUID: String
    private lateinit var referral: Referral

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

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_homepage, container, false)

        drawerLayout = binding.drawerLayoutHomepage
        val navView : NavigationView = binding.navView



        /*toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()*/

        navView.bringToFront()

        navView.setNavigationItemSelectedListener {

            when (it.itemId) {
                //add those fragment id here
                //R.id.dashboard -> replaceFragment(AddNewReferralFragment(), it.title.toString())
                R.id.nav_viewClaim -> navigateToFrag(HomepageFragmentDirections.actionHomepageToClaimListingFragment(), it.title.toString())
                R.id.nav_viewInsurance -> navigateToFrag(HomepageFragmentDirections.actionHomepageToReferralInsuranceListingFragment(), it.title.toString())
                R.id.nav_staffDashboard -> {
                    val intent = Intent(requireContext(), StaffDashboardActivity::class.java)
                    startActivity(intent)
                }
            }


            true
        }

        binding.imgNavigation.setOnClickListener(){
            drawerLayout.openDrawer(navView)
        }

        referralUID = CheckUser().getCurrentUserUID()!!

        referralListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(referralSS in snapshot.children){
                        if(referralSS.child("referralUID").getValue().toString().equals(referralUID)){
                            val referralUID: String = referralSS.child("referralUID").getValue().toString()
                            val deductible: Double = referralSS.child("deductible").getValue().toString().toDouble()
                            val fullName: String = referralSS.child("fullName").getValue().toString()
                            val contactNo: String = referralSS.child("contactNo").getValue().toString()
                            val email: String = referralSS.child("email").getValue().toString()
                            val points: Int = referralSS.child("points").getValue().toString().toInt()
                            referral = Referral(
                                referralUID = referralUID,
                                deductible = deductible,
                                fullName = fullName,
                                contactNo = contactNo,
                                email = email,
                                points = points
                            )
                            println(referral)
                        }
                    }
                    updateView()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        referralRef.addListenerForSingleValueEvent(referralListener)

        /*binding.imgLogo.setOnClickListener(){
            val intent = Intent(requireContext(), StaffDashboardActivity::class.java)
            startActivity(intent)
        }*/

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

        binding.relativeLayout9.setOnClickListener() {
            val action = HomepageFragmentDirections.actionHomepageToNavigationFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }

    private fun navigateToFrag(navDirections: NavDirections, title: String){
        try{
            val it = view
            if(it != null){
                Navigation.findNavController(it).navigate(navDirections)
            }
        }catch(e: Exception){

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onStart() {
        super.onStart()
        if(!CheckUser().ifCurrentUserExists()){
            logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        referralRef.removeEventListener(referralListener)
    }

    fun logout(){
        Toast.makeText(requireContext(),"Successfully logged out",Toast.LENGTH_LONG)
        val intent: Intent = Intent(requireContext(),LoginActivity::class.java)
        startActivity(intent)
    }

    fun updateView(){
        binding.txtReferralNameHomepage.text = referral.fullName
    }


}