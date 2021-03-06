package my.edu.tarc.rewardreferralapp

import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.rewardreferralapp.helper.MyLottie
import my.edu.tarc.rewardreferralapp.adapter.GetEvidenceAdapter
import my.edu.tarc.rewardreferralapp.databinding.FragmentViewInsuranceApplicationCustBinding
import my.edu.tarc.rewardreferralapp.data.File
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.data.InsuranceApplication
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import java.util.*
import kotlin.collections.ArrayList

class ViewInsuranceApplicationCustFragment : Fragment() {

    private lateinit var binding: FragmentViewInsuranceApplicationCustBinding

    private var fileNameList = ArrayList<File>()
    private lateinit var fileAdapter : GetEvidenceAdapter

    private lateinit var referralUID: String

    private val args: ViewInsuranceApplicationCustFragmentArgs by navArgs()

    private lateinit var progressDownloadDialog : ProgressDialog
    private var completeDialog: Dialog?= null
    private var loadingDialog: Dialog?= null
    private var redirectDialog: Dialog?= null

    private val database = FirebaseDatabase.getInstance()
    private val insuranceRef = database.getReference("Insurance")
    private var insuranceCustList = ArrayList<Insurance>()
    private val insuranceApplicationRef = database.getReference("InsuranceApplication")
    private var insuranceAppList = ArrayList<InsuranceApplication>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_view_insurance_application_cust, container, false)

        progressDownloadDialog = ProgressDialog(this.context)
        progressDownloadDialog.setMessage("Downloading the file..")
        progressDownloadDialog.setCancelable(false)
        progressDownloadDialog.setCanceledOnTouchOutside(false)

        loadData(args.insuranceID.toString())

        referralUID = CheckUser().getCurrentUserUID().toString()
        val storageFile = FirebaseStorage.getInstance().getReference("EvidencesInsuranceApplication").child("User_$referralUID").child(args.applicationID.toString())
        storageFile.listAll().addOnSuccessListener { it ->
            for(item in it.items){
                item.downloadUrl.addOnSuccessListener {
                    val uri = it
                    val name = getFileName(uri!!)
                    val file = File(FileName = name, FileUri = uri)
                    fileNameList.add(file)
                    bindFiles()
                }
            }
        }

        binding.imgBackViewInsuranceApplicationCust.setOnClickListener() {
            val action = ViewInsuranceApplicationCustFragmentDirections.actionViewInsuranceApplicationCustFragmentToListInsuranceApplicationCustViewFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnBackViewInsuranceApplicationCust.setOnClickListener() {
            val action = ViewInsuranceApplicationCustFragmentDirections.actionViewInsuranceApplicationCustFragmentToListInsuranceApplicationCustViewFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnCancelViewInsuranceApplicationCust.setOnClickListener() {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("MyBee Alert")
            builder.setMessage("Are you sure to cancel the application?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            //performing positive action
            builder.setPositiveButton("Yes"){dialogInterface, which ->
                showLoading()
                Handler().postDelayed({
                    hideLoading()
                    updateApplication("Cancelled")
                    showRedirect()
                    Handler().postDelayed({
                        hideRedirect()
                        val action = ViewInsuranceApplicationCustFragmentDirections.actionViewInsuranceApplicationCustFragmentToListInsuranceApplicationCustViewFragment()
                        Navigation.findNavController(it).navigate(action)
                    }, 2000)
                }, 2000)
            }

            //performing negative action
            builder.setNegativeButton("No"){dialogInterface, which ->

            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()

        }

        return binding.root
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
                            val insuranceImg: String =
                                insuranceSnapshot.child("insuranceImg").value.toString()

                            val insurance = Insurance(
                                insuranceID,
                                insuranceName,
                                insuranceComp,
                                insurancePlan,
                                insuranceCoverage,
                                insurancePrice.toDouble(),
                                insuranceType,
                                insuranceImg
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
                                insuranceAppSnapshot.child("carNoPlate").value.toString()
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

                    if(insAppList.applicationStatus == "Cancelled") {
                        binding.btnCancelViewInsuranceApplicationCust.isEnabled = false
                        binding.btnCancelViewInsuranceApplicationCust.visibility = View.GONE
                        binding.tvApplicationCancelled.visibility = View.VISIBLE
                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun updateApplication(isApprove : String) {
        insuranceApplicationRef.orderByChild("applicationID").equalTo(args.applicationID.toString()).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    if (ds.exists()){
                        ds.key?.let {
                            insuranceApplicationRef.child(it).child("applicationStatus").setValue(isApprove)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Cancelled Successful",
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
        showComplete()
        progressDownloadDialog.show()

        var total: Long

        val storageFile = FirebaseStorage.getInstance().getReference("EvidencesInsuranceApplication").child("User_$referralUID").child(args.applicationID.toString())
            .child(file.FileName.toString())

        storageFile.metadata.addOnSuccessListener  {

            total = it.sizeBytes

            Handler().postDelayed({
                hideComplete()
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

    private fun hideComplete() {
        completeDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showComplete() {
        hideComplete()
        completeDialog = MyLottie.showCompleteDialog(requireContext())
    }

    private fun hideLoading() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        hideComplete()
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }

    private fun hideRedirect() {
        redirectDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showRedirect() {
        hideComplete()
        redirectDialog = MyLottie.showRedirectingDialog(requireContext())
    }

}