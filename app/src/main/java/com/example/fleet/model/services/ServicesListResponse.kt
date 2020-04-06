package com.example.fleet.model.services

import com.example.fleet.model.vehicle.VehicleListResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ServicesListResponse {
    @SerializedName("code")
    @Expose
    var code : Int? = null
    @SerializedName("message")
    @Expose
    var message : String? = null
    @SerializedName("body")
    @Expose
    var data : ArrayList<Data>? = null

    /*{"id":"4022804e-2401-440e-b475-254c8cb92eda",
    "serviceDate":"2020-03-29T00:00:00.000Z","serviceFor":"0",
    "serviceType":"Full","renewalType":"","odometer":"12",
    "vehicleId":"075d7726-5588-4756-a714-edd16151a7e4","vendorId":"0133acf4-477a-4f8d-93b4-5915f60c7cad",
    "comments":"Service testing","laborPrice":"10","partsPrice":"10","tax":"10","totalPrice":"30","invoiceNumber":"",
    "invoiceImage":"http://camonher.infinitywebtechnologies.com:9064/1585636049070_docx.png","companyId":"021a728f-138c-4d9b-a1a6-8d02fd14922f","userId":"c7d67a48-cbe6-4d45-9c03-0306d868e349","status":0,"createdAt":1585635955,"updatedAt":1585635955,"vehicle":{"id":"075d7726-5588-4756-a714-edd16151a7e4","vehicleTypeId":"1b9cbc2a-6b03-4849-a958-385a2cd928be","fuelTypeId":"0ffbec46-e4fd-4af9-9419-f0e75ee9b732","fuelMeasurementId":"1b3725ab-2127-4ab4-a3fb-8a241021daeb","trackUsageId":"ab5b1d88-ccb4-4f98-a2ea-224bb182fcc2","name":"test1","model":"218447","yom":"2018","color":"Brown","image":"public/company/1585044203327.jpg","regNumber":"qweryd","engineNumber":"ertwwewwewe","chasisNumber":"fdfdfdfdf","meter":"no","currentLat":"","currentLongt":"","vehicleGroup":"test","userId":"c7d67a48-cbe6-4d45-9c03-0306d868e349","companyId":"021a728f-138c-4d9b-a1a6-8d02fd14922f","status":0,"createdAt":1584175407,"updatedAt":1584175407},
    "vendor":{"id":"0133acf4-477a-4f8d-93b4-5915f60c7cad","vendorName":"VENDOR4","vendorType":"1","vContactName":"Kuldeep","vContactNo":"+9121232434342","vendorEmail":" ","vendorWebsite":"","vendorAddress":"Mohali,India","companyId":"021a728f-138c-4d9b-a1a6-8d02fd14922f",
    "userId":"c7d67a48-cbe6-4d45-9c03-0306d868e349","createdAt":1584423863,"updatedAt":1584423863}}*/
    inner class Data {
        @SerializedName("vendor")
        @Expose
        var vendor : Vendor? = null
        @SerializedName("vehicle")
        @Expose
        var vehicle :Vehicle? = null
        @SerializedName("id")
        @Expose
        var service_id : String? = null
        @SerializedName("serviceDate")
        @Expose
        var service_date : String? = null
        @SerializedName("odometer")
        @Expose
        var odometer : String? = null
        @SerializedName("vehicle_id")
        @Expose
        var vehicle_id : String? = null
   /*     @SerializedName("vendorId")
        @Expose
        var vendor_id : String? = null*/
        @SerializedName("comments")
        @Expose
        var comments : String? = null
        @SerializedName("labor_price")
        @Expose
        var labor_price : String? = null
        @SerializedName("parts_price")
        @Expose
        var parts_price : String? = null
        @SerializedName("tax")
        @Expose
        var tax : String? = null
        @SerializedName("total_price")
        @Expose
        var total_price : String? = null
        @SerializedName("invoice_number")
        @Expose
        var invoice_number : String? = null
        @SerializedName("invoice_image")
        @Expose
        var invoice_image : String? = null
        @SerializedName("created_at")
        @Expose
        var created_at : String? = null
        @SerializedName("updated_at")
        @Expose
        var updated_at : String? = null
        /*@SerializedName("vendor_name")
        @Expose
        var vendor_name : String? = null*/
        @SerializedName("vendor_Type")
        @Expose
        var vendor_Type : String? = null
        @SerializedName("vendor_email")
        @Expose
        var vendor_email : String? = null
        @SerializedName("vehicle_name")
        @Expose
        var vehicle_name : String? = null

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
        @SerializedName("serviceFor")
        @Expose
        var service_for : String? = null

    }

    /*"vendor":{"id":"0133acf4-477a-4f8d-93b4-5915f60c7cad","vendorName":"VENDOR4","vendorType":"1","vContactName":"Kuldeep",
    "vContactNo":"+9121232434342","vendorEmail":" ","vendorWebsite":"","vendorAddress":"Mohali,India","companyId":"021a728f-138c-4d9b-a1a6-8d02fd14922f",
        "userId":"c7d67a48-cbe6-4d45-9c03-0306d868e349","createdAt":1584423863,"updatedAt":1584423863}*/
    inner class Vendor {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("vendorName")
        @Expose
        var vendorName : String? = null
        @SerializedName("vendorType")
        @Expose
        var vendorType : String? = null
        @SerializedName("vContactName")
        @Expose
        var vContactName : String? = null
        @SerializedName("vContactNo")
        @Expose
        var vContactNo : String? = null
        @SerializedName("vendorEmail")
        @Expose
        var vendorEmail : String? = null
        @SerializedName("vendorAddress")
        @Expose
        var vendorAddress : String? = null
    }

    /*"vehicle": {
		"id": "075d7726-5588-4756-a714-edd16151a7e4",
		"vehicleTypeId": "1b9cbc2a-6b03-4849-a958-385a2cd928be",
		"fuelTypeId": "0ffbec46-e4fd-4af9-9419-f0e75ee9b732",
		"fuelMeasurementId": "1b3725ab-2127-4ab4-a3fb-8a241021daeb",
		"trackUsageId": "ab5b1d88-ccb4-4f98-a2ea-224bb182fcc2",
		"name": "test1",
		"model": "218447",
		"yom": "2018",
		"color": "Brown",
		"image": "public/company/1585044203327.jpg",
		"regNumber": "qweryd",
		"engineNumber": "ertwwewwewe",
		"chasisNumber": "fdfdfdfdf",
		"meter": "no",
		"currentLat": "",
		"currentLongt": "",
		"vehicleGroup": "test",
		"userId": "c7d67a48-cbe6-4d45-9c03-0306d868e349",
		"companyId": "021a728f-138c-4d9b-a1a6-8d02fd14922f",
		"status": 0,
		"createdAt": 1584175407,
		"updatedAt": 1584175407
	}*/
    inner class Vehicle {
        @SerializedName("id")
        @Expose
        var id : String? = null
       /* @SerializedName("vendorName")
        @Expose
        var vendorName : String? = null*/
    }
}
