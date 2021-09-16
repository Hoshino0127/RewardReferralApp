package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.*
import my.edu.tarc.rewardreferralapp.adapter.ReferralTransferAdapter
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.data.ReferralTransfer
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferralTransferListingBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser


class ReferralTransferListingFragment : Fragment() {

    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val refRef = database.getReference("Referral")
    private val refTransferRef = database.getReference("ReferralTransfer")
    private var refferalUID = CheckUser().getCurrentUserUID()

    val refTransferList = ArrayList<ReferralTransfer>()
    var refList = ArrayList<Referral>()

    private lateinit var binding:FragmentReferralTransferListingBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        refTransferList.clear()
        refList.clear()

        getTransferDetails()

        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_referral_transfer_listing,
                container,
                false
            )



        return binding.root
    }

    private fun getTransferDetails(){

        var qryRefOwnTransfer: Query = refTransferRef.orderByChild("referralDonorID").equalTo(refferalUID)

        qryRefOwnTransfer.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(refTransSnap in snapshot.children){
                        refTransferList.add(refTransSnap.getValue(ReferralTransfer::class.java)!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })


        var qryRefOtherTransfer: Query = refTransferRef.orderByChild("referralRecipientID").equalTo(refferalUID)

        qryRefOtherTransfer.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(refTransSnap in snapshot.children){
                        refTransferList.add(refTransSnap.getValue(ReferralTransfer::class.java)!!)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })

        refRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(refSnap in snapshot.children){
                        refList.add(refSnap.getValue(Referral::class.java)!!)
                    }
                    setAdapter()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun setAdapter(){
        refTransferList.sortByDescending { t -> t.transferID }
        val transferAdapter = ReferralTransferAdapter(refferalUID!!,refTransferList,refList)
        binding.RTLTransferRV.adapter = transferAdapter
        binding.RTLTransferRV.setHasFixedSize(true)

    }


}