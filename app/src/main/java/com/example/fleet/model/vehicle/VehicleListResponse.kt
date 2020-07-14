package com.example.fleet.model.vehicle

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VehicleListResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("body")
    @Expose
    var data: ArrayList<Data>? = null

    /*{"id":"05428072-a779-49f8-80c8-1e36aa50a90a","vehicleId":"075d7726-5588-4756-a714-edd16151a7e4",
    "driverId":"1409dbcc-dcec-4b19-b5d7-287b68ea4d9f","companyId":"021a728f-138c-4d9b-a1a6-8d02fd14922f",
    "assignBy":"c7d67a48-cbe6-4d45-9c03-0306d868e349","status":0,"createdAt":1585571196,"updatedAt":1585571196,
    "vehicle":{"id":"075d7726-5588-4756-a714-edd16151a7e4","name":"test1","model":"218447","yom":"2018","color":"Brown","image":"public/company/1585044203327.jpg","regNumber":"qweryd",
    "engineNumber":"ertwwewwewe","vehicleGroup":"test","currentLat":"","currentLongt":""}}*/
    inner class Data {
        @SerializedName("vehicle")
        @Expose
        var vehicle: Vehicle? = null
        @SerializedName("vehicleId")
        @Expose
        var vehicle_id: String? = null
        @SerializedName("user_id")
        @Expose
        var user_id: Int? = null
        @SerializedName("vehicle_name")
        @Expose
        var vehicle_name: String? = null
        @SerializedName("vehicle_type")
        @Expose
        var vehicle_type: String? = null
        @SerializedName("vehicle_model")
        @Expose
        var vehicle_model: String? = null
        @SerializedName("vehicle_manufacture")
        @Expose
        var vehicle_manufacture: String? = null
        @SerializedName("color")
        @Expose
        var color: String? = null
        @SerializedName("vehicle_image")
        @Expose
        var vehicle_image: String? = null
        @SerializedName("vehicle_group")
        @Expose
        var vehicle_group: String? = null
        @SerializedName("reg_num")
        @Expose
        var reg_num: String? = null
        @SerializedName("engine_num")
        @Expose
        var engine_num: String? = null
        @SerializedName("chassis_num")
        @Expose
        var chassis_num: Int? = null
        @SerializedName("fuel_type")
        @Expose
        var fuel_type: String? = null
        @SerializedName("fuel_measure")
        @Expose
        var fuel_measure: String? = null
        @SerializedName("track")
        @Expose
        var track: String? = null
        @SerializedName("meter")
        @Expose
        var meter: String? = null
        @SerializedName("assigned_driver")
        @Expose
        var assigned_driver: String? = null
        @SerializedName("created_at")
        @Expose
        var created_at: String? = null
        @SerializedName("updated_at")
        @Expose
        var updated_at: String? = null
    }

    inner class Vehicle {
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("name")
        @Expose
        var name: String? = null
        @SerializedName("model")
        @Expose
        var model: String? = null
        @SerializedName("yom")
        @Expose
        var yom: String? = null
        @SerializedName("color")
        @Expose
        var color: String? = null
        @SerializedName("regNumber")
        @Expose
        var regNumber: String? = null
        @SerializedName("image")
        @Expose
        var image: String? = null
        @SerializedName("engineNumber")
        @Expose
        var engineNumber: String? = null
        @SerializedName("vehicleGroup")
        @Expose
        var vehicleGroup: String? = null
        @SerializedName("currentLat")
        @Expose
        var currentLat: String? = null
        @SerializedName("currentLongt")
        @Expose
        var currentLongt: String? = null

    }
}
