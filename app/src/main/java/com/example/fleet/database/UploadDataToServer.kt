package com.example.fleet.database

import android.app.Application
import android.os.Handler
import android.util.Log
import com.example.fleet.application.MyApplication
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.repositories.home.HomeJobsRepository
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.socket.SocketClass
import com.example.fleet.socket.SocketInterface
import com.google.gson.JsonObject
import org.json.JSONObject

class UploadDataToServer : SocketInterface {
    private var socket = SocketClass.socket
    // private lateinit var locationsViewModel : LocationsViewModel
    val latLongList = arrayListOf<JsonObject>()
    val toBeUpdatedList = arrayListOf<Int>()
    var jobID = ""
    var comingFrom = "track"
    var repo = LocationRepository()

    // LocationRepository(LocationsDetailRoomDatabase.getDatabase(MyApplication.instance).jobLocationsDetailsDao())
    init {
        socket.updateSocketInterface(this)
        Log.e("Connect Socket", "Upload Data To Server ")
        socket.onConnect()

    }

    fun synchData(jobId : String, cameFrom : String) {
        val jobLocationsDetailsDao =
            LocationsDetailRoomDatabase.getDatabase(MyApplication.instance).jobLocationsDetailsDao()
        // repo = LocationRepository()
        //  this.locationsViewModel = locationsViewModel
        comingFrom = cameFrom
        if (comingFrom.equals("track")) {
            jobID = jobId
            if (UtilsFunctions.isNetworkConnected()) {
                if (latLongList.size == 0 && GlobalConstants.JOB_STARTED == "true") {
                    var records = repo.getLimitedRecords(jobId)/*.allLocations("")*/
                    if (records.size > 19) {
                        for (item in records) {
                            var mJsonObject = JsonObject()
                            mJsonObject.addProperty(
                                "lat", item.job_lat
                            )
                            mJsonObject.addProperty(
                                "long", item.job_long
                            )
                            latLongList.add(mJsonObject)
                            toBeUpdatedList.add(item.id)
                        }
                        if (latLongList.size > 0) {
                            Handler().postDelayed({
                                callSocketMethods("updateLocation", latLongList, jobId)
                            }, 10)
                        }

                    }

                } else if (GlobalConstants.JOB_STARTED == "false") {
                    var records = repo.getAllRecords(jobId)
                    for (item in records) {
                        var mJsonObject = JsonObject()
                        mJsonObject.addProperty(
                            "lat", item.job_lat
                        )
                        mJsonObject.addProperty(
                            "long", item.job_long
                        )
                        latLongList.add(mJsonObject)
                        toBeUpdatedList.add(item.id)
                    }
                    if (latLongList.size > 0) {
                        Handler().postDelayed({
                            callSocketMethods("updateLocation", latLongList, jobId)
                        }, 10)
                    }
                }
            }
        } else {
            var jobDetails = repo.getAllJobs()
            if (jobDetails.size > 0) {
                jobID = jobDetails[0].job_id!!
                var records = repo.getAllRecords(jobID)
                for (item in records) {
                    var mJsonObject = JsonObject()
                    mJsonObject.addProperty(
                        "lat", item.job_lat
                    )
                    mJsonObject.addProperty(
                        "long", item.job_long
                    )
                    latLongList.add(mJsonObject)
                    toBeUpdatedList.add(item.id)
                }
                if (latLongList.size > 0) {
                    Handler().postDelayed({
                        callSocketMethods("updateLocation", latLongList, jobID)
                    }, 10)
                }
            }
        }
    }

    private fun callSocketMethods(
        methodName : String,
        list : ArrayList<JsonObject>, jobId : String?
    ) {
        val object5 = JSONObject()
        when (methodName) {
            "updateLocation" -> try {
                object5.put("methodName", methodName)
                object5.put("lat_long", list.toString())
                //  object5.put("longitude", mLongitude)
                object5.put(
                    "driverId", SharedPrefClass()!!.getPrefValue(
                        MyApplication.instance,
                        GlobalConstants.USERID
                    ).toString()
                )
                object5.put("jobId", jobId)
                socket.sendDataToServer(methodName, object5)
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun onSocketConnect(vararg args : Any) {
        //OnSocket Connect Call It
        Log.e("Socket Status : ", "Connected")
    }

    override fun onSocketDisconnect(vararg args : Any) {
        // //OnSocket Disconnect Call It
        Log.e("Socket Status : ", "Disconnected")
    }

    override fun onSocketCall(onMethadCall : String, vararg jsonObject : Any) {
        val serverResponse = jsonObject[0] as JSONObject
        var methodName = serverResponse.get("method")
        Log.e("", "serverResponse: " + serverResponse)
        try {
            when (methodName) {
                "updateLocation" -> try {
                    //Update entry in table
                    if (comingFrom.equals("track")) {
                        var count = 0
                        for (item in toBeUpdatedList) {
                            repo.update("1", item)
                            count++
                            if (count == toBeUpdatedList.size) {
                                Log.e("", "LatLong : " + "Upload")
                                if (GlobalConstants.JOB_STARTED == "false") {
                                    repo.updateJobSynchStatus("1", jobID.toInt())
                                }
                                toBeUpdatedList.clear()
                                latLongList.clear()
                            }
                        }
                    } else {
                        var count = 0
                        for (item in toBeUpdatedList) {
                            repo.update("1", item)
                            count++
                            if (count == toBeUpdatedList.size) {
                                repo.updateJobSynchStatus("1", jobID.toInt())

                                toBeUpdatedList.clear()
                                latLongList.clear()
                                var homeJobsRepository = HomeJobsRepository()
                                var jsonObject = JsonObject()
                                jsonObject.addProperty("employeeId", SharedPrefClass()!!.getPrefValue(MyApplication.instance, GlobalConstants.USERID) as String)
                                jsonObject.addProperty("progressStatus", "1")
                                jsonObject.addProperty("id", jobID)
                                homeJobsRepository.startCompleteJob(jsonObject)
                                //   mIsUpdating.postValue(true)
                                synchData("0", "offline")
                            }
                        }
                    }
                } catch (e1 : Exception) {
                    e1.printStackTrace()
                }
            }
            //}
        } catch (e : Exception) {
            e.printStackTrace()
        }

    }

}