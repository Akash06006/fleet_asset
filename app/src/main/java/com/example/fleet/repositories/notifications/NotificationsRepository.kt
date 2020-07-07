package com.example.fleet.repositories.notifications

import android.text.TextUtils
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
import com.example.fleet.model.notificaitons.NotificationsListResponse
import com.example.fleet.model.vehicle.VehicleListResponse
import com.example.fleet.model.vendor.VendorListResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.util.HashMap

class NotificationsRepository {
    private var data : MutableLiveData<NotificationsListResponse>? = null
    private var data1 : MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
    }

    fun getNotificationsList(userId : String) : MutableLiveData<NotificationsListResponse> {
        if (!TextUtils.isEmpty(userId)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<NotificationsListResponse>(
                                "" + mResponse.body(),
                                NotificationsListResponse::class.java
                            )
                        else {
                            gson.fromJson<NotificationsListResponse>(
                                mResponse.errorBody()!!.charStream(),
                                NotificationsListResponse::class.java
                            )
                        }
                        data!!.postValue(loginResponse)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().getNotificationList(userId)
            )
        }
        return data!!
    }

    fun clearAllNotifications(id : String) : MutableLiveData<CommonModel> {
        if (!TextUtils.isEmpty(id)) {
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

                }, ApiClient.getApiInterface().clearAllNotification(id)
            )
        }
        return data1!!
    }

}