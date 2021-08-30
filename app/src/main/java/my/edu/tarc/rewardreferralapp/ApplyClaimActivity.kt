package my.edu.tarc.rewardreferralapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil
import my.edu.tarc.rewardreferralapp.databinding.ActivityApplyClaimBinding

class ApplyClaimActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApplyClaimBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_claim)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_apply_claim)

        binding.btnApplyClaim.setOnClickListener(){
            if(CheckError()){
                if(ApplyClaim()){
                    val intent = Intent(this,ApplyClaimSuccessActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"Apply claim failed",Toast.LENGTH_LONG).show()
                }
            }
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

    private fun CheckError(): Boolean{
        return true
    }

    private fun ApplyClaim(): Boolean{
        return true
    }
}