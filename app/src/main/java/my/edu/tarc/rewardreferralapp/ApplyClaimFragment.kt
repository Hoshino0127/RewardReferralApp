package my.edu.tarc.rewardreferralapp

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentApplyClaimBinding
import java.text.SimpleDateFormat
import java.util.*


class ApplyClaimFragment : Fragment() {
    //val db = Firebase.firestore
    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val insuranceRef = database.getReference("Insurance")
    private lateinit var insurance: Insurance
    private lateinit var binding: FragmentApplyClaimBinding
    private val myCalendar: Calendar = Calendar.getInstance()

    private var imgUriMileage: Uri? = null

    private var imgUriDamage: Uri? = null

    private var fromAction: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apply_claim, container, false)

        val args = ApplyClaimFragmentArgs.fromBundle(requireArguments())

        insuranceRef.orderByKey().equalTo(args.insuranceID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {


                        for (insuranceSnapshot in snapshot.children) {
                            if (insuranceSnapshot.child("insuranceID").getValue().toString()
                                    .equals(args.insuranceID)
                            ) {
                                val insuranceID: String = insuranceSnapshot.child("insuranceID").getValue().toString()
                                val insuranceName: String = insuranceSnapshot.child("insuranceName").getValue().toString()
                                val insuranceComp: String = insuranceSnapshot.child("insuranceComp").getValue().toString()
                                val insurancePlan: String = insuranceSnapshot.child("insurancePlan").getValue().toString()
                                val insuranceReferral: String = insuranceSnapshot.child("insuranceReferral").getValue().toString()
                                val insuranceExpiryDate: Date = Date(insuranceSnapshot.child("insuranceExpiryDate").child("time").getValue() as Long)
                                println(insuranceSnapshot.child("insuranceCoverage").getValue().toString())
                                var insuranceCoverage: ArrayList<String> = ArrayList<String>()
                                for(child in insuranceSnapshot.child("insuranceCoverage").children){
                                    insuranceCoverage.add(child.getValue().toString())
                                    println(child.getValue().toString())
                                }
                                insurance = Insurance(insuranceID,insuranceName,insuranceComp,insurancePlan,insuranceExpiryDate,insuranceReferral,insuranceCoverage)

                            }
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })



        val datePicker: DatePickerDialog.OnDateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                val myFormat = "dd/MM/yy" //In which you need put here
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.dtAccidentDate.setText(sdf.format(myCalendar.time))
            }

        val timePicker: TimePickerDialog.OnTimeSetListener =
            TimePickerDialog.OnTimeSetListener { view: TimePicker?, hourOfDay: Int, minute: Int ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                val myFormat = "HH:mm"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.dtAccidentTime.setText(sdf.format(myCalendar.time))
            }

        binding.dtAccidentDate.setOnClickListener() {

            val dpd: DatePickerDialog = DatePickerDialog(
                requireContext(),
                datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }

        binding.dtAccidentTime.setOnClickListener() {
            val tpd: TimePickerDialog = TimePickerDialog(
                requireContext(),
                timePicker,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE),
                true
            )
            tpd.show()
        }

        binding.btnApplyClaim.setOnClickListener() {
            if (CheckError()) {
                if (ApplyClaim()) {
                    val action =
                        ApplyClaimFragmentDirections.actionApplyClaimFragmentToApplyClaimSuccessFragment()
                    Navigation.findNavController(it).navigate(action)
                }
            }

        }

        binding.btnUploadMileage.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            fromAction = "Mileage"
            addImage.launch(intent)
        }

        binding.rlInsuranceDetails.setOnClickListener{
            if(binding.view.visibility == View.INVISIBLE){
                val format = SimpleDateFormat("dd/MM/yyyy")
                var strCoverage = ""
                var rowCount = 0
                binding.view.visibility = View.VISIBLE

                binding.tvInsuranceID.text = insurance.insuranceID
                binding.tvInsuranceName.text = insurance.insuranceName
                binding.tvCompTitle.text = insurance.insuranceComp
                binding.tvInsurancePlanName.text = insurance.insurancePlan
                binding.tvExpiryDate.text = format.format(insurance.insuranceExpiryDate!!)
                if(!(insurance.insuranceCoverage.isNullOrEmpty())){
                    for(str:String in insurance.insuranceCoverage!!){
                        strCoverage += str + "\n"
                        rowCount += 1
                    }
                }
                binding.tvInsuranceCoverage.text = strCoverage
                var height = 450 + (binding.tvInsuranceCoverage.height* rowCount)

                val layout: RelativeLayout = binding.rlInsuranceDetails
                val params: ViewGroup.LayoutParams = layout.layoutParams
                params.height = height
                layout.layoutParams = params
                binding.invalidateAll()
            }
        }
        return binding.root
    }

    private fun CheckError(): Boolean{
        return true
    }

    private fun ApplyClaim(): Boolean{
        // Create a new user with a first and last name
        //val user = hashMapOf(
        //    "first" to "Lee",
        //    "last" to "Ken",
        //    "born" to 2000
        //)

        val coverageList: List<String> = listOf(
            "Coverage 1","Coverage 2"
        )

        val insurance: Insurance = Insurance("IN005","Cat insurance","Cat","Plan B", Date("07/04/2023"),"IR001",coverageList)

        /*
        // Add a new document with a generated ID
        db.collection("insurance").document(insurance.InsuranceID.toString())
            .set(insurance, SetOptions.merge())
            .addOnSuccessListener { document ->
                Log.d(TAG, "DocumentSnapshot added successful")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        */



        insuranceRef.child(insurance.insuranceID!!).setValue(insurance).addOnSuccessListener {
            println(insurance)
            Toast.makeText(requireContext(),"Added successful",Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Toast.makeText(requireContext(),"Added failed",Toast.LENGTH_LONG).show()
        }


        return true
    }

    var addImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            if(fromAction.equals("Mileage")){
                imgUriMileage  = data?.data
                binding.imgMileage.setImageURI(data?.data)
            }else if(fromAction.equals("Damage")){
                imgUriDamage = data?.data
                binding.imgDamage.setImageURI(data?.data)
            }

        }
    }


}