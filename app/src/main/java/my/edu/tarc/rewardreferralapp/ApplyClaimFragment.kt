package my.edu.tarc.rewardreferralapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.rewardreferralapp.data.Claim
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.data.ReferralInsurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentApplyClaimBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class ApplyClaimFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val insuranceRef = database.getReference("Insurance")
    private val referralInsuranceRef = database.getReference("ReferralInsurance")
    private val claimRef = database.getReference("Claim")

    private lateinit var insuranceListener: ValueEventListener
    private lateinit var referralInsuranceListener: ValueEventListener

    private lateinit var imgStorageRef : StorageReference

    private lateinit var insurance: Insurance
    private lateinit var referralInsurance: ReferralInsurance
    private lateinit var binding: FragmentApplyClaimBinding
    private var insuranceID: String = ""
    private var referralUID: String = ""
    private var insuranceReferralID: String = ""

    private val myCalendar: Calendar = Calendar.getInstance()
    private var imgUriMileage: Uri = Uri.EMPTY
    private var imgUriDamage: Uri = Uri.EMPTY
    private var imgMileageFlag: Boolean = false
    private var imgDamageFlag: Boolean = false
    private lateinit var bmpMileage: Bitmap
    private lateinit var bmpDamage: Bitmap
    private var fromAction: String = ""

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private var locationPermissionGranted: Boolean = false
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var hasMarker: Boolean = false
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var currentLatLng: LatLng

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apply_claim, container, false)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val action = ApplyClaimFragmentDirections.actionApplyClaimFragmentToReferralInsuranceListingFragment()
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        binding.btnBackApplyClaim.setOnClickListener(){
            val action = ApplyClaimFragmentDirections.actionApplyClaimFragmentToReferralInsuranceListingFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        referralUID = CheckUser().getCurrentUserUID()!!

        val args = ApplyClaimFragmentArgs.fromBundle(requireArguments())
        insuranceID = args.insuranceID
        insuranceReferralID = args.insuranceReferralID

        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),104)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.TYPES))

        // Specify the types of place data to return.
        if(!Places.isInitialized()){
            Places.initialize(requireActivity().applicationContext, "AIzaSyCr8RYJfNSb1ebNAN1Zrjd0NzNy6Aa6KNg")
        }

        mapFragment.getMapAsync{
            //default location is KL
            val location = LatLng(3.157764,101.711861)
            googleMap = it

            //zoom in camera to the bookmark
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))


            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissionGranted = true
                googleMap.isMyLocationEnabled = true
            }else{
                locationPermissionGranted = false
                googleMap.isMyLocationEnabled = false
            }

            googleMap.setOnMyLocationButtonClickListener {
                fetchLocation()
                true
            }

            googleMap.setOnMarkerDragListener(object: GoogleMap.OnMarkerDragListener{
                override fun onMarkerDragStart(p0: Marker) {

                }

                override fun onMarkerDrag(p0: Marker) {

                }

                override fun onMarkerDragEnd(p0: Marker) {
                    currentLatLng = p0.position
                }

            })


        }

        //cancel scroll view on map
        binding.transparentImage.setOnTouchListener { v, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    // Disallow ScrollView to intercept touch events.
                    binding.scrollView.requestDisallowInterceptTouchEvent(true)
                    // Disable touch on transparent view
                    false
                }
                MotionEvent.ACTION_UP -> {
                    // Allow ScrollView to intercept touch events.
                    binding.scrollView.requestDisallowInterceptTouchEvent(false)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    binding.scrollView.requestDisallowInterceptTouchEvent(true)
                    false
                }
                else -> {
                    true
                }
            }
        }

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                longitude = place.latLng!!.longitude
                latitude = place.latLng!!.latitude

                currentLatLng = LatLng(latitude, longitude)

                if(hasMarker){
                    googleMap.clear()
                    hasMarker = false
                }

                googleMap.addMarker(MarkerOptions().position(currentLatLng).title(place.name.toString()))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10f))
                hasMarker = true

            }

            override fun onError(status: Status) {
                Log.i(ContentValues.TAG, "Error occurred when searching location: $status")
            }
        })

        referralInsuranceListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (refInsSnapshot in snapshot.children) {
                        if (refInsSnapshot.child("insuranceReferralID").value.toString() == insuranceReferralID) {
                            val insuranceID: String = refInsSnapshot.child("insuranceID").value.toString()
                            val referralUID: String = refInsSnapshot.child("referralUID").value.toString()
                            val insuranceReferralID: String = refInsSnapshot.child("insuranceReferralID").value.toString()
                            val insuranceExpiryDate: Date = Date(refInsSnapshot.child("insuranceExpiryDate").child("time").value as Long)
                            val status: String = refInsSnapshot.child("status").value.toString()
                            referralInsurance = ReferralInsurance(insuranceReferralID,insuranceID,referralUID,insuranceExpiryDate, status)

                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        referralInsuranceRef.addValueEventListener(referralInsuranceListener)

        insuranceListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {


                    for (insuranceSnapshot in snapshot.children) {
                        if (insuranceSnapshot.child("insuranceID").value.toString() == insuranceID) {
                            val insuranceID: String = insuranceSnapshot.child("insuranceID").value.toString()
                            val insuranceType: String = insuranceSnapshot.child("insuranceType").value.toString()
                            val insuranceName: String = insuranceSnapshot.child("insuranceName").value.toString()
                            val insuranceComp: String = insuranceSnapshot.child("insuranceComp").value.toString()
                            val insurancePlan: String = insuranceSnapshot.child("insurancePlan").value.toString()
                            val insuranceCoverage: ArrayList<String> = ArrayList<String>()
                            for(child in insuranceSnapshot.child("insuranceCoverage").children){
                                insuranceCoverage.add(child.value.toString())
                            }
                            insurance = Insurance(
                                insuranceID = insuranceID,
                                insuranceName = insuranceName,
                                insuranceComp = insuranceComp,
                                insurancePlan = insurancePlan,
                                insuranceCoverage = insuranceCoverage,
                                insuranceType = insuranceType
                            )

                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        insuranceRef.addListenerForSingleValueEvent(insuranceListener)


        val datePicker: DatePickerDialog.OnDateSetListener =
            DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                binding.dtAccidentDate.setText(sdf.format(myCalendar.time))
            }

        val timePicker: TimePickerDialog.OnTimeSetListener =
            TimePickerDialog.OnTimeSetListener { view: TimePicker?, hourOfDay: Int, minute: Int ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar.set(Calendar.MINUTE, minute)
                val sdf = SimpleDateFormat("HH:mm", Locale.US)
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
            fromAction = "Mileage"
            chooseImage()
        }

        binding.btnUploadDamage.setOnClickListener(){
            fromAction = "Damage"
            chooseImage()
        }

        binding.imgCrossMileage.setOnClickListener(){
            imgUriMileage = Uri.EMPTY
            binding.imgMileage.setImageURI(Uri.EMPTY)
			hideImgMileage()
        }

        binding.imgCrossDamage.setOnClickListener(){
            imgUriDamage = Uri.EMPTY
            binding.imgDamage.setImageURI(Uri.EMPTY)
			hideImgDamage()
        }

        binding.rlInsuranceDetails.setOnClickListener{
            if(binding.view.visibility == View.INVISIBLE){
                val format = SimpleDateFormat("dd/MM/yyyy",Locale.US)
                var strCoverage = ""
                var rowCount = 0
                binding.view.visibility = View.VISIBLE

                binding.tvInsuranceType.text = insurance.insuranceType
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
                val height = 450 + (binding.tvInsuranceCoverage.height* rowCount) + (20/rowCount)

                val layout: ConstraintLayout = binding.rlInsuranceDetails
                val params: ViewGroup.LayoutParams = layout.layoutParams
                params.height = height
                layout.layoutParams = params
                binding.invalidateAll()
            }
        }
        return binding.root
    }

    private fun fetchLocation() {
        val task = mFusedLocationClient.lastLocation
        if (ActivityCompat.checkSelfPermission(requireContext().applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext().applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 5
            )
            return
        }
        task.addOnSuccessListener {
            if(it != null){
                latitude = it.latitude
                longitude = it.longitude
                currentLatLng = LatLng(latitude, longitude)
                if(hasMarker){
                    googleMap.clear()
                    hasMarker = false
                }
                googleMap.addMarker(MarkerOptions().draggable(true).position(currentLatLng).title("You are here"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10f))
                hasMarker = true
            }
        }
    }

    private fun chooseImage() {
        // create a menu of options
        val optionsMenu = arrayOf<CharSequence>(
            "Take Photo",
            "Choose from Gallery",
            "Exit"
        )
        // create a dialog for showing the optionsMenu
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

        // set the items in builder
        try{
            builder.setItems(optionsMenu,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    when {
                        optionsMenu[i] == "Take Photo" -> {
                            // Open the camera and get the photo
                            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED){
                                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(takePicture, 0)
                            }else{
                                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA),101)
                                Toast.makeText(requireContext(),"Please allow the camera permission",Toast.LENGTH_SHORT).show()
                            }

                        }
                        optionsMenu[i] == "Choose from Gallery" -> {
                            // choose from  external storage
                            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED){
                                val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                                startActivityForResult(pickPhoto, 1)
                            }else{
                                ActivityCompat.requestPermissions(requireActivity(),arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),102)
                                Toast.makeText(requireContext(),"Please allow the file access permission",Toast.LENGTH_SHORT).show()
                            }

                        }
                        optionsMenu[i] == "Exit" -> {
                            dialogInterface.dismiss()
                        }
                    }
                })
        }catch(e: ActivityNotFoundException){
            Toast.makeText(requireContext(),"Camera or image gallery not found",Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.extras!!["data"] as Bitmap?
                    if(fromAction == "Mileage"){
                        bmpMileage = selectedImage!!
                        binding.imgMileage.setImageBitmap(selectedImage)
                        showImgMileage()
                        imgMileageFlag = true
                    }else if(fromAction == "Damage"){
                        bmpDamage = selectedImage!!
                        binding.imgDamage.setImageBitmap(selectedImage)
                        showImgDamage()
                        imgDamageFlag = true
                    }
                }
                1 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.data
                    if(fromAction == "Mileage"){
                        imgUriMileage  = data.data!!
                        binding.imgMileage.setImageURI(data.data)
                        showImgMileage()
                        imgMileageFlag = true
                    }else if(fromAction == "Damage"){
                        imgUriDamage = data.data!!
                        binding.imgDamage.setImageURI(data.data)
                        showImgDamage()
                        imgDamageFlag = true
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }


    private fun showImgMileage(){
        binding.imgMileage.visibility = View.VISIBLE
        binding.imgCrossMileage.visibility = View.VISIBLE
        binding.imgCrossMileage.isClickable = true
    }

    private fun showImgDamage(){
        binding.imgDamage.visibility = View.VISIBLE
        binding.imgCrossDamage.visibility = View.VISIBLE
        binding.imgCrossDamage.isClickable = true
    }


    private fun hideImgMileage(){
        binding.imgMileage.visibility = View.GONE
        binding.imgCrossMileage.visibility = View.INVISIBLE
        binding.imgCrossMileage.isClickable = false
    }

    private fun hideImgDamage(){
        binding.imgDamage.visibility = View.GONE
        binding.imgCrossDamage.visibility = View.INVISIBLE
        binding.imgCrossDamage.isClickable = false
    }




    private fun errorFree(): Boolean{

        if(binding.dtAccidentDate.text.toString().isEmpty() || binding.dtAccidentTime.text.toString().isEmpty()) {
            Toast.makeText(requireContext(),"Please select the accident date and time",Toast.LENGTH_LONG).show()
            binding.dtAccidentDate.requestFocus()
            return false
        }else{


            val dateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
            val currentDate = dateFormat.format(Date())
            val dtString = binding.dtAccidentDate.text.toString() + " " + binding.dtAccidentTime.text.toString()
            if(Date(dtString).after(Date(currentDate))){
                Toast.makeText(requireContext(),"The accident date not be in the future",Toast.LENGTH_LONG).show()
                binding.dtAccidentDate.requestFocus()
                return false
            }


        }

        if(!(binding.rbOwnDamage.isChecked) && !(binding.rbTheft.isChecked) && !(binding.rbThirdParty.isChecked)){
            Toast.makeText(requireContext(),"Please select the accident type",Toast.LENGTH_LONG).show()
            binding.rgAccidentType.requestFocus()
            return false
        }

        if(!hasMarker){
            Toast.makeText(requireContext(),"Please select the accident place",Toast.LENGTH_LONG).show()
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

        if(!imgMileageFlag){
            Toast.makeText(requireContext(),"Please upload mileage image",Toast.LENGTH_LONG).show()
            return false
        }

        if(!imgDamageFlag){
            Toast.makeText(requireContext(),"Please upload damage image",Toast.LENGTH_LONG).show()
            return false
        }

        if(!(referralInsurance.status.equals("Active"))){
            Toast.makeText(requireContext(),"This insurance is not active anymore, try to activate your insurance again",Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun applyClaim(): Boolean{
        val idFormat = SimpleDateFormat("ddMMyyHHMMSS", Locale.US)
        val newUUID = UUID.randomUUID().toString()
        val newID = "CL" + idFormat.format(Date()) + "_" + newUUID.substring(32,36)
        var imgMileageName: String = ""
        var imgDamageName: String = ""

        val dtString = binding.dtAccidentDate.text.toString() + " " + binding.dtAccidentTime.text.toString()

        var accidentType: String = ""
        accidentType = when (binding.rgAccidentType.checkedRadioButtonId) {
            binding.rbOwnDamage.id -> {
                "OwnDamage"
            }
            binding.rbTheft.id -> {
                "Theft"
            }
            else -> {
                "ThirdParty"
            }
        }

        if(imgUriMileage != Uri.EMPTY){
            uploadMileageImg("Upload",newID)
        }else{
            uploadMileageImg("Camera",newID)
        }

        if(imgUriDamage != Uri.EMPTY){
            uploadDamageImg("Upload",newID)
        }else{
            uploadDamageImg("Camera",newID)
        }

        imgMileageName = "mileage_${newID}.jpg"
        imgDamageName = "damage_${newID}.jpg"

        val claim: Claim = Claim(
            newUUID,
            newID,
            Date(dtString),
            currentLatLng,
            accidentType,
            binding.txtAccidentDesc.text.toString(),
            binding.txtMileage.text.toString().toInt(),
            imgMileageName,
            imgDamageName,
            Date(),
            null,
            "Pending",
            insuranceID,
            referralUID
        )

        claimRef.child(claim.claimUUID!!).setValue(claim).addOnSuccessListener {
            println(claim)
            Toast.makeText(requireContext(),"Claim successful",Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Toast.makeText(requireContext(),"Claim failed",Toast.LENGTH_LONG).show()
        }



        return true
    }

    var addImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            if(fromAction == "Mileage"){
                imgUriMileage  = data?.data!!
                binding.imgMileage.setImageURI(data.data)
				binding.imgMileage.visibility = View.VISIBLE
                binding.imgCrossMileage.visibility = View.VISIBLE
                binding.imgCrossMileage.isClickable = true
            }else if(fromAction == "Damage"){
                imgUriDamage = data?.data!!
                binding.imgDamage.setImageURI(data.data)
				binding.imgDamage.visibility = View.VISIBLE
                binding.imgCrossDamage.visibility = View.VISIBLE
                binding.imgCrossDamage.isClickable = true
            }


        }
    }

    private fun uploadMileageImg(imageType: String, claimID: String) {
        imgStorageRef = FirebaseStorage.getInstance().getReference("User_"+CheckUser().getCurrentUserUID()).child("mileage_${claimID}.jpg")
        if(imageType == "Upload"){
            imgStorageRef.putFile(imgUriMileage).addOnSuccessListener {
                Toast.makeText(context, "Upload pic successful",
                    Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(context, "Error fails to upload pic",
                    Toast.LENGTH_SHORT).show()
            }
        }else if (imageType == "Camera"){
            val byteArray = ByteArrayOutputStream()
            bmpMileage.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
            val data = byteArray.toByteArray()
            val uploadTask = imgStorageRef.putBytes(data)
            uploadTask.addOnSuccessListener {
                Toast.makeText(context, "Upload pic successful",
                    Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(context, "Error fails to upload pic",
                    Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun uploadDamageImg(imageType: String, claimID: String) {
        imgStorageRef = FirebaseStorage.getInstance().getReference("User_"+CheckUser().getCurrentUserUID()).child("damage_${claimID}.jpg")
        if(imageType == "Upload"){
            imgStorageRef.putFile(imgUriDamage).addOnSuccessListener {
                Toast.makeText(context, "Upload pic successful",
                    Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(context, "Error fails to upload pic",
                    Toast.LENGTH_SHORT).show()
            }
        }else if (imageType == "Camera"){
            val byteArray = ByteArrayOutputStream()
            bmpDamage.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
            val data = byteArray.toByteArray()
            val uploadTask = imgStorageRef.putBytes(data)
            uploadTask.addOnSuccessListener {
                Toast.makeText(context, "Upload pic successful",
                    Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(context, "Error fails to upload pic",
                    Toast.LENGTH_SHORT).show()
            }
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