package my.edu.tarc.rewardreferralapp

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.adapter.InsuranceAdapter
import my.edu.tarc.rewardreferralapp.helper.MyButton
import my.edu.tarc.rewardreferralapp.helper.MySwipeHelper
import my.edu.tarc.rewardreferralapp.listener.MyButtonClickListener
import my.edu.tarc.rewardreferralapp.adapter.InsuranceApplicationAdapter
import my.edu.tarc.rewardreferralapp.data.Insurance
import my.edu.tarc.rewardreferralapp.databinding.FragmentListInsuranceBinding
import my.edu.tarc.rewardreferralapp.data.InsuranceApplication
import java.util.*
import kotlin.collections.ArrayList


class ListInsuranceFragment : Fragment() {

    lateinit var adapter : InsuranceAdapter

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("Insurance")

    private var insuranceList = ArrayList<Insurance>()
    private var tempInsuranceList = ArrayList<Insurance>()

    private lateinit var binding : FragmentListInsuranceBinding

    private var strMsg:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentListInsuranceBinding.inflate(inflater,  container ,false)

        binding.shimmerViewContainer.startShimmer()

        binding.rvInsuranceList.setHasFixedSize(true)

        val swipe = object: MySwipeHelper(requireActivity(), binding.rvInsuranceList, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(requireContext(), "Delete", 30, R.drawable.ic_baseline_delete_white_24, Color.parseColor("#F30E15"),
                    object: MyButtonClickListener{
                        override fun onClick(pos: Int) {

                            strMsg = "Insurance successfully deleted."

                            deleteData(insuranceList[pos].insuranceID, object : FirebaseSuccessListener {
                                override fun onDataFound(isDataFetched: Boolean, Key: String) {
                                    if (isDataFetched && Key.isNotEmpty()){
                                        myRef.child(Key).removeValue()
                                    }
                                }
                            })
                            Toast.makeText(requireContext(), strMsg, Toast.LENGTH_SHORT).show()
                        }
                    }))
                buffer.add(MyButton(requireContext(), "Update", 30, R.drawable.ic_baseline_edit_24, Color.parseColor("#2266FF"),
                    object: MyButtonClickListener{
                        override fun onClick(pos: Int) {

                            val amount = insuranceList[pos].insuranceID

                            val action = ListInsuranceFragmentDirections.actionListInsuranceFragmentToUpdateInsuranceFragment(amount)
                            view?.let { Navigation.findNavController(it).navigate(action) }
                        }
                    }))
            }
        }

        binding.btnAdd.setOnClickListener(){
            val action = ListInsuranceFragmentDirections.actionListInsuranceFragmentToInsuranceAddFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnBackListInsurance.setOnClickListener() {
            val action = ListInsuranceFragmentDirections.actionListInsuranceFragmentToNavigationFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.searchInsurance.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText!!.isNotEmpty()) {
                    tempInsuranceList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    for (insurance in insuranceList) {
                        val combineText:String = insurance.insuranceID + "-" + insurance.insuranceComp + "-" + insurance.insuranceName
                        if (combineText.lowercase(Locale.getDefault()).contains(search)) {
                            if (binding.tabInsuranceFIlter.getTabAt(binding.tabInsuranceFIlter.selectedTabPosition)?.text == "Company") {
                                if (insurance.insuranceComp == binding.spinnerInsuranceComp.selectedItem) {
                                    tempInsuranceList.add(insurance)
                                }
                            } else if (binding.tabInsuranceFIlter.getTabAt(binding.tabInsuranceFIlter.selectedTabPosition)?.text == "Type") {
                                if (insurance.insuranceType == binding.spinnerInsuranceType.selectedItem) {
                                    tempInsuranceList.add(insurance)
                                }
                            } else if (binding.tabInsuranceFIlter.getTabAt(binding.tabInsuranceFIlter.selectedTabPosition)?.text == "All") {
                                tempInsuranceList.add(insurance)
                            }
                        }
                    }

                    changeView(tempInsuranceList)

                } else {
                    tempInsuranceList.clear()
                    for (insurance in insuranceList) {
                        if (binding.tabInsuranceFIlter.getTabAt(binding.tabInsuranceFIlter.selectedTabPosition)?.text == "Company") {
                            if (insurance.insuranceComp == binding.spinnerInsuranceComp.selectedItem) {
                                tempInsuranceList.add(insurance)
                            }
                        } else if (binding.tabInsuranceFIlter.getTabAt(binding.tabInsuranceFIlter.selectedTabPosition)?.text == "Type") {
                            if (insurance.insuranceType == binding.spinnerInsuranceType.selectedItem) {
                                tempInsuranceList.add(insurance)
                            }
                        } else if (binding.tabInsuranceFIlter.getTabAt(binding.tabInsuranceFIlter.selectedTabPosition)?.text == "All") {
                            tempInsuranceList.add(insurance)
                        }

                    }

                    changeView(tempInsuranceList)
                }

                return true
            }

        })

        binding.btnInsuranceFilter.setOnClickListener() {
            if(binding.tabInsuranceFIlter.visibility == View.GONE) {
                binding.tabInsuranceFIlter.visibility = View.VISIBLE

                binding.tabInsuranceFIlter.setOnTabSelectedListener(object :
                    TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        var selectedApplication: ArrayList<Insurance> = ArrayList<Insurance>()
                        binding.clInsuranceType.visibility = View.GONE
                        binding.clInsuranceComp.visibility = View.GONE

                        when (tab?.position) {
                            0 -> {
                                selectedApplication = insuranceList
                                binding.clInsuranceType.visibility = View.GONE
                                binding.clInsuranceComp.visibility = View.GONE
                                binding.searchInsurance.setQuery("", false)
                                binding.searchInsurance.clearFocus()
                            }
                            1 -> {
                                binding.subTabInsuranceFilter.visibility = View.VISIBLE
                                binding.clInsuranceComp.visibility = View.VISIBLE
                                binding.spinnerInsuranceComp.visibility = View.VISIBLE

                                selectedApplication = insuranceList.filter{ s -> s.insuranceComp == binding.spinnerInsuranceComp.selectedItem} as ArrayList<Insurance>
                            }
                            2 -> {
                                binding.subTabInsuranceFilter.visibility = View.VISIBLE
                                binding.clInsuranceType.visibility = View.VISIBLE
                                binding.spinnerInsuranceType.visibility = View.VISIBLE
                                selectedApplication = insuranceList.filter{ s -> s.insuranceComp == binding.spinnerInsuranceType.selectedItem} as ArrayList<Insurance>
                            }
                        }

                        changeView(selectedApplication)
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        binding.searchInsurance.setQuery("", false)
                        binding.searchInsurance.clearFocus()
                    }

                })

            } else {
                binding.tabInsuranceFIlter.visibility = View.GONE
                binding.spinnerInsuranceComp.visibility = View.GONE
                binding.spinnerInsuranceType.visibility = View.GONE
            }
        }

        binding.spinnerInsuranceComp.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    var selectedApplication: ArrayList<Insurance> = ArrayList<Insurance>()
                    val selectedComp  = binding.spinnerInsuranceComp.selectedItem.toString()
                    selectedApplication = insuranceList.filter{ s -> s.insuranceComp == selectedComp} as ArrayList<Insurance>
                    changeView(selectedApplication)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

        binding.spinnerInsuranceType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    var selectedApplication: ArrayList<Insurance> = ArrayList<Insurance>()
                    val selectedType   = binding.spinnerInsuranceType.selectedItem.toString()
                    selectedApplication = insuranceList.filter{ s -> s.insuranceType == selectedType} as ArrayList<Insurance>
                    changeView(selectedApplication)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

        loadData()
        onRefresh()

        return binding.root
    }

    private fun loadData() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    insuranceList.clear()
                    for (insuranceSnapshot in snapshot.children){

                        val insuranceID: String = insuranceSnapshot.child("insuranceID").value.toString()
                        val insuranceName: String = insuranceSnapshot.child("insuranceName").value.toString()
                        val insuranceComp: String = insuranceSnapshot.child("insuranceComp").value.toString()
                        val insurancePlan: String = insuranceSnapshot.child("insurancePlan").value.toString()
                        val insuranceType: String = insuranceSnapshot.child("insuranceType").value.toString()
                        var insuranceCoverage: ArrayList<String> = ArrayList<String>()
                        for(child in insuranceSnapshot.child("insuranceCoverage").children){
                            insuranceCoverage.add(child.value.toString())
                        }
                        val insurancePrice: String = insuranceSnapshot.child("insurancePrice").value.toString()
                        println(insurancePrice)

                        val insurance = Insurance(insuranceID,insuranceName,insuranceComp,insurancePlan, insuranceCoverage,insurancePrice.toDouble(),insuranceType)

                        insuranceList.add(insurance)

                    }

                    tempInsuranceList.addAll(insuranceList)
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.rvInsuranceList.visibility = View.VISIBLE
                    binding.tvNoRecord.visibility = View.GONE
                    binding.rvInsuranceList.adapter?.notifyDataSetChanged()

                } else {
                    insuranceList.clear()
                    tempInsuranceList.clear()
                    Handler().postDelayed ({
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                        binding.tvNoRecord.visibility = View.VISIBLE
                    }, 3000)
                    binding.rvInsuranceList.visibility = View.INVISIBLE
                    binding.rvInsuranceList.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        changeView(insuranceList)
    }

    private fun deleteData(insuranceID : String?, dataFetched:FirebaseSuccessListener) {

        myRef.orderByChild("insuranceID").equalTo(insuranceID).addListenerForSingleValueEvent( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    if (ds.exists()){
                        ds.key?.let { dataFetched.onDataFound(true, it) }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun onRefresh() {
        binding.srlInsuranceList.setOnRefreshListener {
            loadData()
            Toast.makeText(requireContext(), "Refresh", Toast.LENGTH_SHORT).show()
            binding.srlInsuranceList.isRefreshing = false
        }
    }

    interface FirebaseSuccessListener {
        fun onDataFound(isDataFetched: Boolean, Key: String)
    }

    private fun changeView(insuranceList: List<Insurance>) {
        adapter = InsuranceAdapter(requireActivity(), insuranceList)
        binding.rvInsuranceList.adapter = adapter
        binding.rvInsuranceList.adapter!!.notifyDataSetChanged()
    }

}