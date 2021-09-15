package my.edu.tarc.rewardreferralapp

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import my.edu.tarc.rewardreferralapp.data.Referral
import my.edu.tarc.rewardreferralapp.databinding.FragmentReferralMyQRCodeBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import org.json.JSONObject
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class ReferralMyQRCodeFragment : Fragment() {


    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val refRef = database.getReference("Referral")
    private val refUID = CheckUser().getCurrentUserUID()
    private var referral: Referral = Referral()

    private val secretKey = "kK5UT3i+DP38lIhBxna5XVsneDdoUf6vshfIsSMB6sQ="
    private val salt = "gWlGSHDhMTETQWZ2bFhBV3U="
    private val iv = "tVSDNFNhmkQ2NjR4UUFaWA=="

    private lateinit var binding: FragmentReferralMyQRCodeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_referral_my_q_r_code,
                container,
                false
            )

        getRefDetails()


        return binding.root
    }

    private fun getRefDetails() {

        val qry: Query = refRef.orderByChild("referralUID").equalTo(refUID)

        qry.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (refsnap in snapshot.children) {
                        referral = refsnap.getValue(Referral::class.java)!!
                    }
                    GenererateQRCode()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun GenererateQRCode() {

        val JsonRef =
            JSONObject("""{"App":"MyBee","RefUID":"${referral.referralUID}","RefName":"${referral.fullName}"}""")

        val encryptedContent = encrypt(JsonRef.toString())

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(encryptedContent, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }

        binding.tvRQRName.text = referral.fullName
        binding.imgRQRcode.setImageBitmap(bitmap)

    }

    private fun encrypt(strToEncrypt: String) :  String?
    {
        try
        {
            val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec)
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            return Base64.encodeToString(cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)
        }
        catch (e: Exception)
        {
            println("Error while encrypting: $e")
        }
        return ""
    }



}