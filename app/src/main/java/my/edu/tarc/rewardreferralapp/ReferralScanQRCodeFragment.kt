package my.edu.tarc.rewardreferralapp


import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.*
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.integration.android.IntentIntegrator
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.data.ReferralTransfer
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferralScanQRCodeBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import my.edu.tarc.rewardreferralapp.helper.MyLottie
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters.nextOrSame
import java.time.temporal.TemporalAdjusters.previousOrSame
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class ReferralScanQRCodeFragment : Fragment() {

    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val refRef = database.getReference("Referral")
    private val refTransferRef = database.getReference("ReferralTransfer")
    private var refferalUID = CheckUser().getCurrentUserUID()
    private var refferal: Referral = Referral()
    private var tReferralJSON: JSONObject = JSONObject()

    private var thisWeekPointUsage: Int = 0

    private val secretKey = "kK5UT3i+DP38lIhBxna5XVsneDdoUf6vshfIsSMB6sQ="
    private val salt = "gWlGSHDhMTETQWZ2bFhBV3U="
    private val iv = "tVSDNFNhmkQ2NjR4UUFaWA=="

    private lateinit var binding: FragmentReferralScanQRCodeBinding
    private val handler = Handler(Looper.getMainLooper())
    private var loadingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_referral_scan_q_r_code,
                container,
                false
            )

        thisWeekPointUsage = 100

        showLoading()
        getReferralDetails()

        binding.tvRSQRRTransferName.visibility = View.GONE
        binding.btnRSQRTransfer.visibility = View.GONE
        binding.ptRSQRPointEnter.visibility = View.GONE

        binding.btnBackRSQR.setOnClickListener(){
            val action = ReferralScanQRCodeFragmentDirections.actionReferralScanQRCodeFragmentToHomepage()
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.btnRSQRScanQR.setOnClickListener {

            val QRCodeScanner = IntentIntegrator.forSupportFragment(this)
            QRCodeScanner.initiateScan()

        }

        binding.btnRSQRGallery.setOnClickListener {
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(pickIntent, 111)
        }

        binding.btnRSQRTransfer.setOnClickListener {

            if (checkError()) {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("Confirm details")
                builder.setMessage(
                    "Are you sure to transfer ${binding.ptRSQRPointEnter.text} points to ${
                        tReferralJSON.getString(
                            "RefName"
                        )
                    } ?"
                )
                builder.setPositiveButton("Yes") { dialogInterface, which ->
                    transferPoint()
                }

                builder.setNegativeButton("No") { dialogInterface, which ->
                    dialogInterface.dismiss()
                }

                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        }

        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111) {
            if (data != null || data?.data != null) {

                var uri: Uri? = data.data
                try {
                    var inputstream = context?.getContentResolver()?.openInputStream(uri!!)
                    var bitmap = BitmapFactory.decodeStream(inputstream)

                    val width = bitmap.width
                    val height = bitmap.height
                    val pixels: IntArray = IntArray(width * height)
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                    bitmap.recycle()
                    val source: RGBLuminanceSource = RGBLuminanceSource(width, height, pixels)
                    val bBitmap: BinaryBitmap = BinaryBitmap(HybridBinarizer(source))
                    val reader: MultiFormatReader = MultiFormatReader()
                    try {

                        try {

                            val result = reader.decode(bBitmap)
                            val decyptedContent = decrypt(result.toString())
                            val obj = JSONObject(decyptedContent)

                            if (obj.getString("App") == "MyBee") {
                                if (obj.getString("RefUID") != refferalUID) {
                                    Toast.makeText(
                                        context,
                                        "QR code scanned successful",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    tReferralJSON = obj
                                    setVisibleAfterScan()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "You cannot transfer point to yourself",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please use the MyBee QR code",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }

                        } catch (e: JSONException) {
                            Toast.makeText(
                                context,
                                "Please use the MyBee QR code",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }


                    } catch (e: NotFoundException) {
                        Toast.makeText(context, "Please use the MyBee QR code", Toast.LENGTH_LONG)
                            .show()
                    }


                } catch (e: FileNotFoundException) {
                    Toast.makeText(context, "Not File exception", Toast.LENGTH_LONG).show()
                }

            }

        } else {

            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result.contents != null) {
                try {
                    val decyptedContent = decrypt(result.contents)
                    val obj = JSONObject(decyptedContent)

                    if (obj.getString("App") == "MyBee") {
                        if (obj.getString("RefUID") != refferalUID) {
                            Toast.makeText(context, "QR code scanned successful", Toast.LENGTH_LONG)
                                .show()
                            tReferralJSON = obj
                            setVisibleAfterScan()
                        } else {
                            Toast.makeText(
                                context,
                                "You cannot transfer point to yourself",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    } else {
                        Toast.makeText(context, "Please use the MyBee QR code", Toast.LENGTH_LONG)
                            .show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(context, "Please use the MyBee QR code", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

    }

    private fun setVisibleAfterScan() {

        binding.btnRSQRGallery.visibility = View.GONE
        binding.btnRSQRScanQR.visibility = View.GONE

        binding.tvRSQRRTransferName.visibility = View.VISIBLE
        binding.btnRSQRTransfer.visibility = View.VISIBLE
        binding.ptRSQRPointEnter.visibility = View.VISIBLE

        binding.tvRSQRRTransferName.text = "Name: ${tReferralJSON.getString("RefName")}"

    }

    private fun getReferralDetails() {
        var qryRef: Query = refRef.orderByChild("referralUID").equalTo(refferalUID)
        qryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (refsnap in snapshot.children) {
                        refferal = refsnap.getValue(Referral::class.java)!!
                    }

                    binding.tvRSQRMyPoint.text = refferal.points.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG)
                    .show()
            }
        })

        val format = SimpleDateFormat("dd/MM/yyyy")

        var today: LocalDate = LocalDate.now()
        var thisWeekMonday: LocalDate = today.with(previousOrSame(DayOfWeek.MONDAY))
        var thisWeekSunday: LocalDate = today.with(nextOrSame(DayOfWeek.SUNDAY))

        var mondayString = thisWeekMonday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        var sundayString = thisWeekSunday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        var MondayDate = format.parse(mondayString)
        var SundayDate = format.parse(sundayString)


        var transferDate: Date

        var qryTransfer: Query = refTransferRef.orderByChild("referralDonorID").equalTo(refferalUID)
        qryTransfer.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (transferSnap in snapshot.children) {
                        transferDate =
                            format.parse(transferSnap.child("transferDate").getValue().toString())
                        if (transferDate >= MondayDate && transferDate <= SundayDate) {
                            thisWeekPointUsage -= Integer.valueOf(
                                transferSnap.child("pointTransfer").getValue().toString()
                            )
                        }
                    }
                }
                binding.tvRSQRThisWeekTransfer.text =
                    "You still can transfer $thisWeekPointUsage points in this week"
                handler.postDelayed({
                    hideLoading()
                }, 200)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG)
                    .show()
            }

        })
    }

    private fun decrypt(strToDecrypt: String): String? {
        try {

            val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =
                PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
        } catch (e: Exception) {
            println("Error while decrypting: $e")
        }
        return ""
    }

    private fun checkError(): Boolean {
        if (binding.ptRSQRPointEnter.text.toString() == "") {
            binding.ptRSQRPointEnter.requestFocus()
            Toast.makeText(
                context,
                "Please enter the point that wanted to transfer",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (Integer.valueOf(binding.ptRSQRPointEnter.text.toString()) == 0) {
            binding.ptRSQRPointEnter.requestFocus()
            Toast.makeText(
                context,
                "You cannot transfer 0 points",
                Toast.LENGTH_LONG
            )
                .show()
            return false
        }

        if (Integer.valueOf(binding.ptRSQRPointEnter.text.toString()) > thisWeekPointUsage) {
            binding.ptRSQRPointEnter.requestFocus()
            Toast.makeText(
                context,
                "The transfer amounts have exceed the weekly limit",
                Toast.LENGTH_LONG
            )
                .show()
            return false
        }

        if (Integer.valueOf(binding.ptRSQRPointEnter.text.toString()) > Integer.valueOf(binding.tvRSQRMyPoint.text.toString())) {
            binding.ptRSQRPointEnter.requestFocus()
            Toast.makeText(context, "You have not enough point to transfer", Toast.LENGTH_LONG)
                .show()
            return false
        }



        return true
    }

    private fun transferPoint() {

        var newID: String = UUID.randomUUID().toString()

        val dateformat = SimpleDateFormat("dd/MM/yyyy")
        val transferDate = dateformat.format(Date())

        val referralTransfer = ReferralTransfer(
            newID,
            refferalUID,
            Integer.valueOf(
                binding.ptRSQRPointEnter.text.toString()
            ),
            transferDate,
            tReferralJSON.getString("RefUID")
        )

        //update two referral point

        refRef.child(tReferralJSON.getString("RefUID")).get().addOnSuccessListener() {
            val upRefRecipientPoint = mapOf<String, Any?>(
                "points" to Integer.valueOf(it.child("points").value.toString()) + Integer.valueOf(
                    binding.ptRSQRPointEnter.text.toString()
                )
            )
            refRef.child(tReferralJSON.getString("RefUID")).updateChildren(upRefRecipientPoint)
        }


        val upRefDonorPoint = mapOf<String, Any?>(
            "points" to Integer.valueOf(binding.tvRSQRMyPoint.text.toString()) - Integer.valueOf(
                binding.ptRSQRPointEnter.text.toString()
            )
        )

        refRef.child(refferalUID.toString()).updateChildren(upRefDonorPoint)

        refTransferRef.child(newID).setValue(referralTransfer).addOnSuccessListener() {

            Toast.makeText(context, "Transfer point successful", Toast.LENGTH_LONG).show()
            val action =
                ReferralScanQRCodeFragmentDirections.actionReferralScanQRCodeFragmentToReferralTransferListingFragment()
            Navigation.findNavController(requireView()).navigate(action)


        }.addOnFailureListener {
            Toast.makeText(context, "Fail to transfer point", Toast.LENGTH_LONG).show()
        }


    }

    private fun hideLoading() {
        loadingDialog?.let { if (it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }

}
