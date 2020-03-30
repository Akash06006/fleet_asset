package com.example.fleet.model.home

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class JobsResponse {
    @SerializedName("code")
    @Expose
    var code : Int? = null
    @SerializedName("message")
    @Expose
    var message : String? = null
    @SerializedName("body")
    @Expose
    var data : ArrayList<Data>? = null

    /*{"id":"26f44e62-9c5e-4a26-97fe-357eb239f6b7","fromLocation":"SCF, Sector 118, Phase 3B-2, Sector 60, Sahibzada Ajit Singh Nagar
    , Punjab 160055, India","toLocation":"Mohali 7 Phase, सेक्टर 61, साहिबजादा अजीत सिंह नगर, 160062, India","
    fromLat":"30.7083219","toLat":"30.7017355","fromLongt":"76.723126","toLongt":"76.7247759","distance":"0.75",
    "scheduleDatetime":"2020-03-30T18:03:00.000Z","employeeId":"1409dbcc-dcec-4b19-b5d7-287b68ea4d9f"
    ,"progressStatus":0,"acceptStatus":0,"jobTypeId":"72de2fd1-5953-4379-ad5d-97eb6c26cae0","passengers":"5","loadTones":"null",
    "vehicleId":"075d7726-5588-4756-a714-edd16151a7e4","jobType":{"id":"72de2fd1-5953-4379-ad5d-97eb6c26cae0","jobType":"Taxi"},
    "employee":{"id":"1409dbcc-dcec-4b19-b5d7-287b68ea4d9f","firstName":"Akashhh","lastName":"Gharuuu"},
    "vehicle":{"id":"075d7726-5588-4756-a714-edd16151a7e4","name":"test1","regNumber":"qweryd"}}*/
    inner class Data {
        @SerializedName("jobType")
        @Expose
        var jobType : JobType? = null
        @SerializedName("jobId")
        @Expose
        var jobId : String? = null
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("employeeId")
        @Expose
        var employeeId : String? = null
        @SerializedName("jobTypeId")
        @Expose
        var jobTypeId : String? = null
        @SerializedName("fromLocation")
        @Expose
        var from_location : String? = null
        @SerializedName("fromLat")
        @Expose
        var to_lat : String? = null
        @SerializedName("toLat")
        @Expose
        var toLat : String? = null
        @SerializedName("fromLongt")
        @Expose
        var to_longt : String? = null
        @SerializedName("toLongt")
        @Expose
        var toLongt : String? = null
        @SerializedName("toLocation")
        @Expose
        var to_location : String? = null
        @SerializedName("distance")
        @Expose
        var distance : String? = null
        @SerializedName("scheduleDatetime")
        @Expose
        var scheduleDatetime : String? = null
        @SerializedName("assignedDriver")
        @Expose
        var assignedDriver : String? = null
        @SerializedName("progressStatus")
        @Expose
        var progressStatus : String? = null
        @SerializedName("acceptStatus")
        @Expose
        var acceptStatus : String? = null
      /*  @SerializedName("jobType")
        @Expose
        var jobType : String? = null*/
        @SerializedName("passengers")
        @Expose
        var passengers : String? = null
        @SerializedName("loadTones")
        @Expose
        var loadTones : String? = null
        @SerializedName("vehicleId")
        @Expose
        var vehicleId : String? = null
        @SerializedName("createdAt")
        @Expose
        var createdAt : String? = null
        @SerializedName("updatedAt")
        @Expose
        var updatedAt : String? = null
        @SerializedName("vehicle_name")
        @Expose
        var vehicle_name : String? = null
        @SerializedName("vehicle_type")
        @Expose
        var vehicle_type : String? = null
        @SerializedName("vehicle_model")
        @Expose
        var vehicle_model : String? = null
        @SerializedName("vehicle_manufacture")
        @Expose
        var vehicle_manufacture : String? = null
        @SerializedName("color")
        @Expose
        var color : String? = null
        @SerializedName("vehicle_image")
        @Expose
        var vehicle_image : String? = null
        @SerializedName("vehicle_group")
        @Expose
        var vehicle_group : String? = null
        @SerializedName("reg_num")
        @Expose
        var reg_num : String? = null
        @SerializedName("engine_num")
        @Expose
        var engine_num : String? = null
        @SerializedName("chassis_num")
        @Expose
        var chassis_num : String? = null
        @SerializedName("fuel_type")
        @Expose
        var fuel_type : String? = null
        @SerializedName("fuel_measure")
        @Expose
        var fuel_measure : String? = null
        @SerializedName("track")
        @Expose
        var track : String? = null
        @SerializedName("meter")
        @Expose
        var meter : String? = null
        @SerializedName("created_at")
        @Expose
        var created_at : String? = null
        @SerializedName("updated_at")
        @Expose
        var updated_at : String? = null

    }

    inner class JobType {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("jobType")
        @Expose
        var jobType : String? = null
    }
}
