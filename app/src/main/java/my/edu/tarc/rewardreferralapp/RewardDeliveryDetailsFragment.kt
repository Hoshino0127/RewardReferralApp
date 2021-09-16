package my.edu.tarc.rewardreferralapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.data.RefferalReward
import my.edu.tarc.rewardreferralapp.data.RewardDelivery
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardDeliveyDetailsBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RewardDeliveryDetailsFragment : Fragment() {

    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private val deliveryRef = database.getReference("RewardDelivery")
    private val refRewRef = database.getReference("RefferalReward")

    private lateinit var binding: FragmentRewardDeliveyDetailsBinding
    private var chkRewardList: ArrayList<String> = ArrayList<String>()
    private var rewardClaimID: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val args = RewardDeliveryDetailsFragmentArgs.fromBundle(requireArguments())
        rewardClaimID = args.rewardClaimID.toString()
        if (rewardClaimID == "") {
            chkRewardList = args.rewardClaimList.toList() as ArrayList<String>
        } else {
            chkRewardList.add(rewardClaimID)
        }

        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_reward_delivey_details,
                container,
                false
            )


        binding.btnRDDSubmit.setOnClickListener() {

            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirm details")
            builder.setMessage("Are you sure to use this delivery address. You won't able to change your address after confirm it")

            builder.setPositiveButton("Yes") { dialogInterface, which ->
                if (checkError()) {
                    addDeliveryDetail()
                }
            }
            builder.setNegativeButton("No") { dialogInterface, which ->
                dialogInterface.dismiss()
            }

            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
        }


        return binding.root
    }

    private fun checkError(): Boolean {

        if (binding.ptRRDAddress1.text.toString() == "") {
            binding.ptRRDAddress1.requestFocus()
            Toast.makeText(context, "Please enter your address", Toast.LENGTH_LONG).show()
            return false
        }

        if (binding.ptRDDCity.text.toString() == "") {
            binding.ptRDDCity.requestFocus()
            Toast.makeText(context, "Please enter your city", Toast.LENGTH_LONG).show()
            return false
        }

        if (binding.spRDDState.selectedItem.toString() == "") {
            binding.spRDDState.requestFocus()

            Toast.makeText(context, "Please enter your state", Toast.LENGTH_LONG).show()
            return false
        }

        if (binding.ptRDDPostCode.text.toString() == "") {
            binding.ptRDDPostCode.requestFocus()
            Toast.makeText(context, "Please enter your postcode", Toast.LENGTH_LONG).show()
            return false
        }

        return true

    }

    private fun addDeliveryDetail() {

        var newID: String = UUID.randomUUID().toString()


        val dateformat = SimpleDateFormat("dd/MM/yyyy")
        val applyDate = dateformat.format(Date())

        val rd = RewardDelivery(
            newID,
            binding.ptRRDAddress1.text.toString(),
            binding.ptRDDAddress2.text.toString(),
            binding.ptRRDAddress3.text.toString(),
            binding.ptRDDCity.text.toString(),
            binding.spRDDState.selectedItem.toString(),
            binding.ptRDDPostCode.text.toString(),
            "Pending",
            applyDate
        )

        deliveryRef.child(newID).setValue(rd).addOnSuccessListener() {

            //update status to completed
            val upRefferalReward = mapOf<String, Any?>(
                "status" to "Completed",
                "deliveryID" to newID
            )

            var chkError = 0

            for (chkRewardClaimID in chkRewardList) {

                refRewRef.child(chkRewardClaimID).updateChildren(upRefferalReward)
                    .addOnSuccessListener() {

                    }
                    .addOnFailureListener {
                        chkError++
                    }

            }

            if (chkError == 0) {

                val action =
                    RewardDeliveryDetailsFragmentDirections.actionRewardDeliveryDetailsFragmentToRewardRedeemSuccessFragment(
                        newID,
                        "Entry"
                    )
                Navigation.findNavController(requireView()).navigate(action)

            } else {
                Toast.makeText(context, "update status fail", Toast.LENGTH_LONG).show()
            }


        }.addOnFailureListener {
            Toast.makeText(context, "add delivery fail", Toast.LENGTH_LONG).show()
        }


    }

}