package my.edu.tarc.rewardreferralapp.data

import com.google.type.DateTime
import java.util.*

data class Claim(
    val claimID: String? = null,
    val accidentDateTime: Date? = null,
    val accidentPlace: String? = null,
    val accidentType: String? = null,
    val accidentDesc: String? = null,
    val mileage: Int? = 0,
    val imgMileage: String? = null,
    val imgDamage: String? = null,
    val applyDateTime: Date? = null,
    val approveDateTime: Date? = null,
    val claimStatus: String? = null,
    val insuranceID: String? = null,
    val insuranceReferral: String? = null
)
