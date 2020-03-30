package com.example.fleet.model.fuel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FuelListResponse {
    @SerializedName("code")
    @Expose
    var code : Int? = null
    @SerializedName("message")
    @Expose
    var message : String? = null
    @SerializedName("body")
    @Expose
    var data : ArrayList<Data>? = null

    /*{"code":200,"message":"Fuel list fetch successfully","body":
    [{"id":"a91d414f-a9ec-4e6a-9720-9fe535ef6836","vehicleId":"11eed68b-12b7-48aa-a5c5-be4a2e7ddc73",
    "companyId":"021a728f-138c-4d9b-a1a6-8d02fd14922f","date":"2020-03-09","odometer":"testing","partialFuelup":1,"price":"12500","vendorId":"e3915702-1673-4c2e-829d-2c090a093e8f",
    "invoiceNumber":"KL5236","invoiceImage":"public/company/1584522153153.jpg","createdAt":1584508479,"updatedAt":1584508479,
    "vehicle":{"id":"11eed68b-12b7-48aa-a5c5-be4a2e7ddc73","name":"new vehicle","regNumber":"new vehicle"},
    "vendor":{"id":"e3915702-1673-4c2e-829d-2c090a093e8f","vendorName":"VENDOR3"}}]}*/
    inner class Data {
        @SerializedName("vehicle")
        @Expose
        var vehicleDetail : Vehicle? = null
        @SerializedName("id")
        @Expose
        var fuel_id : String? = null
        @SerializedName("vehicleId")
        @Expose
        var vehicle_id : String? = null
        @SerializedName("companyId")
        @Expose
        var companyId : String? = null
        @SerializedName("vendorId")
        @Expose
        var vendor_id : String? = null
        @SerializedName("invoiceNumber")
        @Expose
        var invoice_number : String? = null
        @SerializedName("invoiceImage")
        @Expose
        var invoice_Image : String? = null
        @SerializedName("date")
        @Expose
        var entry_date : String? = null
        @SerializedName("odometer")
        @Expose
        var odometer : String? = null
        @SerializedName("partialFuelup")
        @Expose
        var partial_fuelup : String? = null
        @SerializedName("price")
        @Expose
        var price : String? = null
        @SerializedName("createdAt")
        @Expose
        var createdAt : String? = null
        @SerializedName("updatedAt")
        @Expose
        var updatedAt : String? = null
        @SerializedName("vendor_name")
        @Expose
        var vendor_name : String? = null
        @SerializedName("vendor_Type")
        @Expose
        var vendor_Type : String? = null
        @SerializedName("vendor_email")
        @Expose
        var vendor_email : String? = null
        @SerializedName("vehicle_name")
        @Expose
        var vehicle_name : String? = null
        @SerializedName("vehicle_type")
        @Expose
        var vehicle_type : String? = null
        @SerializedName("vehicle_model")
        @Expose
        var vehicle_model : String? = null
        @SerializedName("vehicle_group")
        @Expose
        var vehicle_group : String? = null
        @SerializedName("fuel_type")
        @Expose
        var fuel_type : String? = null
        @SerializedName("fuel_measure")
        @Expose
        var fuel_measure : String? = null
    }

    inner class Vehicle {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("regNumber")
        @Expose
        var regNumber : String? = null
    }
}
