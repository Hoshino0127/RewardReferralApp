package my.edu.tarc.rewardreferralapp.data

import java.util.*

data class Insurance(
    val InsuranceID: String,
    val InsuranceName: String,
    val InsuranceComp: String,
    val InsurancePlan: String,
    val InsuranceExpiryDate: Date,
    val InsuranceReferral: String
)
