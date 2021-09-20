package my.edu.tarc.rewardreferralapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferFriendEmailBinding
import java.lang.Exception

class ReferFriendShareTypeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentReferFriendEmailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_refer_friend_email, container, false)

        binding.constraintEmailContent.visibility = View.GONE

        binding.constraintLSendEmail.setOnClickListener(){
            binding.constraintEmailContent.visibility = View.VISIBLE
        }

        binding.imgViewDD.setOnClickListener(){
            binding.constraintEmailContent.visibility = View.VISIBLE
        }

        binding.btnSendEmail.setOnClickListener(){
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
        intent.putExtra(Intent.EXTRA_SUBJECT, arrayOf(emailSubject))
        intent.putExtra(Intent.EXTRA_TEXT, arrayOf(emailMsg))

        try {
            startActivity(Intent.createChooser(intent,"Choose : "))
        }catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }


}