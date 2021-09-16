package my.edu.tarc.rewardreferralapp

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.helper.MyLottie
import my.edu.tarc.rewardreferralapp.adapter.InsuranceAdapter
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentAddInsuranceBinding
import my.edu.tarc.rewardreferralapp.functions.ResetForm
import java.util.*
import kotlin.collections.ArrayList

class InsuranceAddFragment : Fragment() {

    lateinit var adapter : InsuranceAdapter

    private val database = FirebaseDatabase.getInstance()
    private val insuranceRef = database.getReference("Insurance")

    private lateinit var binding: FragmentAddInsuranceBinding

    private var insuranceList = ArrayList<Insurance>()
    private var insuranceTypeList = ArrayList<String>()

    private var completeDialog: Dialog?= null

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

            if(insuranceComp.isNotEmpty() && insuranceName.isNotEmpty() && insurancePlan.isNotEmpty() && insuranceType.isNotEmpty() && insuranceCoverage.isNotEmpty() && insurancePrice.isNotEmpty()) {
                insertData(insuranceComp,insuranceName,insurancePlan,insuranceType,insuranceCoverage, insurancePrice)
            } else {
                Toast.makeText(requireContext(), "Please fill in the required details!", Toast.LENGTH_LONG).show()
            }

        }

        return binding.root
    }

    private fun insertData(insuranceComp: String, insuranceName: String, insurancePlan: String, insuranceType: String, insuranceCoverage: ArrayList<String>, insurancePrice: String) {

        var newID:String = ""

        insuranceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    newID = UUID.randomUUID().toString()
                } else {
                    newID = UUID.randomUUID().toString()
                }

                val newInsurance = Insurance(
                    insuranceID = newID,
                    insuranceName = insuranceName,
                    insuranceComp = insuranceComp,
                    insurancePlan = insurancePlan,
                    insuranceType = insuranceType,
                    insuranceCoverage = insuranceCoverage,
                    insurancePrice = insurancePrice.toDouble()
                )

                insuranceRef.push().setValue(newInsurance).addOnSuccessListener(){
                    Toast.makeText(context, "Add successful", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    Toast.makeText(context, "Add unsuccessful", Toast.LENGTH_LONG).show()
                }
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

                        val insurance = Insurance(insuranceID = insuranceID,
                            insuranceName = insuranceName,
                            insuranceComp = insuranceComp,
                            insurancePlan = insurancePlan,
                            insuranceType = insuranceType,
                            insuranceCoverage = insuranceCoverage
                        )

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
        completeDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        hideLoading()
        completeDialog = MyLottie.showCompleteDialog(requireContext())
    }
}