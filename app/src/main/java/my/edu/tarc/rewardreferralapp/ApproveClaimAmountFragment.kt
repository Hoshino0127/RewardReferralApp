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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.adapter.ClaimFigureAdapter
import my.edu.tarc.rewardreferralapp.data.ClaimFigure
import my.edu.tarc.rewardreferralapp.databinding.FragmentApproveClaimAmountBinding

class ApproveClaimAmountFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val claimFigureRef = database.getReference("ClaimFigure")
    private lateinit var claimFigureListener: ValueEventListener

    private lateinit var binding: FragmentApproveClaimAmountBinding
    private var cfList: ArrayList<ClaimFigure> = ArrayList()

    private var claimUUID: String = ""
    private var deductible: Double = 0.0
    private var email: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_approve_claim_amount, container, false)

        val args = ApproveClaimAmountFragmentArgs.fromBundle(requireArguments())
        claimUUID = args.claimUUID
        deductible = args.deductible.toDouble()
        email = args.email

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val action = ApproveClaimAmountFragmentDirections.actionApproveClaimAmountFragmentToAdminClaimListingFragment()
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        binding.btnBackApproveClaimAmount.setOnClickListener(){
            val action = ApproveClaimAmountFragmentDirections.actionApproveClaimAmountFragmentToAdminClaimListingFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.btnAddClaim.setOnClickListener(){
            if(errorFreeAddFigure()){
                val claimFigure: ClaimFigure = ClaimFigure(claimUUID,binding.txtClaimDesc.text.toString(),binding.txtClaimAmount.text.toString().toDouble())
                cfList.add(claimFigure)
                updateClaimFigure()
            }
        }

        binding.btnConfirm.setOnClickListener(){
            addClaimFigure()
        }

        return binding.root
    }

    private fun errorFreeAddFigure(): Boolean{
        if(binding.txtClaimDesc.text.isNullOrEmpty()){
            binding.txtClaimDesc.requestFocus()
            Toast.makeText(requireContext(),"The description should not be empty",Toast.LENGTH_SHORT).show()
            return false
        }

        if(binding.txtClaimAmount.text.isNullOrEmpty()){
            binding.txtClaimAmount.requestFocus()
            Toast.makeText(requireContext(),"The amount should not be empty",Toast.LENGTH_SHORT).show()
            return false
        }else{
            for(cf in cfList){
                if(binding.txtClaimDesc.text.toString().uppercase().equals(cf.claimFigureName.toString().uppercase())){
                    binding.txtClaimDesc.requestFocus()
                    Toast.makeText(requireContext(),"Duplicate figure description found",Toast.LENGTH_SHORT).show()
                    return false
                }
            }

            if(!(isDouble(binding.txtClaimAmount.text.toString()))){
                binding.txtClaimAmount.requestFocus()
                Toast.makeText(requireContext(),"The amount should be in numbers",Toast.LENGTH_SHORT).show()
                return false
            }

            if(binding.txtClaimAmount.text.toString().toDouble() <= 0){
                binding.txtClaimAmount.requestFocus()
                Toast.makeText(requireContext(),"The amount should be more than 0",Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun addClaimFigure(){
        claimFigureListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var successFlag: Boolean = true
                for(cf in cfList){
                    claimFigureRef.push().setValue(cf).addOnFailureListener() {
                        successFlag = false
                    }
                }
                if(successFlag){
                    val action = ApproveClaimAmountFragmentDirections.actionApproveClaimAmountFragmentToApproveAcceptedFragment(claimUUID,email)
                    Navigation.findNavController(requireView()).navigate(action)
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        claimFigureRef.addListenerForSingleValueEvent(claimFigureListener)
    }

    private fun updateClaimFigure(){
        var subtotal: Double = 0.0
        var deductibleAmt: Double = 0.0
        var total: Double = 0.0

        val adapter = ClaimFigureAdapter(requireContext(),true,cfList,ClaimFigureAdapter.RemoveListener{position ->
            cfList.removeAt(position)
            updateClaimFigure()
        })
        binding.lvAmountDetails.adapter = adapter

        //set height
        var totalHeight: Int = 0;
        for (i in 0 until cfList.count()) {
            val listItem: View? = adapter.getView(i,null,binding.lvAmountDetails)
            listItem?.measure(0, 0)
            totalHeight += listItem?.getMeasuredHeight()!!

            subtotal += cfList[i].claimFigureAmount!!
        }
        val params: ViewGroup.LayoutParams = binding.lvAmountDetails.getLayoutParams()
        params.height = totalHeight + (binding.lvAmountDetails.getDividerHeight() * (adapter.getCount() - 1))
        binding.lvAmountDetails.setLayoutParams(params)
        binding.lvAmountDetails.requestLayout()

        deductibleAmt = subtotal * deductible
        total = subtotal - deductibleAmt
        binding.tvSubtotal.text = String.format("%.2f",subtotal)
        binding.tvDeductible.text = String.format("%.2f",deductibleAmt)
        binding.tvTotal.text = String.format("%.2f",total)
        binding.invalidateAll()




    }

    private fun isDouble(s: String): Boolean {
        return when(s.toDoubleOrNull())
        {
            null -> false
            else -> true
        }
    }


}