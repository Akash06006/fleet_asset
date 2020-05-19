package com.example.fleet.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.model.CommonModel
import com.example.fleet.model.LoginResponse
import com.example.fleet.model.asset.JobdetailResponse
import com.example.fleet.repositories.AssetJobDetailRepository
import com.example.fleet.repositories.profile.ProfileRepository
import com.example.fleet.viewmodels.BaseViewModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AssetJobDetailViewModel : BaseViewModel() {
    private var jobDetail = MutableLiveData<JobdetailResponse>()
    private var assetJobDetailRepository = AssetJobDetailRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        if (UtilsFunctions.isNetworkConnectedWithoutToast()) {
            jobDetail = assetJobDetailRepository.getJobDetail("")
        }
    }

    fun getDetail() : LiveData<JobdetailResponse> {
        return jobDetail!!
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

    fun getjobDetail(mJsonObject : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            jobDetail = assetJobDetailRepository.getJobDetail(mJsonObject)
            mIsUpdating.postValue(true)
        }

    }

}