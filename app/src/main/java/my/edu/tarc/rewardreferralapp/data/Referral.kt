package my.edu.tarc.rewardreferralapp.data

data class Referral(
    val referralUID: String? = null,
    val referralStatus: String? = null,
    val fullName: String? = null,
    val gender: String? = null,
    val nric: String? = null,
    val contactNo: String? = null,
    val email: String? = null,
    val address: String? = null,
    val deductible: Double? = 0.0,
    val points: Int? = 0,
    val invitationCode: String? = null,
    val referralUpline: String? = null
)

