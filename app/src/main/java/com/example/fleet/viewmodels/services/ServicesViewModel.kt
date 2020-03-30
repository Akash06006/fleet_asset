package com.example.fleet.viewmodels.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.model.CommonModel
import com.example.fleet.model.services.ServicesListResponse
import com.example.fleet.model.vendor.VendorListResponse
import com.example.fleet.repositories.services.ServicesRepository
import com.example.fleet.viewmodels.BaseViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ServicesViewModel : BaseViewModel() {
    private var serviceslist = MutableLiveData<ServicesListResponse>()
    private var updateService = MutableLiveData<CommonModel>()
    private var servicesRepository = ServicesRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()
    private var vendorList = MutableLiveData<VendorListResponse>()

    init {
        if (UtilsFunctions.isNetworkConnectedWithoutToast()) {
            serviceslist = servicesRepository.getServicesList("")
            updateService=servicesRepository.updateService(null,null)
            vendorList = servicesRepository.getVendorList()
        }

    }

    fun getServicesList() : LiveData<ServicesListResponse> {
        return serviceslist
    }

    fun updateServiceStatus() : LiveData<CommonModel> {
        return updateService
    }

    fun getVendorList() : LiveData<VendorListResponse> {
        return vendorList
    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return btnClick
    }

    override fun clickListener(v : View) {
        btnClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun getServices(mJsonObject : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            serviceslist = servicesRepository.getServicesList(mJsonObject)
            mIsUpdating.postValue(true)
        }

    }

    fun updateService(hashMap : HashMap<String, RequestBody>, image : MultipartBody.Part?) {
        if (UtilsFunctions.isNetworkConnected()) {
            updateService = servicesRepository.updateService(hashMap, image)
            mIsUpdating.postValue(true)

        }
    }

}