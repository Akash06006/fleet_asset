package com.example.fleet.viewmodels.fuel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.model.CommonModel
import com.example.fleet.model.LoginResponse
import com.example.fleet.model.fuel.FuelListResponse
import com.example.fleet.model.vehicle.VehicleDetailResponse
import com.example.fleet.model.vehicle.VehicleListResponse
import com.example.fleet.model.vendor.VendorListResponse
import com.example.fleet.repositories.Fuel.FuelRepository
import com.example.fleet.viewmodels.BaseViewModel
import com.google.android.gms.common.internal.service.Common
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FuelViewModel : BaseViewModel() {
    private var vehiclelist = MutableLiveData<VehicleListResponse>()
    private var vehicleDetail = MutableLiveData<VehicleDetailResponse>()
    private var addFuelResponse = MutableLiveData<CommonModel>()
    private var updateVehicleRes = MutableLiveData<CommonModel>()
    private var getFuelListResponse = MutableLiveData<FuelListResponse>()
    private var vendorList = MutableLiveData<VendorListResponse>()
    private var fuelRepository = FuelRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {

        if (UtilsFunctions.isNetworkConnectedWithoutToast()) {
            vehiclelist = fuelRepository.getVehicleList()
            vendorList = fuelRepository.getVendorList()
            getFuelListResponse = fuelRepository.getFuelEntryList()
            addFuelResponse = fuelRepository.addFuelEntry(null, null)
            vehicleDetail = fuelRepository.getVehicleDetail("")
            updateVehicleRes = fuelRepository.updateVehicleDetail(null, null)
        }

    }

    fun getVehicleList(): LiveData<VehicleListResponse> {
        return vehiclelist
    }

    fun getVehicleDetail(): LiveData<VehicleDetailResponse> {
        return vehicleDetail
    }

    fun getFuelResponse(): LiveData<CommonModel> {
        return addFuelResponse
    }

    fun getUpdateVehicleRes(): LiveData<CommonModel> {
        return updateVehicleRes
    }

    fun getFuelList(): LiveData<FuelListResponse> {
        return getFuelListResponse
    }

    fun getVendorList(): LiveData<VendorListResponse> {
        return vendorList
    }

    override fun isLoading(): LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick(): LiveData<String> {
        return btnClick
    }

    override fun clickListener(v: View) {
        btnClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun addFuelEntry(hashMap: HashMap<String, RequestBody>, image: MultipartBody.Part?) {
        if (UtilsFunctions.isNetworkConnected()) {
            addFuelResponse = fuelRepository.addFuelEntry(hashMap, image)
            mIsUpdating.postValue(true)

        }
    }

    fun getFuelEntryList() {
        if (UtilsFunctions.isNetworkConnected()) {
            getFuelListResponse = fuelRepository.getFuelEntryList()
            mIsUpdating.postValue(true)
        }
    }

    fun getVehiclDetail(id: String) {
        if (UtilsFunctions.isNetworkConnected()) {
            vehicleDetail = fuelRepository.getVehicleDetail(id)
            mIsUpdating.postValue(true)
        }
    }

    fun getVehcileList() {
        if (UtilsFunctions.isNetworkConnected()) {
            vehiclelist = fuelRepository.getVehicleList()
            mIsUpdating.postValue(true)
        }
    }

    fun updateVehicleDetail(hashMap: HashMap<String, RequestBody>, image: MultipartBody.Part?) {
        if (UtilsFunctions.isNetworkConnected()) {
            addFuelResponse = fuelRepository.updateVehicleDetail(hashMap, image)
            mIsUpdating.postValue(true)

        }
    }

}