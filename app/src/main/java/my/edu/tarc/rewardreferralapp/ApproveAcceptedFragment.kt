package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.rewardreferralapp.databinding.FragmentApproveAcceptedBinding

class ApproveAcceptedFragment : Fragment() {
    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val claimRef = database.getReference("Claim")

    private var claimUUID: String = ""
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

        val updateStatus = mapOf<String,Any?>(
            "claimStatus" to "Accepted"
        )

        claimRef.child(claimUUID).updateChildren(updateStatus).addOnSuccessListener {
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

    
}