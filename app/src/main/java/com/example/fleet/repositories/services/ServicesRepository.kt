package com.example.fleet.repositories.services

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.example.fleet.R
import com.example.fleet.api.ApiClient
import com.example.fleet.api.ApiResponse
import com.example.fleet.api.ApiService
import com.example.fleet.application.MyApplication
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.model.CommonModel
import com.example.fleet.model.services.ServicesListResponse
import com.example.fleet.model.vendor.VendorListResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.HashMap

class ServicesRepository {
    private var data : MutableLiveData<ServicesListResponse>? = null
    private var data1 : MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()
    private var data2 : MutableLiveData<VendorListResponse>? = null

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
        data2 = MutableLiveData()
    }

    fun getServicesList(serviceType : String) : MutableLiveData<ServicesListResponse> {
        if (!TextUtils.isEmpty(serviceType)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<ServicesListResponse>(
                                "" + mResponse.body(),
                                ServicesListResponse::class.java
                            )
                        else {
                            gson.fromJson<ServicesListResponse>(
                                mResponse.errorBody()!!.charStream(),
                                ServicesListResponse::class.java
                            )
                        }
                        data!!.postValue(loginResponse)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().getServicesList(serviceType)
            )
        }
        return data!!

    }

    fun updateService(
        hashMap : HashMap<String, RequestBody>?,
        image : MultipartBody.Part?
    ) : MutableLiveData<CommonModel> {
        if (hashMap != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
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

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data1!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callUpdateService(hashMap, image)
            )
        }
        return data1!!

    }

    fun getVendorList() : MutableLiveData<VendorListResponse> {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse : Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<VendorListResponse>(
                            "" + mResponse.body(),
                            VendorListResponse::class.java
                        )
                    else {
                        gson.fromJson<VendorListResponse>(
                            mResponse.errorBody()!!.charStream(),
                            VendorListResponse::class.java
                        )
                    }
                    data2!!.postValue(loginResponse)
                }

                override fun onError(mKey : String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    data2!!.postValue(null)
                }

            }, ApiClient.getApiInterface().getVendorList(1,1000)
        )

        return data2!!

    }

}