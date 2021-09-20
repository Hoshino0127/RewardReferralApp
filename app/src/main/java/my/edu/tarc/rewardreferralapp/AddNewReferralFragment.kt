package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.adapter.ReferralListAdapter
import my.edu.tarc.rewardreferralapp.data.ReferralList
import my.edu.tarc.rewardreferralapp.databinding.FragmentAddNewReferralBinding


class AddNewReferralFragment : Fragment() {

    private lateinit var adapter: ReferralListAdapter
    var selectedRadioButton: String? = null
    private var referralList = ArrayList<ReferralList>()

    private var tempbinding: FragmentAddNewReferralBinding? = null
    private val binding get() = tempbinding!!

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val referralRef = database.getReference("ReferralList")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //load data from firebase
        //loadData()

        tempbinding = FragmentAddNewReferralBinding.inflate(inflater,  container ,false)

        binding.btnReset.setOnClickListener(){
            binding.txtReferName.text.clear()
            binding.txtReferName.requestFocus() //focus on the input text
            binding.rbActive.isChecked = false
            binding.rbInactive.isChecked = false
        }
//
//        binding.btnAdd.setOnClickListener(){
//            val referralName:String = binding.txtReferName.text.toString()
//            val rbInactive: RadioButton = binding.rbInactive.findViewById(R.id.rbInactive)
//            val rbActive: RadioButton = binding.rbActive.findViewById(R.id.rbActive)
//
//            if(rbInactive.isChecked){
//                selectedRadioButton = rbInactive.text.toString()
//            }
//            else if(rbActive.isChecked){
//                selectedRadioButton = rbActive.text.toString()
//            }
//
//            //check empty
//            if (referralName.isNotEmpty() && selectedRadioButton?.isNotEmpty() == true){
//                addData(referralName, selectedRadioButton) //here
//            }else{
//                Toast.makeText(context, "Please fill up all the details.", Toast.LENGTH_LONG).show()
//            }
//        }

        binding.btnBack.setOnClickListener(){
            val action = AddNewReferralFragmentDirections.actionAddNewReferralFragmentToReferralListingFragment()
            Navigation.findNavController(it).navigate(action)
        }
        return binding.root
    }

    //insert
//    private fun addData(referralName: String, referralStatus: String?){
//        var newID: String = ""
//
//        referralRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    newID = "IR" + "%03d".format(snapshot.childrenCount + 1)
//                } else {
//                    newID = "RR0001"
//                }
//                val addReferral = ReferralList(referralName, referralStatus)
//
//                //push to firebase (if success, it will display toast msg, if failed, display error toast)
//                referralRef.push().setValue(addReferral).addOnSuccessListener {
//                    Toast.makeText(context,"Add new referral record successfully!", Toast.LENGTH_LONG).show()
//                    //clear value of the form
//                    binding.txtReferName.text.clear()
//                    binding.rbActive.isChecked
//
//                }.addOnFailureListener {
//                    Toast.makeText(context,"Unable to add the record!", Toast.LENGTH_LONG).show()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(context,"ERROR", Toast.LENGTH_LONG).show()
//            }
//        })
//    }
//
//    private fun loadData(){
//        referralRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists()){
//                    referralList.clear()
//
//                    for(referralSnapshot in snapshot.children){
//                        val referralName: String = referralSnapshot.child("name").value.toString()
//                        val referralStatus: String = referralSnapshot.child("status").value.toString()
//
//                        val referral = ReferralList(referralName, referralStatus)
//                        referralList.add(referral)
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        })
//    }

}