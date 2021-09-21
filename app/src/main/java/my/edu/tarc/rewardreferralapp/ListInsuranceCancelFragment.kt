package my.edu.tarc.rewardreferralapp

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.helper.MyLottie
import my.edu.tarc.rewardreferralapp.adapter.CancelInsuranceApplicationAdapter
import my.edu.tarc.rewardreferralapp.databinding.FragmentListInsuranceCancelBinding
import my.edu.tarc.rewardreferralapp.data.CancelInsurance
import my.edu.tarc.rewardreferralapp.utils.SimpleEmail
import java.util.*
import kotlin.collections.ArrayList

class ListInsuranceCancelFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private val cancelInsAppRef = database.getReference("CancelInsuranceApplication")
    private val referralInsRef = database.getReference("ReferralInsurance")
    private val referralRef = database.getReference("Referral")

    private lateinit var adapterCancelInsApp : CancelInsuranceApplicationAdapter

    private lateinit var binding: FragmentListInsuranceCancelBinding

    private var cancelInsuranceList = ArrayList<CancelInsurance>()
    private var tempCancelInsuranceList = ArrayList<CancelInsurance>()

    private var loadingDialog : Dialog?= null
    private var redirectDialog : Dialog?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentListInsuranceCancelBinding.inflate(inflater, container ,false)

        binding.shimmerViewContainer.startShimmer()

        binding.rvCancelApplication.setHasFixedSize(true)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val action = ListInsuranceCancelFragmentDirections.actionListInsuranceCancelFragmentToStaffDashboardFragment()
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }

        binding.btnBackListInsuranceApplicationCustView.setOnClickListener() {
            val action = ListInsuranceCancelFragmentDirections.actionListInsuranceCancelFragmentToStaffDashboardFragment()
            Navigation.findNavController(it).navigate(action)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        loadData()

        changeView(cancelInsuranceList)




        return binding.root

    }

    private fun loadData() {
        showLoading()
        cancelInsAppRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    cancelInsuranceList.clear()
                    for (refInsSnapshot in snapshot.children){

                        val cancelInsuranceID: String = refInsSnapshot.child("cancelInsuranceUID").value.toString()
                        val insuranceReferralID: String = refInsSnapshot.child("insuranceReferralUID").value.toString()
                        val cancelAppliedDate: Date = Date(refInsSnapshot.child("appliedDate").child("time").value as Long)
                        val reason: String = refInsSnapshot.child("reason").value.toString()

                        val refIns: CancelInsurance = CancelInsurance(cancelInsuranceID,insuranceReferralID, reason,cancelAppliedDate,false)

                        cancelInsuranceList.add(refIns)

                    }

                    tempCancelInsuranceList.addAll(cancelInsuranceList)
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.rvCancelApplication.visibility = View.VISIBLE
                    binding.tvNoRecord.visibility = View.GONE
                    binding.rvCancelApplication.adapter?.notifyDataSetChanged()
                    hideLoading()
                } else {
                    cancelInsuranceList.clear()
                    tempCancelInsuranceList.clear()
                    Handler().postDelayed ({
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                        binding.tvNoRecord.visibility = View.VISIBLE
                    }, 3000)
                    binding.rvCancelApplication.visibility = View.INVISIBLE
                    binding.rvCancelApplication.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        changeView(cancelInsuranceList)
    }

    private fun changeView(cancelInsuranceList: List<CancelInsurance>){
        val cancelInsAppAdapter = CancelInsuranceApplicationAdapter(cancelInsuranceList,
            CancelInsuranceApplicationAdapter.AcceptListener { CancelApplicationUID, InsuranceReferralUID ->
                val it = view
                if (it != null) {
                    referralInsRef.orderByChild("insuranceReferralID")
                        .equalTo(InsuranceReferralUID)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (ds in snapshot.children) {
                                    if(ds.exists()) {
                                        ds.key?.let {
                                            showLoading()
                                            send(ds.child("referralUID").value.toString(), "Approved")
                                            Handler().postDelayed({
                                                hideLoading()
                                                referralInsRef.child(it).child("status")
                                                    .setValue("Cancelled")
                                                changeView(cancelInsuranceList)
                                            }, 3000)
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                }
            },
            CancelInsuranceApplicationAdapter.RejectListener { CancelApplicationUID, InsuranceReferralUID ->
                val it = view
                if(it != null){
                    referralInsRef.orderByChild("insuranceReferralID")
                        .equalTo(InsuranceReferralUID)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (ds in snapshot.children) {
                                    if(ds.exists()) {
                                        ds.key?.let {
                                            showLoading()
                                            Handler().postDelayed({
                                                hideLoading()
                                                referralInsRef.child(it).child("status")
                                                    .setValue("Active")
                                                changeView(cancelInsuranceList)
                                            }, 3000)
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                }
            }
        )

        binding.rvCancelApplication.adapter = cancelInsAppAdapter
        binding.rvCancelApplication.setHasFixedSize(true)
        binding.rvCancelApplication.adapter?.notifyDataSetChanged()

    }

    private fun hideLoading() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        hideLoading()
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }

    private fun hideRedirect() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showRedirect() {
        hideLoading()
        loadingDialog = MyLottie.showRedirectingDialog(requireContext())
    }

    private fun send(ReferralUID: String, isApprove:String) {
        hideKeyboard()
        showLoading()
        referralRef.orderByChild("referralUID").equalTo(ReferralUID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    if(ds.exists()) {
                        val email = ds.child("email").value.toString().trim()
                        val subject = "Your application has been $isApprove - ${System.currentTimeMillis()}"
                        val content = if(isApprove == "Approved") {
                            """
                            <h1>Your insurance has been cancelled.</h1>
                            <p>Thank you very much!</p>
                            <p>From</p>
                            <p>MyBee Team</p>
                            """.trimIndent()
                        } else {
                            """
                            <h1>Sorry! Your cancel application has been rejected.</h1>
                            <p>Thank you very much!</p>
                            <p>From</p>
                            <p>MyBee Team</p>
                            """.trimIndent()
                        }

                        SimpleEmail().to(email)
                            .subject(subject)
                            .content(content)
                            .isHtml()
                            .send() {
                                hideLoading()
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