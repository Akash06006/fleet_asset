package com.example.fleet.repositories.home

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.example.fleet.R
import com.example.fleet.api.ApiClient
import com.example.fleet.api.ApiResponse
import com.example.fleet.api.ApiService
import com.example.fleet.application.MyApplication
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.model.CommonModel
import com.example.fleet.model.home.JobsResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class HomeJobsRepository {
    private var data: MutableLiveData<JobsResponse>? = null
    private var data1: MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
    }

    fun getMyJobsList(mJsonObject: JsonObject?): MutableLiveData<JobsResponse> {
        if (mJsonObject != null) {

            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                    object : ApiResponse<JsonObject> {
                        override fun onResponse(mResponse: Response<JsonObject>) {
                            val loginResponse = if (mResponse.body() != null)
                                gson.fromJson<JobsResponse>(
                                        "" + mResponse.body(),
                                        JobsResponse::class.java
                                )
                            else {
                                gson.fromJson<JobsResponse>(
                                        mResponse.errorBody()!!.charStream(),
                                        JobsResponse::class.java
                                )
                            }

                            data!!.postValue(loginResponse)

                        }

                        override fun onError(mKey: String) {
                            UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                            data!!.postValue(null)

                        }

                    }, ApiClient.getApiInterface().getJobs(mJsonObject)
            )
        }
        return data!!
    }

    fun getMyJobsHistoryList(mJsonObject: String): MutableLiveData<JobsResponse> {
        if (!TextUtils.isEmpty(mJsonObject)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                    object : ApiResponse<JsonObject> {
                        override fun onResponse(mResponse: Response<JsonObject>) {
                            val loginResponse = if (mResponse.body() != null)
                                gson.fromJson<JobsResponse>(
                                        "" + mResponse.body(),
                                        JobsResponse::class.java
                                )
                            else {
                                gson.fromJson<JobsResponse>(
                                        mResponse.errorBody()!!.charStream(),
                                        JobsResponse::class.java
                                )
                            }

                            data!!.postValue(loginResponse)

                        }

                        override fun onError(mKey: String) {
                            UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                            data!!.postValue(null)

                        }

                        // }, ApiClient.getApiInterface().getJobsHistory(mJsonObject)
                    }, ApiClient.getApiInterface().getJobsHistory1(1, 1000, mJsonObject.toInt())
            )
        }
        return data!!
    }

    fun acceptRejectJob(mJsonObject: JsonObject?): MutableLiveData<CommonModel> {
        if (mJsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                    object : ApiResponse<JsonObject> {
                        override fun onResponse(mResponse: Response<JsonObject>) {
                            val loginResponse = if (mResponse.body() != null)
                                gson.fromJson<CommonModel>(
                                        "" + mResponse.body(),
                                        CommonModel::class.java
                                )
                            else {
                                gson.fromJson<CommonModel>(
                                        mResponse.errorBody()!!.charStream(),
                                        CommonModel::class.java
                                )
                            }

                            data1!!.postValue(loginResponse)

                        }

                        override fun onError(mKey: String) {
                            UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                            data1!!.postValue(null)

                        }

                    }, ApiClient.getApiInterface().acceptRejectJob(mJsonObject)
            )
        }
        return data1!!
    }

    fun startCompleteJob(mJsonObject: JsonObject?): MutableLiveData<CommonModel> {
        if (mJsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                    object : ApiResponse<JsonObject> {
                        override fun onResponse(mResponse: Response<JsonObject>) {
                            val loginResponse = if (mResponse.body() != null)
                                gson.fromJson<CommonModel>(
                                        "" + mResponse.body(),
                                        CommonModel::class.java
                                )
                            else {
                                gson.fromJson<CommonModel>(
                                        mResponse.errorBody()!!.charStream(),
                                        CommonModel::class.java
                                )
                            }

                            data1!!.postValue(loginResponse)

                        }

                        override fun onError(mKey: String) {
                            UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                            data1!!.postValue(null)

                        }

                    }, ApiClient.getApiInterface().startCompleteJob(mJsonObject)
            )
        }
        return data1!!
    }

}