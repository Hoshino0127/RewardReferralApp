package my.edu.tarc.rewardreferralapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
import my.edu.tarc.rewardreferralapp.databinding.FragmentClaimDetailsBinding
import my.edu.tarc.rewardreferralapp.dialog.ImageDialog
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ClaimDetailsFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val insuranceRef = database.getReference("Insurance")
    private val referralInsuranceRef = database.getReference("ReferralInsurance")
    private val referralRef = database.getReference("Referral")
    private val claimRef = database.getReference("Claim")
    private val claimFigureRef = database.getReference("ClaimFigure")

    private lateinit var insuranceListener: ValueEventListener
    private lateinit var referralListener: ValueEventListener
    private lateinit var claimListener: ValueEventListener
    private lateinit var claimFigureListener: ValueEventListener

    private lateinit var imgStorageRef : StorageReference

    private lateinit var binding: FragmentClaimDetailsBinding
    private lateinit var claimUUID: String
    private lateinit var referralUID: String
    private var deductible: Double = 0.0
    private var referral: Referral = Referral()
    private var claim: Claim = Claim()
    private var insurance: Insurance = Insurance()
    private var cfList: ArrayList<ClaimFigure> = ArrayList<ClaimFigure>()

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_claim_details, container, false)


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val action = ClaimDetailsFragmentDirections.actionClaimDetailsFragmentToClaimListingFragment()
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        binding.btnBackClaimDetails.setOnClickListener(){
            val action = ClaimDetailsFragmentDirections.actionClaimDetailsFragmentToClaimListingFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        val args = ClaimDetailsFragmentArgs.fromBundle(requireArguments())
        claimUUID = args.claimUUID
        referralUID = CheckUser().getCurrentUserUID()!!

        loadData()


        return binding.root
    }

    private fun loadData(){
        //get deductible from referral
        referralListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(referralSS in snapshot.children){
                        if(referralSS.child("referralUID").value.toString() == referralUID){
                            val referralUID: String = referralSS.child("referralUID").value.toString()
                            val deductible: Double = referralSS.child("deductible").value.toString().toDouble()
                            referral = Referral(referralUID = referralUID, deductible = deductible)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        referralRef.orderByChild("referralUID").addValueEventListener(referralListener)

        claimListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(currentClaim in snapshot.children){

                        if(currentClaim.child("claimUUID").value.toString() == claimUUID){
                            val claimUUID = currentClaim.child("claimUUID").value.toString()
                            val claimID = currentClaim.child("claimID").value.toString()
                            val dt = Date(currentClaim.child("accidentDateTime").child("time").value as Long)
                            val accidentPlaceLat = currentClaim.child("accidentPlace").child("latitude").value as Double
                            val accidentPlaceLong = currentClaim.child("accidentPlace").child("longitude").value as Double
                            val accidentPlace: LatLng = LatLng(accidentPlaceLat,accidentPlaceLong)
                            val accidentType = currentClaim.child("accidentType").value.toString()
                            val accidentDesc = currentClaim.child("accidentDesc").value.toString()
                            val mileage = currentClaim.child("mileage").value.toString().toInt()
                            val imgMileage = currentClaim.child("imgMileage").value.toString()
                            val imgDamage = currentClaim.child("imgDamage").value.toString()
                            val applyDateTime: Date = Date(currentClaim.child("applyDateTime").child("time").value as Long)
                            val approveDateTime: Date? = if(currentClaim.child("approveDateTime").value == null){
                                null
                            }else{
                                Date(currentClaim.child("approveDateTime").child("time").value as Long)
                            }
                            val claimStatus = currentClaim.child("claimStatus").value.toString()
                            val insuranceID = currentClaim.child("insuranceID").value.toString()
                            val referralUID = currentClaim.child("referralUID").value.toString()
                            claim = Claim(claimUUID,claimID,dt,accidentPlace,accidentType,accidentDesc,mileage,imgMileage,imgDamage,applyDateTime,approveDateTime,claimStatus,insuranceID,referralUID)


                            updateView()
                            getInsurance()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        }
        claimRef.orderByChild("claimUUID").addValueEventListener(claimListener)


        claimFigureListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(currentCF in snapshot.children){
                        if(currentCF.child("claimUUID").value.toString() == claimUUID){
                            val claimFigureName = currentCF.child("claimFigureName").value.toString()
                            val claimFigureAmount = currentCF.child("claimFigureAmount").value.toString().toDouble()
                            cfList.add(ClaimFigure(claimUUID,claimFigureName,claimFigureAmount))
                        }
                    }
                    updateClaimFigureView()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        claimFigureRef.orderByChild("claimUUID").addValueEventListener(claimFigureListener)
    }

    fun updateInsuranceView(){
        var strCoverage = ""
        var rowCount = 0

        binding.tvCompTitle.text = insurance.insuranceComp
        binding.tvInsuranceTypeCD.text = insurance.insuranceType
        binding.tvInsuranceName.text = insurance.insuranceName
        binding.tvInsurancePlanName.text = insurance.insurancePlan
        if(!(insurance.insuranceCoverage.isNullOrEmpty())){
            for(str:String in insurance.insuranceCoverage!!){
                strCoverage += str + "\n"
                rowCount += 1
            }
        }
        binding.tvInsuranceCoverage.text = strCoverage
        binding.invalidateAll()
    }

    private fun getInsurance(){
        insuranceListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(currentInsurance in snapshot.children){
                        if(claim.insuranceID != null){claim.insuranceID}else{""}?.let {
                            Log.v(TAG,
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
                                println(child.getValue().toString())
                            }
                            val insurancePrice: Double = currentInsurance.child("insurancePrice").getValue().toString().toDouble()
                            insurance = Insurance(insuranceID, insuranceName, insuranceComp, insurancePlan, insuranceCoverage, insurancePrice, insuranceType)

                            updateInsuranceView()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        insuranceRef.orderByChild("insuranceID").addListenerForSingleValueEvent(insuranceListener)
    }

    fun updateView(){
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.US)

        binding.tvStatus.text = claim.claimStatus
        binding.tvClaimID.text = claim.claimID
        binding.tvAccidentDate.text = format.format(claim.accidentDateTime!!)
        binding.tvApplyDate.text = format.format(claim.applyDateTime)
        binding.tvMileage.text = claim.mileage.toString()
        binding.tvAccidentDesc.text = claim.accidentDesc
        val accidentType = when (claim.accidentType) {
            "OwnDamage" -> {
                "Own damage"
            }
            "ThirdParty" -> {
                "Third party"
            }
            else -> {
                "Theft"
            }
        }

        binding.tvAccidentType.text = accidentType

        showMap()
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({googleMap.uiSettings.setAllGesturesEnabled(true)},2000)

        when (claim.claimStatus){
            "Pending" -> {
                binding.tvStatus.setTextColor(Color.parseColor("#EC512B"))
                hideClaimFigure()
            }
            "Accepted" -> binding.tvStatus.setTextColor(Color.parseColor("#31B12C"))
            "Rejected" -> {
                binding.tvStatus.setTextColor(Color.parseColor("#F30E15"))
                hideClaimFigure()
            }
            else -> binding.tvStatus.setTextColor(Color.parseColor("#000000"))
        }

        if(!(claim.imgMileage.isNullOrEmpty())){
            imgStorageRef = FirebaseStorage.getInstance().getReference("User_"+ CheckUser().getCurrentUserUID()).child(claim.imgMileage.toString())
            imgStorageRef.downloadUrl.addOnSuccessListener {
                Glide
                    .with(binding.imgMileage.context)
                    .load(it.toString())
                    .into(binding.imgMileage)
                println("Get image from ${it.toString()}")
                val uri = it
                binding.imgMileage.setOnClickListener(){
                    val imgDlg: ImageDialog = ImageDialog(requireActivity(),uri)
                    imgDlg.showAlertDialog()
                }
            }

        }

        if(!(claim.imgDamage.isNullOrEmpty())){
            imgStorageRef = FirebaseStorage.getInstance().getReference("User_"+ CheckUser().getCurrentUserUID()).child(claim.imgDamage.toString())
            imgStorageRef.downloadUrl.addOnSuccessListener {
                Glide
                    .with(binding.imgDamage.context)
                    .load(it.toString())
                    .into(binding.imgDamage)
                println("Get image from ${it.toString()}")
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
        mapFragment = childFragmentManager.findFragmentById(R.id.map_claimDetails) as SupportMapFragment
        mapFragment.getMapAsync{

            val location = claim.accidentPlace
            googleMap = it

            //add marker
            googleMap.addMarker(MarkerOptions().position(location).draggable(true).title("Accident location"))

            //zoom in camera to the bookmark
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
            googleMap.uiSettings.setAllGesturesEnabled(false)
        }


        binding.transparentImage.setOnTouchListener { v, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    // Disallow ScrollView to intercept touch events.
                    binding.scrollViewClaimDetails.requestDisallowInterceptTouchEvent(true)
                    // Disable touch on transparent view
                    false
                }
                MotionEvent.ACTION_UP -> {
                    // Allow ScrollView to intercept touch events.
                    binding.scrollViewClaimDetails.requestDisallowInterceptTouchEvent(false)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    binding.scrollViewClaimDetails.requestDisallowInterceptTouchEvent(true)
                    false
                }
                else -> {
                    true
                }
            }
        }
    }

    private fun hideClaimFigure(){
        binding.view6.visibility = View.GONE
        binding.lvAmountDetails.visibility = View.GONE
        binding.textView25.visibility = View.GONE
        binding.tbAmount.visibility = View.GONE
    }

    fun updateClaimFigureView(){
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

}