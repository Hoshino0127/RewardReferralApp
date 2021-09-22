package my.edu.tarc.rewardreferralapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferFriendBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser

class ReferFriendFragment : Fragment() {

    private var tempbinding: FragmentReferFriendBinding? = null
    private val binding get() = tempbinding!!

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val referralRef = database.getReference("Referral")
    private val referral = ArrayList<Referral>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadData()
        tempbinding = FragmentReferFriendBinding.inflate(inflater,  container ,false)

        //go back user profile
        binding.btnBackReferFriend.setOnClickListener(){
//            val action = ReferFriendFragmentDirections.()
//            Navigation.findNavController(it).navigate(action)
        }

        binding.btnCopyCode.setOnClickListener(){
            copyCode()
//            binding.tvRefCodeResult.setText(generateCode())
        }

        binding.btnRefer.setOnClickListener(){
            val referralCode: String = binding.tvRefCodeResult.text.toString()
            val action = ReferFriendFragmentDirections.actionReferFriendFragmentToReferFriendShareTypeFragment(referralCode)
            Navigation.findNavController(it).navigate(action)
        }
        return binding.root
    }

    private fun copyCode(){
        val copyText: String = binding.tvRefCodeResult.text.toString()
        val myClipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", copyText)
        myClipboard.setPrimaryClip(clipData)

        Toast.makeText(context, "Referral code has been copied.", Toast.LENGTH_LONG).show()
    }

    //load to get invitation code and display in text view
    private fun loadData(){
        referralRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    referral.clear()
                    for(refSnapshot in snapshot.children){
                        if(refSnapshot.child("referralUID").value.toString() == CheckUser().getCurrentUserUID()){
                            val referralInvCode: String = refSnapshot.child("invitationCode").value.toString()

                            binding.tvRefCodeResult.text = referralInvCode
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    //-----------------------------------------------------------------------------
//    private fun generateCode() : String {
//        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
//        val outputStrLength = (5..10).shuffled().first()
//        return (1..outputStrLength)
//            .map{Random.nextInt(0, charPool.size)}
//            .map(charPool::get)
//            .joinToString("")
//    }

}