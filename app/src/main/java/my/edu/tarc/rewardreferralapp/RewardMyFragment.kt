package my.edu.tarc.rewardreferralapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import my.edu.tarc.rewardreferralapp.data.Reward
import my.edu.tarc.rewardreferralapp.adapter.RewardMyAdapter
import my.edu.tarc.rewardreferralapp.databinding.FragmentRewardMyBinding
import com.google.firebase.database.*

class RewardMyFragment : Fragment() {

    val database =
        FirebaseDatabase.getInstance("https://rewardreferralapp-bccdc-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val rewardRef = database.getReference("Reward")
    val refRewRef = database.getReference("RefferalReward")
    val rewardIDList = ArrayList<String>()
    var rewardList = ArrayList<Reward>()
    val referralID = "R0001"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding:FragmentRewardMyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reward_my, container, false)

        val rewardApter = RewardMyAdapter(rewardList, RewardMyAdapter.ProceedListener{
            rewardID,rewardName ->val it = view
            if (it != null) {
                Toast.makeText(context, "$rewardID My", Toast.LENGTH_LONG).show()
            }
        })

        binding.MyRewardRV.adapter = rewardApter
        binding.MyRewardRV.setHasFixedSize(true)

        var qryRefRew:Query = refRewRef.orderByChild("refferalID").equalTo(referralID)
        qryRefRew.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(refrewsnap in snapshot.children){
                    rewardIDList.add(refrewsnap.child("rewardID").getValue().toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })





        return binding.root



    }

}