package my.edu.tarc.rewardreferralapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferFriendBinding
import kotlin.random.Random

class ReferFriendFragment : Fragment() {

    private var tempbinding: FragmentReferFriendBinding? = null
    private val binding get() = tempbinding!!

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    //private val referralRef = database.getReference("Referral")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tempbinding = FragmentReferFriendBinding.inflate(inflater,  container ,false)

//        val binding: FragmentReferFriendBinding =
//            DataBindingUtil.inflate(inflater,R.layout.fragment_refer_friend, container, false)

        binding.btnGenerate.setOnClickListener(){
            binding.tvRefCodeResult.setText(generateCode())

        }

        binding.btnRefer.setOnClickListener(){
            val referralCode = binding.tvRefCodeResult.text.toString()

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "You can share this code : " + referralCode + " via")
            startActivity(Intent.createChooser(shareIntent, "Share referral code via :"))
        }

        return binding.root
    }

    private fun generateCode() : String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val outputStrLength = (5..10).shuffled().first()
        return (1..outputStrLength)
            .map{Random.nextInt(0, charPool.size)}
            .map(charPool::get)
            .joinToString("")
    }



}