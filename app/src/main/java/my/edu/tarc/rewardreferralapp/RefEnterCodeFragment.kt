package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.databinding.FragmentRefEnterCodeBinding
import my.edu.tarc.rewardreferralapp.databinding.FragmentUpdateProfileDetailsBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser

class RefEnterCodeFragment : Fragment() {

    private var tempbinding: FragmentRefEnterCodeBinding? = null
    private val binding get() = tempbinding!!
    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val referralRef = database.getReference("Referral")
    private val referral = ArrayList<Referral>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        tempbinding = FragmentRefEnterCodeBinding.inflate(inflater, container, false)
//        val binding: FragmentRefEnterCodeBinding =
//            DataBindingUtil.inflate(inflater, R.layout.fragment_ref_enter_code, container, false)

        binding.btnSubmitCode.setOnClickListener(){
            Toast.makeText(context, "You have successfully refer a friend.", Toast.LENGTH_LONG).show()

            val handler = Handler()
            handler.postDelayed({
                val action = RefEnterCodeFragmentDirections.actionRefEnterCodeFragmentToUserProfileFragment()
                Navigation.findNavController(it).navigate(action)
            }, 3000)

            //increasePoints()
        }
        return binding.root
    }

    private fun increasePoints(){
        referralRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (referralSnapshot in snapshot.children)
                        if (referralSnapshot.child("referralUID").value.toString() == CheckUser().getCurrentUserUID()) {
                            if(referralSnapshot.child("invitationCode").value.toString() == binding.txtReferralCode.text.toString()){
                                //increase the referral points
                            }

                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun checkError() : Boolean{
        if(binding.txtReferralCode.text.isEmpty()){
            Toast.makeText(context, "Please enter a valid referral code.", Toast.LENGTH_SHORT).show()
            binding.txtReferralCode.requestFocus()
            return false
        }
        return true
    }

}