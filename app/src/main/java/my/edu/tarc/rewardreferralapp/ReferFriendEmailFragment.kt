package my.edu.tarc.rewardreferralapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferFriendEmailBinding
import java.lang.Exception

class ReferFriendEmailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentReferFriendEmailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_refer_friend_email, container, false)

        //args
        val args = ReferFriendEmailFragmentArgs.fromBundle(requireArguments())
        val referCode = args.referralCode
        binding.tvReferCodes.text = referCode

        binding.constraintEmailContent.visibility = View.GONE

        binding.constraintLSendEmail.setOnClickListener(){
            binding.constraintEmailContent.visibility = View.VISIBLE
        }

        binding.imgViewDD.setOnClickListener(){
            binding.constraintEmailContent.visibility = View.VISIBLE
        }

        binding.btnBackInviteFriend.setOnClickListener(){
            val action = ReferFriendEmailFragmentDirections.actionReferFriendEmailFragmentToReferFriendFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnSendEmail.setOnClickListener(){
            val referCode: String = binding.tvReferCodes.text.toString()
            val recipientEmail: String = binding.txtRecipeintEmail.text.toString()
            val emailSubject: String = binding.txtEmailSubject.text.toString()
            val emailMsg: String = binding.txtEmailMsg.text.toString()

            sendEmail(recipientEmail, emailSubject, emailMsg)
        }

        return binding.root
    }

    private fun sendEmail(recipientEmail: String, emailSubject: String, emailMsg: String) {
        val intent = Intent(Intent.ACTION_SEND)

        intent.data = Uri.parse("Mail To: ")
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
        intent.putExtra(Intent.EXTRA_TEXT, emailMsg)

        try {
            startActivity(Intent.createChooser(intent,"Choose : "))
        }catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

}

