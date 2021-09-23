package my.edu.tarc.rewardreferralapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import my.edu.tarc.rewardreferralapp.functions.CheckUser

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({if(CheckUser().ifCurrentUserExists()){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }else{
            val intent = Intent(this, GetStarted::class.java)
            startActivity(intent)
        }},2000)

    }
}