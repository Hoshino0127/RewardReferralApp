package my.edu.tarc.rewardreferralapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardEntryBinding

class RewardEntryFragment : Fragment() {

    lateinit var binding: FragmentRewardEntryBinding
    private lateinit var imgUriReward: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_reward_entry,
            container,
            false
        )

        binding.btnRESubmit.setOnClickListener() {

        }


        var addImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data

                    imgUriReward = data?.data!!
                    binding.ImgREReward.setImageURI(data?.data)
                }
            }

        binding.btnREUploadPhoto.setOnClickListener() {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            addImage.launch(intent)
        }

        return binding.root
    }

    private fun checkError(): Boolean {

        if (binding.ptRERewardName.text.toString() == "") {
            binding.ptRERewardName.requestFocus()
            Toast.makeText(context, "Please enter the reward name", Toast.LENGTH_LONG).show()
            return false
        }

        if (binding.ptRERewardDescription.text.toString() == "") {
            binding.ptRERewardDescription.requestFocus()
            Toast.makeText(context, "Please enter the reward description", Toast.LENGTH_LONG).show()
            return false
        }

        if (binding.ptREPointNeeded.text.toString() == "") {
            binding.ptREPointNeeded.requestFocus()
            Toast.makeText(context, "Please enter the point needed", Toast.LENGTH_LONG).show()
            return false
        }

        if (binding.tdREStartDate.text.toString() == "") {
            binding.ptREPointNeeded.requestFocus()
            Toast.makeText(context, "Please enter the start date", Toast.LENGTH_LONG).show()
            return false
        }

        if (binding.tdREDateBefore.text.toString() == "") {
            binding.ptREPointNeeded.requestFocus()
            Toast.makeText(context, "Please enter the available before date", Toast.LENGTH_LONG)
                .show()
            return false
        }

        return true
    }

}