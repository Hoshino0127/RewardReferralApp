package my.edu.tarc.rewardreferralapp.data

data class Reward(
    val rewardID :String? = null,
    val rewardName :String? = null,
    val rewardDesc :String? = null,
    val pointNeeded :Int? = null,
    val startDate :String? = null,
    val endDate :String? = null,
    val stock : Int? = null
)
