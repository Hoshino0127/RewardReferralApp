package my.edu.tarc.rewardreferralapp

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.rewardreferralapp.helper.MyLottie
import my.edu.tarc.rewardreferralapp.adapter.InsuranceApplicationAdapter
import my.edu.tarc.rewardreferralapp.databinding.FragmentListInsuranceApplicationCustViewBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser
import my.edu.tarc.rewardreferralapp.data.InsuranceApplication
import java.util.*
import kotlin.collections.ArrayList

class ListInsuranceApplicationCustViewFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private val insuranceApplicationRef = database.getReference("InsuranceApplication")

    private lateinit var adapterInsApp : InsuranceApplicationAdapter

    private var insApplicationList = ArrayList<InsuranceApplication>()
    private var tempInsApplicationList = ArrayList<InsuranceApplication>()

    private lateinit var binding: FragmentListInsuranceApplicationCustViewBinding

    private var loadingDialog: Dialog?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListInsuranceApplicationCustViewBinding.inflate(inflater, container ,false)

        loadData()

        binding.rvInsApplicationCustView.setHasFixedSize(true)

        binding.btnBackListInsuranceApplicationCustView.setOnClickListener() {
            val action = ListInsuranceApplicationCustViewFragmentDirections.actionListInsuranceApplicationCustViewFragmentToNavigationFragment()
            Navigation.findNavController(it).navigate(action)
        }

        changeView(insApplicationList)

        binding.searchApplicationCustView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    tempInsApplicationList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    for (insurance in insApplicationList) {
                        val combineText = insurance.applicationID
                        if (combineText?.lowercase(Locale.getDefault())?.contains(search) == true) {
                            if(binding.applicationTabLayout.getTabAt(binding.applicationTabLayout.selectedTabPosition)?.text == insurance.applicationStatus) {
                                tempInsApplicationList.add(insurance)
                            } else if (binding.applicationTabLayout.getTabAt(binding.applicationTabLayout.selectedTabPosition)?.text == "All") {
                                tempInsApplicationList.add(insurance)
                            }
                        }
                    }
                    changeView(tempInsApplicationList)
                } else {
                    changeView(insApplicationList)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    tempInsApplicationList.clear()
                    val search = newText.lowercase(Locale.getDefault())
                    for (insurance in insApplicationList) {
                        val combineText = insurance.applicationID
                        if (combineText?.lowercase(Locale.getDefault())?.contains(search) == true) {
                            if(binding.applicationTabLayout.getTabAt(binding.applicationTabLayout.selectedTabPosition)?.text == insurance.applicationStatus) {
                                tempInsApplicationList.add(insurance)
                            } else if (binding.applicationTabLayout.getTabAt(binding.applicationTabLayout.selectedTabPosition)?.text == "All") {
                                tempInsApplicationList.add(insurance)
                            }
                        }
                    }
                    changeView(tempInsApplicationList)
                } else {
                    changeView(insApplicationList)
                }
                return true
            }
        })

        binding.btnApplicationListFilterCustView.setOnClickListener() {
            if(binding.filterLayout.visibility == View.GONE){
                binding.filterLayout.visibility = View.VISIBLE

                binding.applicationTabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        var selectedApplication : ArrayList<InsuranceApplication> = ArrayList<InsuranceApplication>()
                        when (tab?.position) {
                            0 -> {
                                binding.searchApplicationCustView.setQuery("", false)
                                binding.searchApplicationCustView.clearFocus()
                                selectedApplication = insApplicationList
                            }
                            1 -> {
                                selectedApplication = insApplicationList.filter{ s -> s.applicationStatus == "Pending"} as ArrayList<InsuranceApplication>
                                selectedApplication = selectedApplication.filter{ s -> s.applicationID!!.contains(binding.searchApplicationCustView.query.toString())} as ArrayList<InsuranceApplication>
                            }
                            2 -> {
                                selectedApplication = insApplicationList.filter{ s -> s.applicationStatus == "Accepted"} as ArrayList<InsuranceApplication>
                                selectedApplication = selectedApplication.filter{ s -> s.applicationID!!.contains(binding.searchApplicationCustView.query.toString())} as ArrayList<InsuranceApplication>
                            }
                            3 -> {
                                selectedApplication = insApplicationList.filter{ s -> s.applicationStatus == "Rejected"} as ArrayList<InsuranceApplication>
                                selectedApplication = selectedApplication.filter{ s -> s.applicationID!!.contains(binding.searchApplicationCustView.query.toString())} as ArrayList<InsuranceApplication>
                            }
                        }

                        changeView(selectedApplication)

                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        binding.searchApplicationCustView.setQuery("", false)
                        binding.searchApplicationCustView.clearFocus()
                    }

                })

            } else  {
                binding.filterLayout.visibility = View.GONE
            }

        }

        changeView(insApplicationList)

        return binding.root
    }

    private fun loadData() {
        insuranceApplicationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    insApplicationList.clear()
                    for (insuranceSnapshot in snapshot.children) {

                        if (insuranceSnapshot.child("referralID").value.toString() == CheckUser().getCurrentUserUID()) {

                            val applicationID: String =
                                insuranceSnapshot.child("applicationID").value.toString()
                            val applicationAppliedDate: Date = Date(
                                insuranceSnapshot.child("applicationAppliedDate")
                                    .child("time").value as Long
                            )
                            val insuranceID: String =
                                insuranceSnapshot.child("insuranceID").value.toString()
                            val referralID: String =
                                insuranceSnapshot.child("referralID").value.toString()
                            val insuranceStatus: String =
                                insuranceSnapshot.child("applicationStatus").value.toString()

                            val insApp = InsuranceApplication(
                                applicationID,
                                insuranceID,
                                referralID,
                                applicationAppliedDate,
                                insuranceStatus,
                                false,
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                false
                            )

                            insApplicationList.add(insApp)
                        }

                        binding.tvNoRecordFound.visibility = View.GONE
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                        binding.rvInsApplicationCustView.visibility = View.VISIBLE
                        binding.rvInsApplicationCustView.adapter?.notifyDataSetChanged()
                    }

                } else {
                    insApplicationList.clear()
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.tvNoRecordFound.visibility = View.VISIBLE
                    binding.rvInsApplicationCustView.visibility = View.INVISIBLE
                    binding.rvInsApplicationCustView.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun changeView(insuranceApplicationList: List<InsuranceApplication>) {
        adapterInsApp = InsuranceApplicationAdapter(insuranceApplicationList, InsuranceApplicationAdapter.ViewListener{
                applicationID,insuranceID -> val it = view

            showLoading()

            Handler().postDelayed({
                hideLoading()
                val action = ListInsuranceApplicationCustViewFragmentDirections.actionListInsuranceApplicationCustViewFragmentToViewInsuranceApplicationCustFragment(insuranceID, applicationID)
                view?.let { Navigation.findNavController(it).navigate(action) }
            }, 3000)

        })

        binding.rvInsApplicationCustView.adapter = adapterInsApp
        binding.rvInsApplicationCustView.adapter!!.notifyDataSetChanged()
    }

    private fun hideLoading() {
        loadingDialog?.let { if(it.isShowing) it.cancel() }
    }

    private fun showLoading() {
        hideLoading()
        loadingDialog = MyLottie.showLoadingDialog(requireContext())
    }
}