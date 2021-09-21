package my.edu.tarc.rewardreferralapp.data

import java.util.*

data class CancelInsurance (
    val cancelInsuranceUID : String?=null,
    val insuranceReferralUID : String ?=null,
    val reason : String?=null,
    val appliedDate : Date?=null,
    var expandable : Boolean = false
)