package com.example.fleet.repositories

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
import com.example.fleet.model.asset.JobdetailResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.util.HashMap

class AssetJobDetailRepository {
    private var data1 : MutableLiveData<JobdetailResponse>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data1 = MutableLiveData()

    }


    fun getJobDetail(jsonObject : String) : MutableLiveData<JobdetailResponse> {
        if (!TextUtils.isEmpty(jsonObject)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val jobDetailResponse = if (mResponse.body() != null)
                            gson.fromJson<JobdetailResponse>(
                                "" + mResponse.body(),
                                JobdetailResponse::class.java
                            )
                        else {
                            gson.fromJson<JobdetailResponse>(
                                mResponse.errorBody()!!.charStream(),
                                JobdetailResponse::class.java
                            )
                        }
                        data1!!.postValue(jobDetailResponse)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data1!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().getJobDetail(jsonObject)
            )

        }
        return data1!!

    }

}