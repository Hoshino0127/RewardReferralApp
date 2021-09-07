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
import androidx.core.net.toFile
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.data.Claim
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.data.ReferralInsurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentApplyClaimBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import my.edu.tarc.rewardreferralapp.functions.CheckUser

import java.text.SimpleDateFormat
import java.util.*


class ApplyClaimFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val insuranceRef = database.getReference("Insurance")
    private val referralInsuranceRef = database.getReference("ReferralInsurance")
    private val claimRef = database.getReference("Claim")

    private lateinit var srref : StorageReference

    private lateinit var insurance: Insurance
    private lateinit var referralInsurance: ReferralInsurance
    private lateinit var binding: FragmentApplyClaimBinding
    private var insuranceID: String = ""
    private var referralID: String = ""

    private val myCalendar: Calendar = Calendar.getInstance()
    private lateinit var imgUriMileage: Uri
    private lateinit var imgUriDamage: Uri
    private var fromAction: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apply_claim, container, false)

        val args = ApplyClaimFragmentArgs.fromBundle(requireArguments())
        insuranceID = args.insuranceID
        referralID = args.referralID

        referralInsuranceRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {


                    for (insuranceSnapshot in snapshot.children) {
                        if (insuranceSnapshot.child("insuranceReferral").getValue().toString()
                                .equals(referralID)
                        ) {
                            val insuranceID: String = insuranceSnapshot.child("insuranceID").getValue().toString()
                            val insuranceReferral: String = insuranceSnapshot.child("insuranceReferral").getValue().toString()
                            val insuranceReferralID: String = insuranceSnapshot.child("insuranceReferralID").getValue().toString()
                            val insuranceExpiryDate: Date = Date(insuranceSnapshot.child("insuranceExpiryDate").child("time").getValue() as Long)

                            referralInsurance = ReferralInsurance(insuranceReferralID,insuranceID,insuranceReferral,insuranceExpiryDate)

                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        insuranceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {


                    for (insuranceSnapshot in snapshot.children) {
                        if (insuranceSnapshot.child("insuranceID").getValue().toString()
                                .equals(insuranceID)
                        ) {
                            val insuranceID: String = insuranceSnapshot.child("insuranceID").getValue().toString()
                            val insuranceName: String = insuranceSnapshot.child("insuranceName").getValue().toString()
                            val insuranceComp: String = insuranceSnapshot.child("insuranceComp").getValue().toString()
                            val insurancePlan: String = insuranceSnapshot.child("insurancePlan").getValue().toString()
                            //val insuranceReferral: String = insuranceSnapshot.child("insuranceReferral").getValue().toString()
                            //val insuranceExpiryDate: Date = Date(insuranceSnapshot.child("insuranceExpiryDate").child("time").getValue() as Long)
                            println(insuranceSnapshot.child("insuranceCoverage").getValue().toString())
                            var insuranceCoverage: ArrayList<String> = ArrayList<String>()
                            for(child in insuranceSnapshot.child("insuranceCoverage").children){
                                insuranceCoverage.add(child.getValue().toString())
                                println(child.getValue().toString())
                            }
                            insurance = Insurance(insuranceID,insuranceName,insuranceComp,insurancePlan,insuranceCoverage)

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
            if (errorFree()) {
                if (applyClaim()) {
                    binding.imgDamage.setImageResource(0)
                    binding.imgMileage.setImageResource(0)
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
                binding.tvExpiryDate.text = format.format(referralInsurance.insuranceExpiryDate!!)
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



    private fun errorFree(): Boolean{

        if(binding.dtAccidentDate.text.toString().isEmpty() || binding.dtAccidentTime.text.toString().isEmpty()) {
            Toast.makeText(requireContext(),"Please select the accident date and time",Toast.LENGTH_LONG).show()
            binding.dtAccidentDate.requestFocus()
            return false
        }else{
            val dtString = binding.dtAccidentDate.text.toString() + " " + binding.dtAccidentTime.text.toString()
            if(Date(dtString) > Date()){
                Toast.makeText(requireContext(),"The accident date not be in the future",Toast.LENGTH_LONG).show()
                binding.dtAccidentDate.requestFocus()
                return false
            }
        }

        if(binding.txtAccidentPlace.text.toString().isEmpty()){
            Toast.makeText(requireContext(),"The enter the accident place",Toast.LENGTH_LONG).show()
            return false
        }

        if(!(binding.rbOwnDamage.isChecked) && !(binding.rbTheft.isChecked) && !(binding.rbThirdParty.isChecked)){
            Toast.makeText(requireContext(),"The select the accident type",Toast.LENGTH_LONG).show()
            binding.rgAccidentType.requestFocus()
            return false
        }

        if(binding.txtAccidentDesc.text.toString().isEmpty()){
            Toast.makeText(requireContext(),"The enter the accident description",Toast.LENGTH_LONG).show()
            binding.txtAccidentDesc.requestFocus()
            return false
        }

        if(binding.txtMileage.text.toString().isEmpty()){
            Toast.makeText(requireContext(),"Please enter the current mileage",Toast.LENGTH_LONG).show()
            binding.txtMileage.requestFocus()
            return false
        }else{
            if(!(isNumber(binding.txtMileage.text.toString()))){
                Toast.makeText(requireContext(),"The mileage should be numeric",Toast.LENGTH_LONG).show()
                binding.txtMileage.requestFocus()
                return false
            }
        }

        //check if mileage image is uploaded
        //check if damage image is uploaded
        return true
    }

    private fun applyClaim(): Boolean{
        var newID: String = ""
        var imgMileageName: String = ""

        val dtString = binding.dtAccidentDate.text.toString() + " " + binding.dtAccidentTime.text.toString()

        var accidentType: String = ""
        accidentType = if (binding.rgAccidentType.checkedRadioButtonId == binding.rbOwnDamage.id){
            "OwnDamage"
        }else if(binding.rgAccidentType.checkedRadioButtonId == binding.rbTheft.id){
            "Theft"
        }else{
            "ThirdParty"
        }



        claimRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                newID = if (snapshot.exists()) {

                    "CL" + "%03d".format(snapshot.childrenCount + 1)

                } else {

                    "CL001"

                }

                if(!(imgUriMileage.equals(""))){
                    uploadMileageImg(newID)
                    imgMileageName = "mileage_$newID"
                }



                val claim: Claim = Claim(
                    newID,
                    Date(dtString),
                    binding.txtAccidentPlace.text.toString(),
                    accidentType,
                    binding.txtAccidentDesc.text.toString(),
                    binding.txtMileage.text.toString().toInt(),
                    imgMileageName,
                    "",
                    Date(),
                    null,
                    "Pending",
                    insuranceID,
                    referralID
                )

                claimRef.child(claim.claimID!!).setValue(claim).addOnSuccessListener {
                    println(claim)
                    Toast.makeText(requireContext(),"Claim successful",Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    Toast.makeText(requireContext(),"Claim failed",Toast.LENGTH_LONG).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })

        return true
    }

    var addImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            if(fromAction.equals("Mileage")){
                imgUriMileage  = data?.data!!
                binding.imgMileage.setImageURI(data?.data)
            }else if(fromAction.equals("Damage")){
                imgUriDamage = data?.data!!
                binding.imgDamage.setImageURI(data?.data)
            }

        }
    }

    private fun uploadMileageImg(claimID: String) {
        //imgUriMileage = Uri.parse("android.resource://my.edu.tarc.rewardreferralapp/drawable/ic_base_profile_pic")
        //println("Image uploaded is $imgUriMileage")
        val strMileageImgName: String = ""
        srref = FirebaseStorage.getInstance().getReference("User_"+CheckUser().getCurrentUserUID()).child("mileage_$claimID")
        srref.putFile(imgUriMileage).addOnSuccessListener {
            /*OnSuccessListener<UploadTask.TaskSnapshot> { p0 ->
                if (p0 != null) {
                    p0.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                        
                    }
                }
            }*/
            Toast.makeText(context, "Upload pic successful",
                Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context, "Error fails to upload pic",
                Toast.LENGTH_SHORT).show()
        }

    }

    fun isNumber(s: String): Boolean {
        return when(s.toIntOrNull())
        {
            null -> false
            else -> true
        }
    }

}