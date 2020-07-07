package com.example.fleet.model.notificaitons

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NotificationsListResponse {
    @SerializedName("code")
    @Expose
    var code : Int? = null
    @SerializedName("message")
    @Expose
    var message : String? = null
    @SerializedName("body")
    @Expose
    var data : ArrayList<Data>? = null

    /*{"id":"29f08c41-733a-11ea-9ae8-0a14ec6e4c67","notificationTitle":"Vehicle1 Renewal Reminder",
    "notificationDescription":"Dear saira1aaa ,this is reminder from Fleet Management System about your vehicle Vehicle1(PB65Z3899) Renewal on 2020-03-01",
    "companyId":"021a728f-138c-4d9b-a1a6-8d02fd14922f",
    "employeeId":"1409dbcc-dcec-4b19-b5d7-287b68ea4d9f",
    "status":0,"createdAt":1584448412,"updatedAt":1584448412}*/
    inner class Data {
        @SerializedName("id")
        @Expose
        var notification_id : String? = null
        @SerializedName("notificationTitle")
        @Expose
        var notification_title : String? = null
        @SerializedName("notificationDescription")
        @Expose
        var notification_description : String? = null
        @SerializedName("employeeId")
        @Expose
        var employeeId : String? = null
        @SerializedName("companyId")
        @Expose
        var companyId : String? = null
        @SerializedName("user_id")
        @Expose
        var user_id : String? = null
        @SerializedName("status")
        @Expose
        var status : Int? = null
        @SerializedName("created_at")
        @Expose
        var created_at : String? = null
        @SerializedName("updated_at")
        @Expose
        var updated_at : String? = null
    }
}
