package my.edu.tarc.rewardreferralapp

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.databinding.FragmentLoggingInBinding
import my.edu.tarc.rewardreferralapp.dialog.LoadingDialog
import my.edu.tarc.rewardreferralapp.functions.CheckUser


class LoggingInFragment : Fragment() {
    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val referralRef = database.getReference("Referral")
    private lateinit var referralListener: ValueEventListener
    private lateinit var binding: FragmentLoggingInBinding
    private lateinit var referral: Referral
    private var isLogout: Boolean = false

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_logging_in, container, false)



        val dlg: LoadingDialog = LoadingDialog(requireActivity())
        dlg.showAlertDialog()
        binding.txtLoginStatusLoggingin.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString() == "Inactive"){
                    isLogout = true
                    logout()
                }else if(s.toString() == "Active"){
                    Toast.makeText(requireContext(), "Login success",
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), HomepageActivity::class.java)
                    startActivity(intent)
                }
            }

        })

        if(!isLogout){
            referralListener = object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(referralSS in snapshot.children){
                            if(referralSS.child("referralUID").getValue().toString().equals(CheckUser().getCurrentUserUID())){
                                val referralUID = referralSS.child("referralUID").getValue().toString()
                                val referralStatus = referralSS.child("referralStatus").getValue().toString()
                                referral = Referral(referralUID = referralUID, referralStatus = referralStatus)
                                binding.txtLoginStatusLoggingin.setText(referralStatus)
                                dlg.dismissAlertDialog()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            }

            referralRef.addListenerForSingleValueEvent(referralListener)
        }

        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        referralRef.removeEventListener(referralListener)
    }

    fun logout(){
        val handler: Handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({
            auth.signOut()
            Toast.makeText(requireContext(),"Account is inactive",Toast.LENGTH_LONG).show()
            val action = LoggingInFragmentDirections.actionLoggingInFragmentToUserLoginFragment()
            Navigation.findNavController(requireView()).navigate(action)
                            },1000)
    }


}