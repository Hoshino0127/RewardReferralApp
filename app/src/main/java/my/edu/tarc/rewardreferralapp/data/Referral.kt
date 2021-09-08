package my.edu.tarc.rewardreferralapp.data

data class Referral(
    val referralID: String? = null,
    val referralUID: String? = null,
    val referralStatus: String? = null,
    val fullName: String? = null,
    val gender: String? = null,
    val nric: String? = null,
    val contactNo: String? = null,
    val email: String? = null,
    val address: String? = null,
    val deductible: Double? = 0.0
)
