package my.edu.tarc.rewardreferralapp

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.databinding.FragmentApproveAcceptedBinding
import my.edu.tarc.rewardreferralapp.utils.SimpleEmail

class ApproveAcceptedFragment : Fragment() {
    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val claimRef = database.getReference("Claim")

    private var claimUUID: String = ""
    private var email: String = ""
    private lateinit var binding: FragmentApproveAcceptedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_approve_accepted, container, false)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val action = ApproveAcceptedFragmentDirections.actionApproveAcceptedFragmentToAdminClaimListingFragment()

                    Navigation.findNavController(requireView()).navigate(action)
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        binding.btnBackApproveClaimAccepted.setOnClickListener(){
            val action = ApproveAcceptedFragmentDirections.actionApproveAcceptedFragmentToAdminClaimListingFragment()

            Navigation.findNavController(requireView()).navigate(action)
        }

        val args = ApproveAcceptedFragmentArgs.fromBundle(requireArguments())
        claimUUID = args.claimUUID
        email = args.email

        val updateStatus = mapOf<String,Any?>(
            "claimStatus" to "Accepted"
        )

        claimRef.child(claimUUID).updateChildren(updateStatus).addOnSuccessListener {
            sendEmail()
            Toast.makeText(requireContext(),"Claim accepted",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Claim updated failed",Toast.LENGTH_SHORT).show()
        }

        binding.btnToClaimListing.setOnClickListener(){
            val action = ApproveAcceptedFragmentDirections.actionApproveAcceptedFragmentToAdminClaimListingFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        return binding.root
    }

    private fun sendEmail() {
        hideKeyboard()
        claimRef.orderByChild("claimUUID").equalTo(claimUUID).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(claimSS in snapshot.children){
                        val subject = "Claim application accepted"
                        val content =   """
                        <h1>Your claim is accepted</h1>
                        <p>Claim ID: ${claimSS.child("claimID").value.toString()}</p>
                        <p>Thank you very much!</p>
                        <p>From</p>
                        <p>MyBee Team</p>
                        """.trimIndent()

                        SimpleEmail().to(email)
                            .subject(subject)
                            .content(content)
                            .isHtml()
                            .send() {
                                snackbar("Email sent.")
                            }

                        snackbar("Sending email to applicants...")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun isEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun snackbar(text: String) {
        Snackbar.make(requireView(), text, Snackbar.LENGTH_SHORT).show()
    }
    
}