package my.edu.tarc.rewardreferralapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import kotlin.system.exitProcess


class ListInsuranceActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_insurance)
    }

    override fun onDestroy() {
        super.onDestroy()
        exitProcess(0)
    }

    private fun restart(context: Context) {
        val mainIntent =
            IntentCompat.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_LAUNCHER)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.applicationContext.startActivity(mainIntent)
        exitProcess(0)
    }

}