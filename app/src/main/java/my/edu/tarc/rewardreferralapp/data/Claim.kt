package my.edu.tarc.rewardreferralapp.data

import com.google.type.DateTime
import com.google.android.gms.maps.model.LatLng
import java.util.*

data class Claim(
    val claimUUID: String? = null,
    val claimID: String? = null,
    val accidentDateTime: Date? = null,
    val accidentPlace: LatLng? = null,
    val accidentType: String? = null,
    val accidentDesc: String? = null,
    val mileage: Int? = 0,
    val imgMileage: String? = null,
    val imgDamage: String? = null,
    val applyDateTime: Date? = null,
    val approveDateTime: Date? = null,
    val claimStatus: String? = null,
    val insuranceID: String? = null,
    val referralUID: String? = null
)
