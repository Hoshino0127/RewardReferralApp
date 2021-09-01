package my.edu.tarc.rewardreferralapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentApplyClaimBinding
import java.text.SimpleDateFormat
import java.util.*

class ApplyClaimFragment : Fragment() {
    val db = Firebase.firestore

    val myCalendar: Calendar = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentApplyClaimBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_apply_claim, container, false)

        val args = ApplyClaimFragmentArgs.fromBundle(requireArguments())
        binding.tvInsuranceID.text = args.insuranceID

        val datePicker: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            val myFormat = "dd/MM/yy" //In which you need put here
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.dtAccidentDate.setText(sdf.format(myCalendar.time))
        }

        val timePicker: TimePickerDialog.OnTimeSetListener = TimePickerDialog.OnTimeSetListener{view: TimePicker?, hourOfDay: Int, minute: Int ->
            myCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
            myCalendar.set(Calendar.MINUTE,minute);
            val myFormat = "HH:mm"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.dtAccidentTime.setText(sdf.format(myCalendar.time))
        }

        binding.dtAccidentDate.setOnClickListener(){

            val dpd:DatePickerDialog = DatePickerDialog(requireContext(),datePicker,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH))
            dpd.show()
        }

        binding.dtAccidentTime.setOnClickListener(){
            val tpd:TimePickerDialog = TimePickerDialog(requireContext(),timePicker,myCalendar.get(Calendar.HOUR_OF_DAY),myCalendar.get(Calendar.MINUTE),true)
            tpd.show()
        }

        binding.btnApplyClaim.setOnClickListener(){
            if(CheckError()){
                if(ApplyClaim()) {
                    val action = ApplyClaimFragmentDirections.actionApplyClaimFragmentToApplyClaimSuccessFragment()
                    Navigation.findNavController(it).navigate(action)
                }
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

        val insurance: Insurance = Insurance("IN001","Car insurance","Etiqa","Plan A", SimpleDateFormat("dd/MM/yyyy").parse("22/08/2022"),"IR001")

        // Add a new document with a generated ID
        db.collection("insurance").document(insurance.InsuranceID.toString())
            .set(insurance, SetOptions.merge())
            .addOnSuccessListener { document ->
                Log.d(TAG, "DocumentSnapshot added successful")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        return true
    }


}