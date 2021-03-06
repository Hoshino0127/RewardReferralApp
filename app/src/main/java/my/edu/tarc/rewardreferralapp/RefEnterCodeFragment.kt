package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.os.Handler
import android.renderscript.Sampler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
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
    private var currentUserCode: String = ""
    private val codeList: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        checkIfGotUpLine()

        getCode()
        tempbinding = FragmentRefEnterCodeBinding.inflate(inflater, container, false)

        binding.btnSubmitCode.setOnClickListener(){
            checkIfGotUpLine()
            checkUpLine()
            checkError()
        }

        binding.btnBackEnterRefCode.setOnClickListener(){
            val action = RefEnterCodeFragmentDirections.actionRefEnterCodeFragmentToUserProfileFragment()
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }

    private fun checkUpLine(){
        val referCodeEntered: String = binding.txtReferralCode.text.toString()
        val referralUID = CheckUser().getCurrentUserUID()
        referralRef.orderByChild("referralUID").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (updateSnapshot in snapshot.children) {
                    if (updateSnapshot.exists()) {
                        updateSnapshot.key?.let {
                            val uplineUID: String = updateSnapshot.child("referralUID").value.toString()
                            var points: Int = updateSnapshot.child("points").value.toString().toInt()
                            if (referralUID != null) {
                                if (checkError()) {
                                    if(referCodeEntered == updateSnapshot.child("invitationCode").value.toString()) {
                                        //set the current user's referralUpline to upline ID
                                            points += 10
                                        referralRef.child(referralUID).child("referralUpline").setValue(uplineUID)
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Refer successfully.", Toast.LENGTH_LONG).show()
                                                referralRef.child(uplineUID).child("points").setValue(points).addOnSuccessListener {
                                                    val handler = Handler()
                                                    handler.postDelayed({
                                                        val action = RefEnterCodeFragmentDirections.actionRefEnterCodeFragmentToUserProfileFragment()
                                                        Navigation.findNavController(requireView()).navigate(action)
                                                    }, 3000)
                                                }.addOnFailureListener {
                                                    Toast.makeText(context, "Unable to add points to upline.", Toast.LENGTH_LONG).show()
                                                }

                                            }.addOnFailureListener {
                                                Toast.makeText(context, "Unable to refer, you may have entered the invalid code.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun checkIfGotUpLine() : Boolean{
        referralRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(checkSnapshot in snapshot.children){
                        if (checkSnapshot.child("referralUID").value.toString() == CheckUser().getCurrentUserUID()) {
                            if(checkSnapshot.child("referralUpline").value.toString() != ""){
                                binding.constraintAlertMsg.visibility = View.VISIBLE
                                binding.btnSubmitCode.visibility = View.INVISIBLE
                                binding.textView58.visibility = View.INVISIBLE
                                binding.textView59.visibility = View.INVISIBLE
                                binding.txtReferralCode.visibility = View.INVISIBLE
                                //binding.txtReferralCode.isEnabled = false
                            }
                            if(checkSnapshot.child("referralUpline").value.toString() != "none"){
                                binding.constraintAlertMsg.visibility = View.VISIBLE
                                binding.btnSubmitCode.visibility = View.INVISIBLE
                                binding.textView58.visibility = View.INVISIBLE
                                binding.textView59.visibility = View.INVISIBLE
                                binding.txtReferralCode.visibility = View.INVISIBLE
                                //binding.txtReferralCode.isEnabled = false
                            }
                            if(checkSnapshot.child("referralUpline").value.toString().isNullOrBlank() || checkSnapshot.child("referralUpline").value.toString() == "none"){
                                binding.constraintAlertMsg.visibility = View.INVISIBLE
                                binding.btnSubmitCode.visibility = View.VISIBLE
                                binding.textView58.visibility = View.VISIBLE
                                binding.textView59.visibility = View.VISIBLE
                                binding.txtReferralCode.visibility = View.VISIBLE
                                //binding.txtReferralCode.isEnabled = true
                            }
                        }
                    }
                }
            }


            override fun onCancelled(error: DatabaseError) {

            }

        })
        return true
    }

    private fun checkError() : Boolean{
        if(binding.txtReferralCode.text.isEmpty()){
            Toast.makeText(context, "Please enter a referral code.", Toast.LENGTH_SHORT).show()
            binding.txtReferralCode.requestFocus()
            return false
        }

        if(binding.txtReferralCode.text.toString() == currentUserCode){
            Toast.makeText(context, "This is your own code, try share to others", Toast.LENGTH_SHORT).show()
            binding.txtReferralCode.requestFocus()
            return false
        }

        for(code in codeList){
            if(code == binding.txtReferralCode.text.toString()){
                Toast.makeText(context, "You can't become your downline's downline.", Toast.LENGTH_SHORT).show()
                binding.txtReferralCode.requestFocus()
                return false
            }
        }

        return true
    }

    private fun getCode(){
        referralRef.orderByChild("referralUID").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(referralSS in snapshot.children){
                        if(referralSS.child("referralUID").value.toString() == CheckUser().getCurrentUserUID()){
                            currentUserCode = referralSS.child("invitationCode").value.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        getDownlineCode()
    }

    private fun getDownlineCode(){
        referralRef.orderByChild("referralUpline").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(referralSS in snapshot.children){
                        if(referralSS.child("referralUpline").value.toString() == CheckUser().getCurrentUserUID()){
                            codeList.add(referralSS.child("invitationCode").value.toString())
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}
