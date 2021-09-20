package my.edu.tarc.rewardreferralapp

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.data.RewardDelivery
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardEntryBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import my.edu.tarc.rewardreferralapp.helper.MyLottie
import java.text.SimpleDateFormat
import java.util.*

class RewardEntryFragment : Fragment() {
    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val rewardRef = database.getReference("Reward")

    lateinit var binding: FragmentRewardEntryBinding
    private val myCalendar: Calendar = Calendar.getInstance()
    private lateinit var imgUriReward: Uri
    private var rewardID: String = ""
    private var checkImgChange: Int = 0
    private var upImgName: String = ""

    private var loadingDialog: Dialog?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val args = RewardEntryFragmentArgs.fromBundle(requireArguments())

        rewardID = args.rewardID.toString()


        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_reward_entry,
            container,
            false
        )

        if (rewardID != "") {
            binding.btnRESubmit.visibility = View.GONE
            binding.btnREUpdate.visibility = View.VISIBLE
            checkImgChange = 1
            showLoading()
            loadReward()
            hideLoading()
        }

        binding.btnBackRE.setOnClickListener(){
            val action = RewardEntryFragmentDirections.actionRewardEntryFragmentToRewardListingFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.btnRESubmit.setOnClickListener() {
            if (checkError()) {
                InsertReward()
            }
        }

        binding.btnREUpdate.setOnClickListener {
            if (checkError()) {
                updateReward()
            }
        }


        var addImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data

                    imgUriReward = data?.data!!
                    binding.ImgREReward.setImageURI(data?.data)

                }
            }

        val datePickerStartDate: DatePickerDialog.OnDateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                val myFormat = "dd/MM/yyyy" //In which you need put here
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.tdREStartDate.setText(sdf.format(myCalendar.time))
            }

        val datePickerExpiredDate: DatePickerDialog.OnDateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                val myFormat = "dd/MM/yyyy" //In which you need put here
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.tdREDateBefore.setText(sdf.format(myCalendar.time))
            }

        binding.tdREStartDate.setOnClickListener {
            val dpd: DatePickerDialog = DatePickerDialog(
                requireContext(),
                datePickerStartDate,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }

        binding.tdREDateBefore.setOnClickListener {
            val dpd: DatePickerDialog = DatePickerDialog(
                requireContext(),
                datePickerExpiredDate,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }

        binding.btnREUploadPhoto.setOnClickListener() {

            if (checkImgChange == 1) {
                checkImgChange = 2
            }

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
            Toast.makeText(context, "Please enter the start date", Toast.LENGTH_LONG).show()
            return false
        }

        if (binding.tdREDateBefore.text.toString() == "") {
            Toast.makeText(context, "Please enter the available before date", Toast.LENGTH_LONG)
                .show()
            return false
        }

        var todayDate: Date
        var startDate: Date

        val format = SimpleDateFormat("dd/MM/yyyy")

        todayDate = format.parse(format.format(Date()))
        startDate = format.parse(binding.tdREStartDate.text.toString())

        if (startDate < todayDate) {
            Toast.makeText(context, "Start date must be today or day later", Toast.LENGTH_LONG)
                .show()
            return false
        }

        var expiredDate: Date = format.parse(binding.tdREDateBefore.text.toString())

        if (expiredDate <= startDate) {
            Toast.makeText(
                context,
                "Expired date must later than the start date",
                Toast.LENGTH_LONG
            )
                .show()
            return false
        }

        if (binding.spREStatus.selectedItem == "") {
            Toast.makeText(context, "Please select the status", Toast.LENGTH_LONG)
                .show()
            return false
        }

        if (binding.ptREStock.text.toString() == "") {
            binding.ptREStock.requestFocus()
            Toast.makeText(context, "Please enter the stock", Toast.LENGTH_LONG).show()
            return false
        }

        if (binding.ImgREReward.drawable.constantState == resources.getDrawable(R.drawable.rect_bg).constantState) {
            Toast.makeText(context, "Please upload image", Toast.LENGTH_LONG)
                .show()
            return false
        }

        return true
    }

    private fun InsertReward() {

        var newID: String = UUID.randomUUID().toString()

        var imageName = "Reward_$newID"

        insertRewardImg(imageName)

        val reward = Reward(
            newID,
            binding.ptRERewardName.text.toString(),
            binding.ptRERewardDescription.text.toString(),
            Integer.valueOf(binding.ptREPointNeeded.text.toString()),
            binding.tdREStartDate.text.toString(),
            binding.tdREDateBefore.text.toString(),
            Integer.valueOf(binding.ptREStock.text.toString()),
            binding.spREStatus.selectedItem.toString(),
            imageName
        )

        rewardRef.child(newID).setValue(reward).addOnSuccessListener() {
            Toast.makeText(context, "Reward added successful", Toast.LENGTH_LONG)
                .show()
            val action =
                RewardEntryFragmentDirections.actionRewardEntryFragmentToRewardListingFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

    }

    private fun insertRewardImg(imageName: String) {
        var imgref: StorageReference =
            FirebaseStorage.getInstance().getReference("RewardStorage").child(imageName)
        imgref.putFile(imgUriReward).addOnSuccessListener {

        }
    }

    private fun loadReward() {

        var qry: Query = rewardRef.orderByChild("rewardID").equalTo(rewardID)
        var rw = Reward()


        qry.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (rewardsnap in snapshot.children) {
                        rw = rewardsnap.getValue(Reward::class.java)!!
                    }
                    binding.ptRERewardName.setText(rw.rewardName)
                    binding.ptRERewardDescription.setText(rw.rewardDesc)
                    binding.ptREPointNeeded.setText(rw.pointNeeded.toString())
                    binding.tdREStartDate.setText(rw.startDate)
                    binding.tdREDateBefore.setText(rw.endDate)

                    val spinnerAdp: ArrayAdapter<*> =
                        binding.spREStatus.adapter as ArrayAdapter<*>

                    for (i in 0..spinnerAdp.count - 1) {
                        if (spinnerAdp.getItem(i) == rw.status) {
                            binding.spREStatus.setSelection(i)
                            break
                        }
                    }

                    binding.ptREStock.setText(rw.stock.toString())

                    var imgref: StorageReference =
                        FirebaseStorage.getInstance().getReference("RewardStorage")
                            .child(rw.rewardImg.toString())
                    imgref.downloadUrl.addOnSuccessListener {
                        Glide
                            .with(binding.ImgREReward.context)
                            .load(it.toString())
                            .into(binding.ImgREReward)
                    }

                    upImgName = rw.rewardImg.toString()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun updateReward() {

        val upReward = mapOf<String, Any?>(
            "rewardName" to binding.ptRERewardName.text.toString(),
            "rewardDesc" to binding.ptRERewardDescription.text.toString(),
            "pointNeeded" to Integer.valueOf(binding.ptREPointNeeded.text.toString()),
            "startDate" to binding.tdREStartDate.text.toString(),
            "endDate" to binding.tdREDateBefore.text.toString(),
            "status" to binding.spREStatus.selectedItem.toString(),
            "stock" to Integer.valueOf(binding.ptREStock.text.toString())
        )

        rewardRef.child(rewardID).updateChildren(upReward).addOnSuccessListener() {

            if (checkImgChange == 2) {
                insertRewardImg(upImgName)
            }
            Toast.makeText(context, "Update reward successful", Toast.LENGTH_LONG)
                .show()
            val action =
                RewardEntryFragmentDirections.actionRewardEntryFragmentToRewardListingFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
    }

    private fun hideLoading() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }

}