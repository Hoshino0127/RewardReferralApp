package my.edu.tarc.rewardreferralapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import my.edu.tarc.rewardreferralapp.databinding.ActivityApplyClaimSuccessBinding

class ApplyClaimSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApplyClaimSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_claim_success)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_apply_claim_success)

        binding.btnToClaimListing.setOnClickListener(){
            val intent = Intent(this,ClaimListingActivity::class.java)
            startActivity(intent)
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}