package com.example.fleet.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResponse {
    @SerializedName("code")
    @Expose
    var code : Int? = null
    @SerializedName("message")
    @Expose
    var message : String? = null
    @SerializedName("body")
    @Expose
    var data : Data? = null

    inner class Data {
        @SerializedName("assignWarehouse")
        @Expose
        var assignWarehouse : AssignWarehouse? = null
        @SerializedName("assignRoles")
        @Expose
        var assignRoles : ArrayList<AssignRoles>? = null
        @SerializedName("driver_id")
        @Expose
        var driver_id : Int? = null
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("user_id")
        @Expose
        var userId : String? = null
        @SerializedName("user_type")
        @Expose
        var userType : String? = null
        @SerializedName("profileImage")
        @Expose
        var profile_image : String? = null
        @SerializedName("profileImageUrl")
        @Expose
        var profileImageUrl : String? = null
        @SerializedName("sessionToken")
        @Expose
        var sessionToken : String? = null
        @SerializedName("status")
        @Expose
        var status : String? = null
        @SerializedName("availablity")
        @Expose
        var availablity : String? = null
        @SerializedName("countryCodename")
        @Expose
        var countryCodename : String? = null
        @SerializedName("email")
        @Expose
        var email : String? = null
        @SerializedName("password")
        @Expose
        var password : String? = null
        @SerializedName("phoneNumber")
        @Expose
        var phoneNumber : String? = null
        @SerializedName("firstName")
        @Expose
        var firstName : String? = null
        @SerializedName("lastName")
        @Expose
        var lastName : String? = null
        @SerializedName("gender")
        @Expose
        var gender : String? = null
        @SerializedName("licenseNumber")
        @Expose
        var licenseNumber : String? = null
        @SerializedName("address")
        @Expose
        var address : String? = null
        @SerializedName("licenseClass")
        @Expose
        var licenseClass : String? = null
        @SerializedName("licenseState")
        @Expose
        var licenseState : String? = null
        @SerializedName("dob")
        @Expose
        var dob : String? = null
        @SerializedName("companyId")
        @Expose
        var companyId : String? = null
        @SerializedName("authToken")
        @Expose
        var authToken : String? = null
        @SerializedName("refreshToken")
        @Expose
        var refreshToken : String? = null
        @SerializedName("deviceType")
        @Expose
        var deviceType : String? = null
        @SerializedName("deviceId")
        @Expose
        var deviceId : String? = null
        @SerializedName("jwtToken")
        @Expose
        var jwtToken : String? = null
        @SerializedName("notify_id")
        @Expose
        var notifyId : String? = null
        @SerializedName("created_at")
        @Expose
        var createdAt : String? = null
        @SerializedName("updated_at")
        @Expose
        var updatedAt : String? = null
        @SerializedName("title1")
        @Expose
        var title1 : String? = null
        @SerializedName("title2")
        @Expose
        var title2 : String? = null
        @SerializedName("licenseImage")
        @Expose
        var licenseImage : String? = null
        @SerializedName("otherimage")
        @Expose
        var otherimage : String? = null

    }

    inner class AssignWarehouse {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("wareHouseId")
        @Expose
        var wareHouseId : String? = null
        @SerializedName("employeeId")
        @Expose
        var employeeId : String? = null
        @SerializedName("companyId")
        @Expose
        var companyId : String? = null
    }

    inner class AssignRoles {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("roleTypeId")
        @Expose
        var roleTypeId : String? = null
        @SerializedName("roleType")
        @Expose
        var roleType : String? = null
    }
}
