package my.edu.tarc.rewardreferralapp.data

import java.text.DateFormat
import java.time.LocalDate
import java.util.*

data class Insurance(
    val insuranceID: String? = null,
    val insuranceName: String? = null,
    val insuranceComp: String? = null,
    val insurancePlan: String? = null,
    val insuranceExpiryDate: Date? = null,
    val insuranceReferral: String? = null,
    val insuranceCoverage: List<String>? = null
)
