package my.edu.tarc.rewardreferralapp

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.helper.MyLottie
import my.edu.tarc.rewardreferralapp.databinding.FragmentAddInsuranceBinding
import my.edu.tarc.rewardreferralapp.databinding.FragmentCancelInsuranceCustBinding
import my.edu.tarc.rewardreferralapp.data.CancelInsurance
import my.edu.tarc.rewardreferralapp.data.Insurance
import java.util.*
import kotlin.collections.ArrayList

class CancelInsuranceCustFragment : Fragment() {

    private lateinit var binding: FragmentCancelInsuranceCustBinding

    private val database = FirebaseDatabase.getInstance()
    private val cancelAppRef = database.getReference("CancelInsuranceApplication")

    private val referralInsuranceRef = database.getReference("ReferralInsurance")

    private var cancelApplicationList = ArrayList<CancelInsurance>()

    private val args: CancelInsuranceCustFragmentArgs by navArgs()

    private var completeDialog: Dialog?= null
    private var loadingDialog: Dialog?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCancelInsuranceCustBinding.inflate(inflater,  container ,false)

        binding.radGrpReason.setOnCheckedChangeListener { group, checkedId ->

            if (checkedId == R.id.radOther)
                binding.tfReason.visibility = View.VISIBLE
            else {
                binding.tfReason.setText("")
                binding.tfReason.visibility = View.GONE
            }
        }

        binding.btnProceed.setOnClickListener() {
            if (binding.radGrpReason.checkedRadioButtonId == -1) {
                Toast.makeText(requireContext(), "Please checked at least one button.", Toast.LENGTH_LONG).show()
            } else {
                addCancelApplication()
            }
        }

        return binding.root

    }

    private fun addCancelApplication() {

        showLoading()

        var newID : String
        var reason : String

        cancelAppRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    newID = UUID.randomUUID().toString()
                } else {
                    newID = UUID.randomUUID().toString()
                }

                val radGrpReason: RadioButton =
                    binding.radGrpReason.findViewById(binding.radGrpReason.checkedRadioButtonId)

                reason = if (radGrpReason.text.toString() == "Other")
                    binding.tfReason.text.toString()
                else
                    radGrpReason.text.toString()

                val newCancalApp = CancelInsurance(newID, args.insuranceReferralID.toString(), reason, Date(), false)

                println(args.insuranceReferralID.toString())

                referralInsuranceRef.orderByChild("insuranceReferralID").equalTo(args.insuranceReferralID.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.children){
                            if (ds.exists()){
                                ds.key?.let {
                                    referralInsuranceRef.child(it).child("status").setValue("Pending")
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

                cancelAppRef.child(newID).setValue(newCancalApp).addOnSuccessListener{
                    Handler().postDelayed({
                        hideLoading()
                        showApplyComplete()
                        Toast.makeText(context, "Apply successfully", Toast.LENGTH_LONG).show()
                        Handler().postDelayed({
                            hideApplyComplete()
                            val action = CancelInsuranceCustFragmentDirections.actionCancelInsuranceCustFragmentToReferralInsuranceListingFragment()
                            view?.let { Navigation.findNavController(it).navigate(action) }
                        }, 2000)
                    }, 3000)

                }.addOnFailureListener {
                    Toast.makeText(context, "Apply unsuccessful", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun hideLoading() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        hideLoading()
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }

    private fun hideApplyComplete() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showApplyComplete() {
        hideApplyComplete()
        loadingDialog = MyLottie.showApplyDialog(requireContext())
    }

}