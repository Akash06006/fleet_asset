package com.example.fleet.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.HashMap

interface ApiInterface {
    @Headers("Content-Type: application/json")
    @POST("api/loginEmployee")
    fun callLogin(@Body jsonObject : JsonObject) : Call<JsonObject>

    /*@POST("login/")
    fun callLogin(@Body jsonObject : JsonObject) : Call<JsonObject>*/
    @Multipart
    @POST("register/")
    fun finishRegistartion(
        @PartMap mHashMap : HashMap<String,
                RequestBody>, @Part image : MultipartBody.Part?
    ) : Call<JsonObject>

    @Multipart
    @PUT("api/employee/update")
    fun callUpdateProfile(
        @PartMap mHashMap : HashMap<String,
                RequestBody>, @Part image : MultipartBody.Part?
    ) : Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("checkPhoneNumber/")
    fun checkPhoneExistence(@Body jsonObject : JsonObject) : Call<JsonObject>

    @GET("api/logout")
    fun callLogout(/*@Body mJsonObject : JsonObject*/) : Call<JsonObject>

    @POST("resetpassword/")
    fun resetPassword(@Body mJsonObject : JsonObject) : Call<JsonObject>

    //@POST("resetpassword/")
    //fun getProfile(@Body mJsonObject : JsonObject) : Call<JsonObject>
    @POST("users/changepassword/")
    fun chnagePassword(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @GET("api/employee/get/{id}")
    fun getProfile(@Path("id")  id:String) : Call<JsonObject>

    @GET("driver/vehicle/latLongList")
    fun getVehicleList() : Call<JsonObject>

    @GET("service/driver/getServiceList")
    fun getServicesList(@Query("status") status : String) : Call<JsonObject>

    @GET(" api/service/list")
    fun getServicesList1(@Query("page") page : Int,@Query("limit") limit : Int) : Call<JsonObject>

    @GET("api/fuel/getList")
    fun getFuelEntryList() : Call<JsonObject>

    @GET("notification/driver/getList")
    fun getNotificationList() : Call<JsonObject>

    @DELETE("notification/driver/clearAll")
    fun clearAllNotification() : Call<JsonObject>

    @GET("api/vendor/list")
    fun getVendorList(@Query("page") page : Int,@Query("limit") limit : Int) : Call<JsonObject>

   /* @GET("job/getDriverJob")
    fun getJobs(@Query("acceptStatus") userId : String) : Call<JsonObject>*/
   @POST("api/job/getDriverJobs")
   fun getJobs(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @GET("job/driver/jobsHistory")
    fun getJobsHistory(@Query("progressStatus") userId : String) : Call<JsonObject>
    @GET("api/job/jobsHistory")
    fun getJobsHistory1(@Query("page") page : Int,@Query("limit") limit : Int,@Query("progressStatus") progressStatus : Int) : Call<JsonObject>

    @POST("api/job/changeJobStatus")
    fun startCompleteJob(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @POST("api/job/AcceptReject")
    fun acceptRejectJob(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @Multipart
    @POST("api/fuel/addFuel")
    fun callAddFuelEntry(
        @PartMap mHashMap : HashMap<String,
                RequestBody>, @Part image : MultipartBody.Part?
    ) : Call<JsonObject>

    @Multipart
    @POST("api/service/add")
    fun callUpdateService(
        @PartMap mHashMap : HashMap<String,
                RequestBody>, @Part image : MultipartBody.Part?
    ) : Call<JsonObject>

}