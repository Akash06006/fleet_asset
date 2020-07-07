package com.example.fleet.viewmodels.tracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.example.fleet.application.MyApplication
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.model.CommonModel
import com.example.fleet.repositories.home.HomeJobsRepository
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.viewmodels.BaseViewModel
import com.google.gson.JsonObject

class TrackingViewModel : BaseViewModel() {
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val isClick = MutableLiveData<String>()
    private var completejob = MutableLiveData<CommonModel>()
    private var homeJobsRepository = HomeJobsRepository()

    init {
        if (UtilsFunctions.isNetworkConnectedWithoutToast()) {
            completejob = homeJobsRepository.startCompleteJob(null)
        }
    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return isClick
    }

    override fun clickListener(v : View) {
        isClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun startCompleteJob() : LiveData<CommonModel> {
        return completejob
    }
    fun startJob(status : String, jobId : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            var jsonObject = JsonObject()
            jsonObject.addProperty(
                "employeeId",
                SharedPrefClass()!!.getPrefValue(
                    MyApplication.instance,
                    GlobalConstants.USERID
                ) as String
            )
            jsonObject.addProperty("progressStatus", status)
            jsonObject.addProperty("id", jobId)
            completejob = homeJobsRepository.startCompleteJob(jsonObject)
            mIsUpdating.postValue(true)
        }

    }

}