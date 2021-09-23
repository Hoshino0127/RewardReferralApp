package my.edu.tarc.rewardreferralapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.rewardreferralapp.helper.MyLottie
import my.edu.tarc.rewardreferralapp.adapter.InsuranceAdapter
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentAddInsuranceBinding
import my.edu.tarc.rewardreferralapp.functions.ResetForm
import java.util.*
import kotlin.collections.ArrayList

class InsuranceAddFragment : Fragment() {

    private lateinit var adapter : InsuranceAdapter

    private val database = FirebaseDatabase.getInstance()
    private val insuranceRef = database.getReference("Insurance")

    private lateinit var binding: FragmentAddInsuranceBinding

    private var insuranceList = ArrayList<Insurance>()
    private var insuranceTypeList = ArrayList<String>()

    private var loadingDialog: Dialog?= null
    private var completeDialog: Dialog?= null
    private var redirectDialog : Dialog?= null

    private var imgUriReward: Uri = Uri.EMPTY

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadData()

        binding = FragmentAddInsuranceBinding.inflate(inflater,  container ,false)

        binding.tfAddInsurancePrice.addDecimalLimiter(2)

        val insuranceComp = resources.getStringArray(R.array.insurance_comp)
        val insuranceCompAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, insuranceComp)
        binding.tfAddInsuranceComp.setAdapter(insuranceCompAdapter)

        binding.tfAddInsuranceComp

        binding.btnBackAddInsurance.setOnClickListener() {
            val action = InsuranceAddFragmentDirections.actionInsuranceAddFragmentToListInsuranceFragment()
            Navigation.findNavController(it).navigate(action)   
        }

        binding.btnResetAddInsurance.setOnClickListener(){
            ResetForm().resetAllField(view as ViewGroup)
        }

        binding.btnAddAddInsurance.setOnClickListener() {

            val insuranceComp:String = binding.tfAddInsuranceComp.text.toString()
            val insuranceName:String = binding.tfAddInsuranceName.text.toString()
            val insurancePlan:String = binding.tfAddInsurancePlan.text.toString()
            val insuranceType:String = binding.ddlAddInsuranceType.selectedItem.toString()
            val insurancePrice: String = binding.tfAddInsurancePrice.text.toString()
            var insuranceCoverage: ArrayList<String> = ArrayList()

            if(binding.cbBodyInjury.isChecked){
                insuranceCoverage.add(binding.cbBodyInjury.text.toString())
            }
            if(binding.cbInsured.isChecked){
                insuranceCoverage.add(binding.cbInsured.text.toString())
            }
            if(binding.cbMedical.isChecked){
                insuranceCoverage.add(binding.cbMedical.text.toString())
            }
            if(binding.cbPropertyDmg.isChecked){
                insuranceCoverage.add(binding.cbPropertyDmg.text.toString())
            }

            var newID: String = UUID.randomUUID().toString()

            var imageName = "insImg_$newID"

            if(imgUriReward == Uri.EMPTY){
                imageName = "default-img.jpg"
            } else {
                insertRewardImg(imageName)
            }


            if (checkError()) {
                insertData(
                    insuranceComp,
                    insuranceName,
                    insurancePlan,
                    insuranceType,
                    insuranceCoverage,
                    insurancePrice,
                    imageName
                )
            }


        }

        var addImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data

                    imgUriReward = data?.data!!
                    binding.imgAddInsurancePhoto.setImageURI(data?.data)

                }
            }

        binding.btnUpload.setOnClickListener() {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            addImage.launch(intent)

        }

        return binding.root
    }

    private fun insertData(
        insuranceComp: String,
        insuranceName: String,
        insurancePlan: String,
        insuranceType: String,
        insuranceCoverage: ArrayList<String>,
        insurancePrice: String,
        insuranceImg: String
    ) {

        showLoading()
        var newID:String = ""

        insuranceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    newID = UUID.randomUUID().toString()
                } else {
                    newID = UUID.randomUUID().toString()
                }

                val newInsurance = Insurance(newID, insuranceName, insuranceComp, insurancePlan, insuranceCoverage, insurancePrice.toDouble(), insuranceType, insuranceImg)

                Handler().postDelayed({
                    hideLoading()
                    insuranceRef.push().setValue(newInsurance).addOnSuccessListener(){
                        Toast.makeText(context, "Add successfully", Toast.LENGTH_LONG).show()
                    }.addOnFailureListener {
                        Toast.makeText(context, "Add unsuccessful", Toast.LENGTH_LONG).show()
                    }
                    showRedirect()
                    Handler().postDelayed({
                        hideRedirect()
                        val action = InsuranceAddFragmentDirections.actionInsuranceAddFragmentToListInsuranceFragment()
                        Navigation.findNavController(requireView()).navigate(action)
                    }, 2000)

                }, 3000)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun loadData() {
        insuranceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    insuranceList.clear()
                    for (insuranceSnapshot in snapshot.children){

                        val insuranceID: String = insuranceSnapshot.child("insuranceID").value.toString()
                        val insuranceName: String = insuranceSnapshot.child("insuranceName").value.toString()
                        val insuranceComp: String = insuranceSnapshot.child("insuranceComp").value.toString()
                        val insurancePlan: String = insuranceSnapshot.child("insurancePlan").value.toString()
                        val insuranceType: String = insuranceSnapshot.child("insuranceType").value.toString()
                        var insuranceCoverage: ArrayList<String> = ArrayList<String>()
                        for(child in insuranceSnapshot.child("insuranceCoverage").children){
                            insuranceCoverage.add(child.value.toString())
                        }
                        val insurancePrice: String = insuranceSnapshot.child("insurancePrice").value.toString()

                        val insurance = Insurance(insuranceID,insuranceName,insuranceComp,insurancePlan,insuranceCoverage,insurancePrice.toDouble(),insuranceType)

                        insuranceList.add(insurance)
                    }

                    /*
                    for(ds in insuranceList) {
                        if(!insuranceTypeList.contains(ds.insuranceType)) {
                            ds.insuranceType?.let { insuranceTypeList.add(it) }
                        }
                    }

                    val spinner: Spinner = binding.ddlAddInsuranceType

                    val adapterIns: ArrayAdapter<String> =
                        ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, insuranceTypeList)
                    adapterIns.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    spinner.adapter = adapterIns
                    adapterIns.notifyDataSetChanged()

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val text = parent?.selectedItem.toString()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }
                    */

                } else {
                    insuranceList.clear()
                    insuranceTypeList.clear()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun insertRewardImg(imageName: String) {
        var imgref: StorageReference =
            FirebaseStorage.getInstance().getReference("InsuranceStorage").child(imageName)
        imgref.putFile(imgUriReward).addOnSuccessListener {

        }
    }

    fun EditText.addDecimalLimiter(maxLimit: Int = 2) {

        this.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val str = this@addDecimalLimiter.text!!.toString()
                if (str.isEmpty()) return
                val str2 = decimalLimiter(str, maxLimit)

                if (str2 != str) {
                    this@addDecimalLimiter.setText(str2)
                    val pos = this@addDecimalLimiter.text!!.length
                    this@addDecimalLimiter.setSelection(pos)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    fun decimalLimiter(string: String, MAX_DECIMAL: Int): String {

        var str = string
        if (str[0] == '.') str = "0$str"
        val max = str.length

        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char

        val decimalCount = str.count { ".".contains(it) }

        if (decimalCount > 1)
            return str.dropLast(1)

        while (i < max) {
            t = str[i]
            if (t != '.' && !after) {
                up++
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL)
                    return rFinal
            }
            rFinal += t
            i++
        }
        return rFinal
    }

    private fun hideLoading() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        hideLoading()
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }

    private fun hideComplete() {
        completeDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showComplete() {
        hideComplete()
        completeDialog = MyLottie.showCompleteDialog(requireContext())
    }

    private fun hideRedirect() {
        redirectDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showRedirect() {
        hideLoading()
        redirectDialog = MyLottie.showRedirectingDialog(requireContext())
    }

    private fun checkError() : Boolean {

        if (binding.tfAddInsuranceComp.text.isNullOrEmpty()) {
            binding.tfAddInsuranceComp.error = "Please enter a company name."
            return false
        }

        if (binding.tfAddInsuranceName.text.isNullOrEmpty()) {
            binding.tfAddInsuranceName.error = "Please enter a insurance name."
            return false
        }

        if (binding.tfAddInsurancePrice.text.isNullOrEmpty()) {
            binding.tfAddInsurancePrice.error = "Please enter a price."
            return false
        }

        if (binding.tfAddInsurancePlan.text.isNullOrEmpty()) {
            binding.tfAddInsurancePlan.error = "Please enter a plan name."
            return false
        }

        if (!binding.cbBodyInjury.isChecked && !binding.cbInsured.isChecked && !binding.cbMedical.isChecked && !binding.cbPropertyDmg.isChecked) {
            Toast.makeText(requireContext(), "Please check at least one insurance coverage.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }



}