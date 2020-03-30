package com.example.fleet.repositories.Fuel

import androidx.lifecycle.MutableLiveData
import com.example.fleet.R
import com.example.fleet.api.ApiClient
import com.example.fleet.api.ApiResponse
import com.example.fleet.api.ApiService
import com.example.fleet.application.MyApplication
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.model.CommonModel
import com.example.fleet.model.LoginResponse
import com.example.fleet.model.fuel.FuelListResponse
import com.example.fleet.model.vehicle.VehicleListResponse
import com.example.fleet.model.vendor.VendorListResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.util.HashMap

class FuelRepository {
    private var data : MutableLiveData<LoginResponse>? = null
    private var data3 : MutableLiveData<CommonModel>? = null
    private var data1 : MutableLiveData<VehicleListResponse>? = null
    private var data2 : MutableLiveData<VendorListResponse>? = null
    private var data4 : MutableLiveData<FuelListResponse>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
        data2 = MutableLiveData()
        data3 = MutableLiveData()
        data4 = MutableLiveData()

    }

    fun getFuelEntryList() : MutableLiveData<FuelListResponse> {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse : Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<FuelListResponse>(
                            "" + mResponse.body(),
                            FuelListResponse::class.java
                        )
                    else {
                        gson.fromJson<FuelListResponse>(
                            mResponse.errorBody()!!.charStream(),
                            FuelListResponse::class.java
                        )
                    }
                    data4!!.postValue(loginResponse)
                }

                override fun onError(mKey : String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    data4!!.postValue(null)
                }

            }, ApiClient.getApiInterface().getFuelEntryList(/*jsonObject*/)
        )

        return data4!!

    }

    fun addFuelEntry(
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


                        data3!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data3!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callAddFuelEntry(hashMap, image)
            )
        }
        return data3!!

    }

    fun getVehicleList() : MutableLiveData<VehicleListResponse> {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse : Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<VehicleListResponse>(
                            "" + mResponse.body(),
                            VehicleListResponse::class.java
                        )
                    else {
                        gson.fromJson<VehicleListResponse>(
                            mResponse.errorBody()!!.charStream(),
                            VehicleListResponse::class.java
                        )
                    }
                    data1!!.postValue(loginResponse)
                }

                override fun onError(mKey : String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    data1!!.postValue(null)
                }

            }, ApiClient.getApiInterface().getVehicleList(/*jsonObject*/)
        )

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