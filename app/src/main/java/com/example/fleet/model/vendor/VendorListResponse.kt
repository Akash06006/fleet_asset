package com.example.fleet.model.vendor

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VendorListResponse {
    @SerializedName("code")
    @Expose
    var code : Int? = null
    @SerializedName("message")
    @Expose
    var message : String? = null
    @SerializedName("body")
    @Expose
    var data : ArrayList<Data>? = null

    /*{"id":"480bfe78-4bb5-4af4-8be8-28803a2c1a65","vendorName":"VENDOR2","vendorType":"1","vContactName":"Saira",
    "vContactNo":"+9121232434342","vendorEmail":" ","vendorWebsite":"","vendorAddress":"Mohali,India",
    "companyId":"021a728f-138c-4d9b-a1a6-8d02fd14922f","userId":"c7d67a48-cbe6-4d45-9c03-0306d868e349",
    "createdAt":1584423863,"updatedAt":1584423863}*/
    inner class Data {
        @SerializedName("id")
        @Expose
        var vendor_id : String? = null
        @SerializedName("companyId")
        @Expose
        var companyId : String? = null
        @SerializedName("vendorName")
        @Expose
        var vendor_name : String? = null
        @SerializedName("vendorType")
        @Expose
        var vendor_type : String? = null
        @SerializedName("vContactName")
        @Expose
        var vcontact_fname : String? = null
        @SerializedName("vcontact_lname")
        @Expose
        var vcontact_lname : String? = null
        @SerializedName("vContactNo")
        @Expose
        var vcontact_no : String? = null
        @SerializedName("vendorEmail")
        @Expose
        var vendor_email : String? = null
        @SerializedName("vendorWebsite")
        @Expose
        var vendor_website : String? = null
        @SerializedName("vendorAddress")
        @Expose
        var vendor_address : String? = null
        @SerializedName("createdAt")
        @Expose
        var createdAt : String? = null
        @SerializedName("updatedAt")
        @Expose
        var updatedAt : String? = null

    }
}
