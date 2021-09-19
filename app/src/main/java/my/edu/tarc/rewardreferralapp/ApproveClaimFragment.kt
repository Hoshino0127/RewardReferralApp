package my.edu.tarc.rewardreferralapp

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.rewardreferralapp.adapter.ClaimFigureAdapter
import my.edu.tarc.rewardreferralapp.data.Claim
import my.edu.tarc.rewardreferralapp.data.ClaimFigure
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.databinding.FragmentApproveClaimBinding
import my.edu.tarc.rewardreferralapp.dialog.ImageDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ApproveClaimFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val claimRef = database.getReference("Claim")
    private val referralRef = database.getReference("Referral")
    private val insuranceRef = database.getReference("Insurance")
    private val claimFigureRef = database.getReference("ClaimFigure")

    private lateinit var claimListener: ValueEventListener
    private lateinit var referralListener: ValueEventListener
    private lateinit var insuranceListener: ValueEventListener
    private lateinit var claimFigureListener: ValueEventListener

    private lateinit var imgStorageRef : StorageReference

    private lateinit var claimUUID: String
    private lateinit var referralUID: String
    private lateinit var binding: FragmentApproveClaimBinding

    private var referral: Referral = Referral()
    private var claim: Claim = Claim()
    private var insurance: Insurance = Insurance()
    private var cfList: ArrayList<ClaimFigure> = ArrayList()

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap

    private var isExpanded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_approve_claim, container, false)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    DetachListener()
                    val action = ApproveClaimFragmentDirections.actionApproveClaimFragmentToAdminClaimListingFragment()
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        val args = ApproveClaimFragmentArgs.fromBundle(requireArguments())
        claimUUID = args.claimUUID
        referralUID = args.referralUID

        referralListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(referralSS in snapshot.children){
                        if(referralSS.child("referralUID").getValue().toString().equals(referralUID)){
                            val referralUID: String = referralSS.child("referralUID").getValue().toString()
                            val deductible: Double = referralSS.child("deductible").getValue().toString().toDouble()
                            val fullName: String = referralSS.child("fullName").getValue().toString()
                            val contactNo: String = referralSS.child("contactNo").getValue().toString()
                            val email: String = referralSS.child("email").getValue().toString()
                            referral = Referral(
                                referralUID = referralUID,
                                deductible = deductible,
                                fullName = fullName,
                                contactNo = contactNo,
                                email = email
                            )
                            println(referral)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        claimListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(currentClaim in snapshot.children){
                        Log.v(ContentValues.TAG,currentClaim.child("claimUUID").getValue().toString())
                        if(currentClaim.child("claimUUID").getValue().toString().equals(claimUUID)){
                            val claimUUID = currentClaim.child("claimUUID").getValue().toString()
                            val claimID = currentClaim.child("claimID").getValue().toString()
                            val dt = Date(currentClaim.child("accidentDateTime").child("time").getValue() as Long)
                            val accidentPlaceLat = currentClaim.child("accidentPlace").child("latitude").getValue() as Double
                            val accidentPlaceLong = currentClaim.child("accidentPlace").child("longitude").getValue() as Double
                            val accidentPlace: LatLng = LatLng(accidentPlaceLat,accidentPlaceLong)
                            val accidentType = currentClaim.child("accidentType").getValue().toString()
                            val accidentDesc = currentClaim.child("accidentDesc").getValue().toString()
                            val mileage = currentClaim.child("mileage").getValue().toString().toInt()
                            val imgMileage = currentClaim.child("imgMileage").getValue().toString()
                            val imgDamage = currentClaim.child("imgDamage").getValue().toString()
                            val applyDateTime: Date = Date(currentClaim.child("applyDateTime").child("time").getValue() as Long)
                            val approveDateTime: Date? = if(currentClaim.child("approveDateTime").getValue() == null){
                                null
                            }else{
                                Date(currentClaim.child("approveDateTime").child("time").getValue() as Long)
                            }
                            val claimStatus = currentClaim.child("claimStatus").getValue().toString()
                            val insuranceID = currentClaim.child("insuranceID").getValue().toString()
                            val referralUID = currentClaim.child("referralUID").getValue().toString()
                            claim = Claim(claimUUID,claimID,dt,accidentPlace,accidentType,accidentDesc,mileage,imgMileage,imgDamage,applyDateTime,approveDateTime,claimStatus,insuranceID,referralUID)


                            updateView()


                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            insuranceListener = object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(currentInsurance in snapshot.children){
                            if(claim.insuranceID != null){claim.insuranceID}else{""}?.let {
                                Log.v(
                                    ContentValues.TAG,
                                    it
                                )
                            }
                            if(currentInsurance.child("insuranceID").getValue().toString().equals(claim.insuranceID)){
                                val insuranceID = currentInsurance.child("insuranceID").getValue().toString()
                                val insuranceName = currentInsurance.child("insuranceName").getValue().toString()
                                val insuranceType = currentInsurance.child("insuranceType").getValue().toString()
                                val insuranceComp = currentInsurance.child("insuranceComp").getValue().toString()
                                val insurancePlan = currentInsurance.child("insurancePlan").getValue().toString()
                                val insuranceCoverage: ArrayList<String> = ArrayList<String>()
                                for(child in currentInsurance.child("insuranceCoverage").children){
                                    insuranceCoverage.add(child.getValue().toString())
                                    //println(child.getValue().toString())
                                }
                                insurance = Insurance(
                                    insuranceID = insuranceID,
                                    insuranceName = insuranceName,
                                    insuranceComp = insuranceComp,
                                    insurancePlan = insurancePlan,
                                    insuranceCoverage = insuranceCoverage,
                                    insuranceType = insuranceType
                                )

                                updateInsuranceView()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            }

            insuranceRef.orderByChild("insuranceID").addValueEventListener(insuranceListener)


        },1000)

        handler.postDelayed({
            claimFigureListener = object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(currentCF in snapshot.children){
                            if(currentCF.child("claimUUID").getValue().toString().equals(claimUUID)){
                                val claimFigureName = currentCF.child("claimFigureName").getValue().toString()
                                val claimFigureAmount = currentCF.child("claimFigureAmount").getValue().toString().toDouble()
                                cfList.add(ClaimFigure(claimUUID,claimFigureName,claimFigureAmount))
                            }
                        }
                        //Log.v(ContentValues.TAG,cfList.size.toString())
                        updateClaimFigure()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            }
            claimFigureRef.orderByChild("claimID").addValueEventListener(claimFigureListener)

        },1000)


        referralRef.orderByChild("referralUID").addListenerForSingleValueEvent(referralListener)
        claimRef.orderByChild("claimID").addValueEventListener(claimListener)

        binding.clCoverageDetailsAC.setOnClickListener{

            var strCoverage = ""
            var rowCount = 0

            if(!(insurance.insuranceCoverage.isNullOrEmpty())){
                for(str:String in insurance.insuranceCoverage!!){
                    strCoverage += str + "\n"
                    rowCount += 1
                }
            }
            binding.tvCoverageTitle.text = "Coverage"
            binding.tvCoverageApproveClaim.text = strCoverage

            if(!isExpanded){
                val height = 200 + (binding.tvCoverageApproveClaim.height* rowCount)

                val layout: ConstraintLayout = binding.clCoverageDetailsAC
                val params: ViewGroup.LayoutParams = layout.layoutParams
                params.height = height
                layout.layoutParams = params
                binding.invalidateAll()
                isExpanded = true
            }

        }

        binding.tvContactNo.setOnClickListener(){
            val telNumber = Uri.parse("tel:${referral.contactNo}")
            val intent = Intent(Intent.ACTION_DIAL,telNumber)
            try{
                startActivity(intent)
            }catch(e: ActivityNotFoundException){
                Toast.makeText(requireContext(),"Action not found",Toast.LENGTH_LONG).show()
            }

        }

        binding.btnAccept.setOnClickListener(){
            DetachListener()
            val action = ApproveClaimFragmentDirections.actionApproveClaimFragmentToApproveClaimAmountFragment(claimUUID,referral.deductible!!.toFloat())
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnReject.setOnClickListener(){
            DetachListener()
            val action = ApproveClaimFragmentDirections.actionApproveClaimFragmentToApproveRejectedFragment(claimUUID)
            Navigation.findNavController(it).navigate(action)
        }

        return binding.root
    }

    fun updateInsuranceView(){

        binding.tvCompTitle.text = insurance.insuranceComp
        binding.tvAccidentType.text = insurance.insuranceType
        binding.tvInsuranceName.text = insurance.insuranceName
        binding.tvInsurancePlanName.text = insurance.insurancePlan

    }

    fun updateView(){
        val format = SimpleDateFormat("dd/MM/yyyy hh:mm:ss",Locale.US)

        binding.tvStatus.text = claim.claimStatus
        binding.tvClaimIDApproveClaim.text = claim.claimID
        binding.tvAccidentDate.text = format.format(claim.accidentDateTime!!)
        //binding.tvAccidentPlace.text = claim.accidentPlace
        binding.tvMileage.text = claim.mileage.toString()
        binding.tvAccidentDesc.text = claim.accidentDesc
        binding.tvAccidentType.text = claim.accidentType

        binding.tvReferralName.text = referral.fullName
        binding.tvContactNo.text = referral.contactNo
        binding.tvEmail.text = referral.email

        showMap()

        when (claim.claimStatus){
            "Pending" -> {
                binding.tvStatus.setTextColor(Color.parseColor("#EC512B"))
                hideClaimFigure()
            }
            "Accepted" -> {
                binding.tvStatus.setTextColor(Color.parseColor("#31B12C"))
                hideButtons()
            }
            "Rejected" -> {
                binding.tvStatus.setTextColor(Color.parseColor("#F30E15"))
                hideClaimFigure()
                hideButtons()
            }
            else -> {
                binding.tvStatus.setTextColor(Color.parseColor("#000000"))
                hideClaimFigure()
                hideButtons()
            }
        }

        if(!(claim.imgMileage.isNullOrEmpty())){
            imgStorageRef = FirebaseStorage.getInstance().getReference("User_$referralUID").child(claim.imgMileage.toString())
            imgStorageRef.downloadUrl.addOnSuccessListener {
                Glide
                    .with(binding.imgMileage.context)
                    .load(it.toString())
                    .into(binding.imgMileage)
                //println("Get image from ${it.toString()}")
                val uri = it
                binding.imgMileage.setOnClickListener(){
                    val imgDlg: ImageDialog = ImageDialog(requireActivity(),uri)
                    imgDlg.showAlertDialog()
                }
            }
        }

        if(!(claim.imgDamage.isNullOrEmpty())){
            imgStorageRef = FirebaseStorage.getInstance().getReference("User_$referralUID").child(claim.imgDamage.toString())
            imgStorageRef.downloadUrl.addOnSuccessListener {
                Glide
                    .with(binding.imgDamage.context)
                    .load(it.toString())
                    .into(binding.imgDamage)
                //println("Get image from ${it.toString()}")
                val uri = it
                binding.imgDamage.setOnClickListener(){
                    val imgDlg: ImageDialog = ImageDialog(requireActivity(),uri)
                    imgDlg.showAlertDialog()
                }
            }
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    fun showMap(){
        mapFragment = childFragmentManager.findFragmentById(R.id.map_approveClaim) as SupportMapFragment
        mapFragment.getMapAsync{

            val location = claim.accidentPlace
            googleMap = it

            //add marker
            googleMap.addMarker(MarkerOptions().position(location).draggable(true).title("Accident location"))

            //zoom in camera to the bookmark
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
        }

        //binding.tvAccidentPlace.text = claim.accidentPlace.toString()

        binding.transparentImage.setOnTouchListener { v, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    // Disallow ScrollView to intercept touch events.
                    binding.scrollViewApproveClaim.requestDisallowInterceptTouchEvent(true)
                    // Disable touch on transparent view
                    false
                }
                MotionEvent.ACTION_UP -> {
                    // Allow ScrollView to intercept touch events.
                    binding.scrollViewApproveClaim.requestDisallowInterceptTouchEvent(false)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    binding.scrollViewApproveClaim.requestDisallowInterceptTouchEvent(true)
                    false
                }
                else -> {
                    true
                }
            }
        }
    }

    fun updateClaimFigure(){
        var subtotal: Double = 0.0
        var deductibleAmt: Double = 0.0
        var total: Double = 0.0

        val adapter = ClaimFigureAdapter(requireContext(),false,cfList,ClaimFigureAdapter.RemoveListener{})
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

        deductibleAmt = subtotal * referral.deductible!!
        total = subtotal - deductibleAmt
        binding.tvSubtotal.text = String.format("%.2f",subtotal)
        binding.tvDeductible.text = String.format("%.2f",deductibleAmt)
        binding.tvTotal.text = String.format("%.2f",total)
        binding.invalidateAll()




    }

    override fun onDetach() {
        super.onDetach()
        DetachListener()
    }

    private fun DetachListener(){
        referralRef.removeEventListener(referralListener)
        claimRef.removeEventListener(claimListener)
        insuranceRef.removeEventListener(insuranceListener)
        claimFigureRef.removeEventListener(claimFigureListener)
    }

    private fun hideClaimFigure(){
        binding.view6.visibility = View.INVISIBLE
        binding.lvAmountDetails.visibility = View.INVISIBLE
        binding.textView31.visibility = View.INVISIBLE
        binding.tbAmount.visibility = View.INVISIBLE
    }

    private fun hideButtons(){
        binding.btnReject.visibility = View.INVISIBLE
        binding.btnReject.isEnabled = false
        binding.btnAccept.visibility = View.INVISIBLE
        binding.btnAccept.isEnabled = false
    }

}