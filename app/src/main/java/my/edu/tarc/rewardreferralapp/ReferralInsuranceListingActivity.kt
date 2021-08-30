package my.edu.tarc.rewardreferralapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil
import my.edu.tarc.rewardreferralapp.databinding.ActivityReferralInsuranceListingBinding


class ReferralInsuranceListingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReferralInsuranceListingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_referral_insurance_listing)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_referral_insurance_listing)
        binding.btnToApplyClaim.setOnClickListener(){
            val intent = Intent(this,ApplyClaimActivity::class.java)
            startActivity(intent)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}