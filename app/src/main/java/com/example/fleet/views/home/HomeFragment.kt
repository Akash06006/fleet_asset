package com.example.fleet.views.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleet.R
import com.example.fleet.application.MyApplication
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.common.UtilsFunctions.showToastError
import com.example.fleet.common.UtilsFunctions.showToastSuccess
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.database.UploadDataToServer
import com.example.fleet.databinding.FragmentHomeBinding
import com.example.fleet.maps.FusedLocationClass
import com.example.fleet.model.CommonModel
import com.example.fleet.model.home.JobsResponse
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.socket.SocketClass
import com.example.fleet.socket.SocketInterface
import com.example.fleet.socket.TrackingActivity
import com.example.fleet.utils.BaseFragment
import com.example.fleet.utils.DialogClass
import com.example.fleet.utils.DialogssInterface
import com.example.fleet.viewmodels.home.HomeViewModel
import com.google.android.gms.location.*
import com.google.gson.JsonObject
import com.uniongoods.adapters.MyJobsListAdapter
import org.json.JSONObject

class
HomeFragment : BaseFragment(), SocketInterface, DialogssInterface {
    private var mFusedLocationClass: FusedLocationClass? = null
    private var socket = SocketClass.socket
    private var jobsList = ArrayList<JobsResponse.Data>()
    private var myJobsListAdapter: MyJobsListAdapter? = null
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private val mJsonObject = JsonObject()
    private val mJobListObject = JsonObject()
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var currentLat = ""
    var currentLong = ""
    var mJsonObjectStartJob = JsonObject()
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()

    override fun getLayoutResId(): Int {
        return R.layout.fragment_home
    }

    override fun onResume() {
        super.onResume()

    }

    override fun initView() {
        fragmentHomeBinding = viewDataBinding as FragmentHomeBinding
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        fragmentHomeBinding.homeViewModel = homeViewModel

        mFusedLocationClass = FusedLocationClass(activity)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        jobsList.clear()
        if (UtilsFunctions.isNetworkConnected()) {
            fragmentHomeBinding.rvJobs.visibility = View.VISIBLE
            // baseActivity.startProgressDialog()
            mJobListObject.addProperty(
                    "employeeId",
                    SharedPrefClass()!!.getPrefValue(activity!!, GlobalConstants.USERID) as String
            )
            mJobListObject.addProperty(
                    "acceptStatus", "1"
            )
            homeViewModel.getMyJobs(mJobListObject)
            var jobId =
                    SharedPrefClass()!!.getPrefValue(activity!!, GlobalConstants.JOBID).toString()
            if (!jobId.equals("null") && jobId.equals("0")) {
                Handler().postDelayed({
                    var upload = UploadDataToServer()
                    upload.synchData("0", "offline")
                }, 5000)
            }

        } else {
            baseActivity.stopProgressDialog()
            fragmentHomeBinding.rvJobs.visibility = View.GONE
        }

        getLastLocation()
        socket.updateSocketInterface(this)
        Log.e("Connect Socket", "Home Fragment")
        socket.onConnect()
        //acceptStatus
        mJsonObject.addProperty(
                "acceptStatus", "1"
        )


        homeViewModel.getJobs().observe(this,
                Observer<JobsResponse> { response ->
                    baseActivity.stopProgressDialog()
                    if (response != null) {
                        val message = response.message
                        when {
                            response.code == 200 -> {
                                if (response.data != null && response.data?.size!! > 0) {
                                    jobsList.addAll(response.data!!)
                                    fragmentHomeBinding.rvJobs.visibility = View.VISIBLE
                                    fragmentHomeBinding.tvNoRecord.visibility = View.GONE
                                    initRecyclerView()
                                }

                            }
                            else -> message?.let {
                                showToastError(it)
                                fragmentHomeBinding.rvJobs.visibility = View.GONE
                                fragmentHomeBinding.tvNoRecord.visibility = View.VISIBLE
                            }
                        }

                    }
                })

        homeViewModel.startCompleteJob().observe(this,
                Observer<CommonModel> { response ->
                    baseActivity.stopProgressDialog()
                    if (response != null) {
                        val message = response.message
                        when {
                            response.code == 200 -> {
                                message?.let { showToastSuccess(message!!) }
                                var status = mJsonObjectStartJob.get("status").toString()
                                if (status.equals("1")) {
                                    SharedPrefClass().putObject(
                                            MyApplication.instance.applicationContext,
                                            GlobalConstants.JOB_STARTED,
                                            "true"
                                    )
                                    GlobalConstants.JOB_STARTED = "true"
                                    SharedPrefClass().putObject(
                                            MyApplication.instance.applicationContext,
                                            GlobalConstants.JOBID,
                                            mJsonObjectStartJob.get("jobId")
                                    )
                                    val intent = Intent(activity, TrackingActivity::class.java)
                                    intent.putExtra("data", mJsonObjectStartJob.toString())
                                    activity!!.startActivity(intent)
                                    activity!!.finish()
                                }

                            }
                            else -> message?.let { showToastError(it) }
                        }

                    }
                })

        homeViewModel.acceptReject().observe(this,
                Observer<CommonModel> { response ->
                    baseActivity.stopProgressDialog()
                    if (response != null) {
                        val message = response.message
                        when {
                            response.code == 200 -> {
                                jobsList.clear()
                                homeViewModel.getMyJobs(mJobListObject)

                                UtilsFunctions.showToastSuccess(message!!)
                            }
                            else -> message?.let { UtilsFunctions.showToastError(it) }
                        }

                    }
                })
        // initRecyclerView()
        Handler().postDelayed({
            callSocketMethods("updateVehicleLocation")
        }, 2000)

    }

    private fun callSocketMethods(methodName: String) {
        val object5 = JSONObject()
        when (methodName) {
            "updateVehicleLocation" -> try {
                object5.put("methodName", methodName)
                object5.put("latitude", currentLat)
                object5.put("longitude", currentLong)
                object5.put(
                        "driverId", SharedPrefClass().getPrefValue(
                        MyApplication.instance,
                        GlobalConstants.USERID
                ).toString()
                )
                socket.sendDataToServer(methodName, object5)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun initRecyclerView() {
        myJobsListAdapter = MyJobsListAdapter(this@HomeFragment, jobsList, activity!!)
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        fragmentHomeBinding.rvJobs.layoutManager = linearLayoutManager
        fragmentHomeBinding.rvJobs.adapter = myJobsListAdapter
        fragmentHomeBinding.rvJobs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    fun cancelJob(jobId: String?, status: String) {
        mJsonObjectStartJob.addProperty(
                "acceptStatus", status
        )
        mJsonObjectStartJob.addProperty(
                "jobId", jobId
        )
        mJsonObjectStartJob.addProperty(
                "status", 0
        )
        mJsonObjectStartJob.addProperty(
                "employeeId",
                SharedPrefClass()!!.getPrefValue(activity!!, GlobalConstants.USERID) as String
        )
        confirmationDialog = mDialogClass.setDefaultDialog(
                activity!!,
                this,
                "Cancel Job",
                getString(R.string.warning_cancel_job)
        )
        confirmationDialog?.show()

    }

    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        when (mKey) {
            "Cancel Job" -> {
                baseActivity.startProgressDialog()
                confirmationDialog?.dismiss()
                homeViewModel.acceptRejectJob(mJsonObjectStartJob)

            }
        }
    }

    override fun onDialogCancelAction(mView: View?, mKey: String) {
        when (mKey) {
            "Cancel Job" -> confirmationDialog?.dismiss()
        }
    }

    fun startJob(
            jobId: String?,
            toLat: String?,
            toLongt: String?,
            trackOrStart: String
    ) {
        mJsonObjectStartJob.addProperty(
                "jobId", jobId
        )
        mJsonObjectStartJob.addProperty(
                "dest_lat", toLat.toString()
        )
        mJsonObjectStartJob.addProperty(
                "dest_long", toLongt.toString()
        )
        mJsonObjectStartJob.addProperty(
                "trackOrStart", trackOrStart
        )
        mJsonObjectStartJob.addProperty(
                "status", 1
        )

        SharedPrefClass().putObject(
                MyApplication.instance.applicationContext,
                GlobalConstants.DEST_LAT,
                toLat
        )

        SharedPrefClass().putObject(
                MyApplication.instance.applicationContext,
                GlobalConstants.DEST_LONG,
                toLongt
        )
        if (trackOrStart == activity?.resources?.getString(R.string.track)) {
            val intent = Intent(activity, TrackingActivity::class.java)
            intent.putExtra("data", mJsonObjectStartJob.toString())
            activity!!.startActivity(intent)
            activity!!.finish()
        } else {
            homeViewModel.startJob("2", jobId.toString())
        }

    }

    override fun onSocketCall(onMethadCall: String, vararg args: Any) {
        try {
            when (onMethadCall) {
                "updateVehicleLocation" -> try {
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
    }

    override fun onSocketConnect(vararg args: Any) {
        //OnSocket Connect Call It
    }

    override fun onSocketDisconnect(vararg args: Any) {
        // //OnSocket Disconnect Call It
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(activity!!) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLat = location.latitude.toString()
                        currentLong = location.longitude.toString()
                        Handler().postDelayed({
                            callSocketMethods("updateVehicleLocation")
                        }, 2000)

                    }
                }
            } else {
                Toast.makeText(activity, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                        activity!!,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        activity!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
                activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        )

    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            currentLat = mLastLocation.latitude.toString()
            currentLong = mLastLocation.longitude.toString()
            Handler().postDelayed({
                callSocketMethods("updateVehicleLocation")
            }, 2000)

        }
    }

}