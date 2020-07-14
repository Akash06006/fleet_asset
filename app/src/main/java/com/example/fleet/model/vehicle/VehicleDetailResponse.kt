package com.example.fleet.model.vehicle

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VehicleDetailResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("body")
    @Expose
    var data: Data? = null


    inner class Data {
        @SerializedName("vehicleTypeId")
        @Expose
        var vehicleTypeId: String? = null
        @SerializedName("fuelTypeId")
        @Expose
        var fuelTypeId: String? = null
        @SerializedName("insurexpirydate")
        @Expose
        var insurexpirydate: String? = null
        @SerializedName("insurcompanyname")
        @Expose
        var insurcompanyname: String? = null
        @SerializedName("insurnumber")
        @Expose
        var insurnumber: String? = null
        @SerializedName("fuelMeasurementId")
        @Expose
        var fuelMeasurementId: String? = null
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
        @SerializedName("image")
        @Expose
        var image: String? = null
        @SerializedName("regNumber")
        @Expose
        var regNumber: String? = null
        @SerializedName("engineNumber")
        @Expose
        var engineNumber: String? = null
        @SerializedName("chasisNumber")
        @Expose
        var chasisNumber: String? = null
        @SerializedName("meter")
        @Expose
        var meter: String? = null
        @SerializedName("vehicleGroup")
        @Expose
        var vehicleGroup: String? = null

    }


}
