package my.edu.tarc.rewardreferralapp.data

data class Reward(
    val RewardID :String,
    val RewardName :String,
    val RewardDesc :String,
    val PointNeeded :Int,
    val StartDate :String,
    val EndDate :String,
    val Stock : Int
)
