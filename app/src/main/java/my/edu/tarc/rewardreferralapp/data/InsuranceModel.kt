package my.edu.tarc.rewardreferralapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class InsuranceModel(InsuranceID: String, InsuranceName: String, InsuranceComp: String, InsurancePlan: String, InsuranceExpiryDate: Date, InsuranceReferral: String): ViewModel() {
    private var _InsuranceID = MutableLiveData<String>()
    val InsuranceID : LiveData<String> get() = _InsuranceID
    private var _InsuranceName = MutableLiveData<String>()
    val InsuranceName : LiveData<String> get() = _InsuranceName
    private var _InsuranceComp = MutableLiveData<String>()
    val InsuranceComp : LiveData<String> get() = _InsuranceComp
    private var _InsurancePlan = MutableLiveData<String>()
    val InsurancePlan : LiveData<String> get() = _InsurancePlan
    private var _InsuranceExpiryDate = MutableLiveData<Date>()
    val InsuranceExpiryDate : LiveData<Date> get() = _InsuranceExpiryDate
    private var _InsuranceReferral = MutableLiveData<String>()
    val InsuranceReferral : LiveData<String> get() = _InsuranceReferral

    init {
        this._InsuranceID.value = InsuranceID
        this._InsuranceName.value = InsuranceName
        this._InsuranceComp.value = InsuranceComp
        this._InsurancePlan.value = InsurancePlan
        this._InsuranceExpiryDate.value = InsuranceExpiryDate
        this._InsuranceReferral.value = InsuranceReferral
    }

    fun getInsuranceID(): String? {
        return _InsuranceID.value
    }

    fun getInsuranceName(): String ? {
        return _InsuranceName.value
    }

    fun getInsuranceComp(): String ? {
        return _InsuranceComp.value
    }
    fun getInsurancePlan(): String ? {
        return _InsurancePlan.value
    }

    fun getInsuranceExpiryDate(): Date?{
        return _InsuranceExpiryDate.value
    }

    fun getInsuranceReferral(): String ? {
        return _InsuranceReferral.value
    }

}