package my.edu.tarc.rewardreferralapp.data

import java.util.*

data class InsuranceApplication(
    val applicationID:String?=null,
    val insuranceID:String?=null,
    val referralID:String ?=null,
    val applicationAppliedDate: Date? = null,
    val applicationStatus:String?=null,
    val isEvidences: Boolean? = false,
    val carNoPlate: String? = null,
    val yearMake: String? = null,
    val modelName: String? = null,
    val annualMileage: String? = null,
    val usage: String? = null,
    val airBag: String? = null,
    val antiLockBrake: String? = null,
    var expandable : Boolean = false
)
