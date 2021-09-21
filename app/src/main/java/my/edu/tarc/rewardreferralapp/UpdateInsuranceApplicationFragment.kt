package my.edu.tarc.rewardreferralapp

import android.app.Dialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.OpenableColumns
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.rewardreferralapp.helper.MyLottie
import my.edu.tarc.rewardreferralapp.adapter.GetEvidenceAdapter
import my.edu.tarc.rewardreferralapp.data.*
import my.edu.tarc.rewardreferralapp.databinding.FragmentUpdateInsuranceApplicationBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import my.edu.tarc.rewardreferralapp.utils.SimpleEmail
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UpdateInsuranceApplicationFragment : Fragment() {

    private lateinit var binding: FragmentUpdateInsuranceApplicationBinding
    private var fileNameList = ArrayList<File>()
    private lateinit var fileAdapter : GetEvidenceAdapter

    private var referralUID: String = ""

    private val args: UpdateInsuranceApplicationFragmentArgs by navArgs()

    private lateinit var progressDownloadDialog : ProgressDialog
    private var completeDialog: Dialog?= null

    private val database = FirebaseDatabase.getInstance()
    private val insuranceRef = database.getReference("Insurance")
    private var insuranceCustList = ArrayList<Insurance>()
    private val insuranceApplicationRef = database.getReference("InsuranceApplication")
    private var insuranceAppList = ArrayList<InsuranceApplication>()

    private val referralRef = database.getReference("Referral")
    private var referralList = ArrayList<Referral>()

    private val referralInsuranceRef = database.getReference("ReferralInsurance")
    private var referralInsuranceList = ArrayList<ReferralInsurance>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_update_insurance_application, container, false)

        progressDownloadDialog = ProgressDialog(this.context)
        progressDownloadDialog.setMessage("Downloading the file..")
        progressDownloadDialog.setCancelable(false)
        progressDownloadDialog.setCanceledOnTouchOutside(false)

        loadData(args.insuranceID.toString())

        binding.btnBackUpdateInsuranceApplication.setOnClickListener() {
            val action = UpdateInsuranceApplicationFragmentDirections.actionUpdateInsuranceApplicationFragmentToListInsuranceApplicationFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnAcceptApplication.setOnClickListener() {
            updateApplication("Accepted")
        }

        binding.btnRejectApplication.setOnClickListener() {
            updateApplication("Rejected")
        }

        return binding.root
    }

    private fun loadFileList(tempReferralUID: String) {
        referralUID = tempReferralUID
        val storageFile =
            FirebaseStorage.getInstance().getReference("EvidencesInsuranceApplication")
                .child("User_${referralUID}").child(args.applicationID.toString())
        storageFile.listAll().addOnSuccessListener { it ->
            for (item in it.items) {
                item.downloadUrl.addOnSuccessListener {
                    val uri = it
                    val name = getFileName(uri!!)
                    val file = File(FileName = name, FileUri = uri)
                    fileNameList.add(file)
                    bindFiles()
                }
            }
        }
    }

    private fun loadData(insuranceID: String) {
        insuranceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    insuranceCustList.clear()
                    for (insuranceSnapshot in snapshot.children){
                        if(insuranceSnapshot.child("insuranceID").value.toString() == insuranceID) {
                            val insuranceName: String =
                                insuranceSnapshot.child("insuranceName").value.toString()
                            val insuranceComp: String =
                                insuranceSnapshot.child("insuranceComp").value.toString()
                            val insurancePlan: String =
                                insuranceSnapshot.child("insurancePlan").value.toString()
                            val insuranceType: String =
                                insuranceSnapshot.child("insuranceType").value.toString()
                            val insuranceCoverage: ArrayList<String> = ArrayList<String>()
                            for (child in insuranceSnapshot.child("insuranceCoverage").children) {
                                insuranceCoverage.add(child.value.toString())
                            }
                            val insurancePrice: String =
                                insuranceSnapshot.child("insurancePrice").value.toString()

                            val insurance = Insurance(
                                insuranceID,
                                insuranceName,
                                insuranceComp,
                                insurancePlan,
                                insuranceCoverage,
                                insurancePrice.toDouble(),
                                insuranceType
                            )

                            insuranceCustList.add(insurance)
                        }
                    }

                    for (insCustList in insuranceCustList) {
                        binding.tvCustInsuranceName.text = insCustList.insuranceName
                        binding.tvCustInsuranceComp.text = insCustList.insuranceComp
                        binding.tvCustInsurancePlan.text = insCustList.insurancePlan
                        binding.tvCustInsuranceType.text = insCustList.insuranceType

                        var strCover : String? = ""
                        val lastCover : String? = insCustList.insuranceCoverage!!.lastOrNull()
                        for (insCover in insCustList.insuranceCoverage) {
                            strCover += if(lastCover.equals(insCover)) {
                                "$insCover\n"
                            } else
                                "$insCover,\n"
                        }

                        binding.tvCustInsuranceCoverage.text = strCover
                    }

                } else {
                    insuranceCustList.clear()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        insuranceApplicationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    insuranceAppList.clear()
                    for (insuranceAppSnapshot in snapshot.children) {
                        if (insuranceAppSnapshot.child("applicationID").value.toString() == args.applicationID.toString()) {

                            val referralID : String =
                                insuranceAppSnapshot.child("referralID").value.toString()
                            val appliedDate : Date =
                                Date(insuranceAppSnapshot.child("applicationAppliedDate").child("time").value as Long)
                            val applicationStatus : String =
                                insuranceAppSnapshot.child("applicationStatus").value.toString()
                            val carNoPlate : String =
                                insuranceAppSnapshot.child("applicationStatus").value.toString()
                            val yearMake : String =
                                insuranceAppSnapshot.child("yearMake").value.toString()
                            val modelName : String =
                                insuranceAppSnapshot.child("modelName").value.toString()
                            val mileage : String =
                                insuranceAppSnapshot.child("annualMileage").value.toString()
                            val usage : String =
                                insuranceAppSnapshot.child("usage").value.toString()
                            val airBag : String =
                                insuranceAppSnapshot.child("airBag").value.toString()
                            val antiLockBrake : String =
                                insuranceAppSnapshot.child("antiLockBrake").value.toString()
                            val evidences : String =
                                insuranceAppSnapshot.child("evidences").value.toString()

                            val newInsApp = InsuranceApplication(
                                args.applicationID.toString(),
                                args.insuranceID.toString(),
                                referralID,
                                appliedDate,
                                applicationStatus,
                                evidences.toBoolean(),
                                carNoPlate,
                                yearMake,
                                modelName,
                                mileage,
                                usage,
                                airBag,
                                antiLockBrake
                            )

                            insuranceAppList.add(newInsApp)
                        }
                    }
                }


                for(insAppList in insuranceAppList) {
                    loadFileList(insAppList.referralID.toString())
                    binding.tfCarNoPlate.setText(insAppList.carNoPlate.toString())
                    binding.tfMileage.setText(insAppList.annualMileage.toString())
                    binding.tfModelName.setText(insAppList.modelName.toString())
                    binding.tfYearMake.setText(insAppList.yearMake.toString())

                    if(insAppList.usage.equals(binding.radUsagePersonal.text.toString()))
                        binding.radUsagePersonal.toggle()
                    else if (insAppList.usage.equals(binding.radUsageBusiness.text.toString()))
                        binding.radUsageBusiness.toggle()
                    else if (insAppList.usage.equals(binding.radUsageCarpool.text.toString()))
                        binding.radUsageCarpool.toggle()
                    else if (insAppList.usage.equals(binding.radUsageOther.text.toString()))
                        binding.radUsageOther.toggle()

                    if(insAppList.antiLockBrake.equals(binding.radAntiLockBrakeOther.text.toString()))
                        binding.radAntiLockBrakeOther.toggle()
                    else if (insAppList.antiLockBrake.equals(binding.radAntiLockBrakeFourWheel.text.toString()))
                        binding.radAntiLockBrakeFourWheel.toggle()
                    else if (insAppList.antiLockBrake.equals(binding.radAntiLockBrakeFourWheelStandard.text.toString()))
                        binding.radAntiLockBrakeFourWheelStandard.toggle()
                    else if (insAppList.antiLockBrake.equals(binding.radAntiLockBrakeNone.text.toString()))
                        binding.radAntiLockBrakeNone.toggle()

                    if(insAppList.airBag.equals(binding.radAirBagDriver.text.toString()))
                        binding.radAirBagDriver.toggle()
                    else if (insAppList.airBag.equals(binding.radAirBagDriverPassenger.text.toString()))
                        binding.radAirBagDriverPassenger.toggle()
                    else if (insAppList.airBag.equals(binding.radAirBagNone.text.toString()))
                        binding.radAirBagNone.toggle()

                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun updateApplication(isApprove : String) {
        insuranceApplicationRef.orderByChild("applicationID").equalTo(args.applicationID.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    if (ds.exists()){
                        ds.key?.let {
                            insuranceApplicationRef.child(it).child("applicationStatus").setValue(isApprove)
                                .addOnSuccessListener {
                                    send(isApprove)
                                    Toast.makeText(
                                        context,
                                        "Update Successful",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        var newID:String = ""

        referralInsuranceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val rnd: Random = Random()
                var IDFormat = SimpleDateFormat("ddMMyyHHMMSS", Locale.US)
                var newID = "CL" + IDFormat.format(Date()) + "_" + String.format("%04d",rnd.nextInt(9999))

                val calendar = Calendar.getInstance()

                calendar.time = Date()
                calendar.add(Calendar.YEAR, 1)

                val insuranceExpiryDate = calendar.time

                val newInsurance = ReferralInsurance(newID, args.insuranceID.toString(), CheckUser().getCurrentUserUID(), insuranceExpiryDate, "Active")

                referralInsuranceRef.child(newID).setValue(newInsurance).addOnSuccessListener(){
                    Toast.makeText(context, "Update successful. Redirecting you back to the listings...", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        val action = UpdateInsuranceApplicationFragmentDirections.actionUpdateInsuranceApplicationFragmentToListInsuranceApplicationFragment()
                        Navigation.findNavController(requireView()).navigate(action)
                    }, 2000)
                }.addOnFailureListener {
                    Toast.makeText(context, "Unable to update", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }

        })



    }

    private fun bindFiles(){
        fileAdapter = GetEvidenceAdapter(fileNameList, GetEvidenceAdapter.DownloadListener {
                File -> view

            try {
                for (i in 0 until fileNameList.size) {
                    if (File.FileName.equals(fileNameList[i].FileName)) {
                        Toast.makeText(context, "File ${File.FileName} is downloading...", Toast.LENGTH_SHORT).show()
                        downloadFile(fileNameList[i])
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        })

        binding.rvEvidenceListGetEvidence.setHasFixedSize(true)
        binding.rvEvidenceListGetEvidence.adapter = fileAdapter
        fileAdapter.notifyDataSetChanged()
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context?.contentResolver?.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun  downloadFile(file: File) {
        showLoading()
        progressDownloadDialog.show()

        var total: Long

        val storageFile = FirebaseStorage.getInstance().getReference("EvidencesInsuranceApplication").child("User_$referralUID").child(args.applicationID.toString())
            .child(file.FileName.toString())

        storageFile.metadata.addOnSuccessListener  {

            total = it.sizeBytes

            Handler().postDelayed({
                hideLoading()
            }, (total/100)*2)

            val handler = Handler()

            val progressRunnable = Runnable {
                val request = DownloadManager.Request(file.FileUri)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // Visibility of the download Notification
                    .setTitle(file.FileName) // Title of the Download Notification
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file.FileName)
                    .setDescription("From ") // Description of the Download Notification
                    .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
                    .setAllowedOverRoaming(true) // Set if download is allowed on roaming network

                val downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
                downloadManager!!.enqueue(request) // enqueue puts the download request in the queue. val downloadID = downloadManager!!.enqueue(request)
                progressDownloadDialog.dismiss()
                Toast.makeText(context, "Download Complete.", Toast.LENGTH_SHORT).show()
            }

            handler.postDelayed(progressRunnable, total/100)

        }

    }

    private fun hideLoading() {
        completeDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        hideLoading()
        completeDialog = MyLottie.showCompleteDialog(requireContext())
    }

    // TODO: Compose and send email
    private fun send(isApprove: String) {
        hideKeyboard()

        referralRef.orderByChild("referralUID").equalTo(referralUID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    if(ds.exists()) {
                        val email = ds.child("email").value.toString().trim()
                        val subject = "Your application has been $isApprove - ${System.currentTimeMillis()}"
                        val content = if(isApprove == "Accepted") {
                            """
                            <h1>Thanks you for subscribing to this insurance</h1>
                            <p>Thank you very much!</p>
                            <p>From</p>
                            <p>MyBee Team</p>
                            """.trimIndent()
                        } else {
                            """
                            <h1>Sorry for your application has been rejected!</h1>
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

    // TODO: Validate if email address valid
    private fun isEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()


    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun snackbar(text: String) {
        Snackbar.make(requireView(), text, Snackbar.LENGTH_SHORT).show()
    }

}
