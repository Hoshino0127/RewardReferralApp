package my.edu.tarc.rewardreferralapp

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.database.*
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.adapter.RewardListAdapter
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardListingBinding

class RewardListingFragment : Fragment() {


    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val rewardRef = database.getReference("Reward")

    private lateinit var binding: FragmentRewardListingBinding
    private var rewardList = ArrayList<Reward>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reward_listing, container, false)

        val rewardAdapter = RewardListAdapter(rewardList, RewardListAdapter.ViewListener{
            rewardID -> val it = view
            if (it != null) {
            }
        })
        binding.RewardListRV.adapter = rewardAdapter
        binding.RewardListRV.setHasFixedSize(true)

        getReward()

        binding.btnRLSearch.setOnClickListener {
            val iMm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            iMm.hideSoftInputFromWindow(view?.windowToken, 0)
            view?.clearFocus()

            getRewardBySearch()

        }

        binding.fbtnRLAddNewReward.setOnClickListener(){

            val action = RewardListingFragmentDirections.actionRewardListingFragmentToRewardEntryFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        return binding.root

    }

    private fun getReward(){
        rewardRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rewardList.clear()
                if (snapshot.exists()) {

                    for (rewardSnapshot in snapshot.children) {
                        rewardList.add(rewardSnapshot.getValue(Reward::class.java)!!)

                    }
                    binding.RewardListRV.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getRewardBySearch(){
        var qry: Query = rewardRef.orderByChild("rewardName")
            .startAt(binding.ptRLRewardName.text.toString())
            .endAt(binding.ptRLRewardName.text.toString() + "\uf8ff")

        qry.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rewardList.clear()
                if (snapshot.exists()) {
                    for (rewardSnapshot in snapshot.children) {
                        rewardList.add(rewardSnapshot.getValue(Reward::class.java)!!)

                    }
                    binding.RewardListRV.adapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Reward Not Found", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }
        })
    }

}