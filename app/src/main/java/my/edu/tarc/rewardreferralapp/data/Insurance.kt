package my.edu.tarc.rewardreferralapp.data

data class Insurance(
    val insuranceID: String? = null,
    val insuranceName: String? = null,
    val insuranceComp: String? = null,
    val insurancePlan: String? = null,
    val insuranceCoverage: List<String>? = null,
    val insurancePrice: Double? = 0.0,
    val insuranceType: String? = ""
)
