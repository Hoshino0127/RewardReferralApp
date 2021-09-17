package my.edu.tarc.rewardreferralapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import my.edu.tarc.rewardreferralapp.adapter.InsuranceAdapter
import my.edu.tarc.rewardreferralapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var adapter : InsuranceAdapter
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

}

