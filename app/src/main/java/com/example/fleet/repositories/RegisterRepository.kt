package com.example.fleet.repositories

import androidx.lifecycle.MutableLiveData
import com.example.fleet.R
import com.example.fleet.api.ApiClient
import com.example.fleet.api.ApiResponse
import com.example.fleet.api.ApiService
import com.example.fleet.application.MyApplication
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.model.CommonModel
import com.example.fleet.model.LoginResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.util.HashMap

class RegisterRepository {
    private var data : MutableLiveData<LoginResponse> = MutableLiveData()
    private val gson = GsonBuilder().serializeNulls().create()



    fun getRegisterData(hashMap :HashMap<String, RequestBody>? ,image: MultipartBody.Part?) : MutableLiveData<LoginResponse> {
        if (hashMap != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                    object : ApiResponse<JsonObject> {
                        override fun onResponse(mResponse : Response<JsonObject>) {
                            val loginResponse = if (mResponse.body() != null)
                                gson.fromJson<LoginResponse>("" + mResponse.body(), LoginResponse::class.java)
                            else {
                                gson.fromJson<LoginResponse>(
                                        mResponse.errorBody()!!.charStream(),
                                        LoginResponse::class.java
                                )
                            }


                            data!!.postValue(loginResponse)

                        }

                        override fun onError(mKey : String) {
                            UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                            data!!.postValue(null)

                        }

                    }, ApiClient.getApiInterface().finishRegistartion(hashMap,image)

            )

        }
        return data!!

    }


}