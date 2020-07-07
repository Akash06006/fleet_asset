package com.example.fleet.socket

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.bluetooth.BluetoothClass
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.fleet.R
import com.example.fleet.application.MyApplication
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.database.*
import com.example.fleet.databinding.FragmentTrackingBinding
import com.example.fleet.maps.FusedLocationClass
import com.example.fleet.maps.MapClass
import com.example.fleet.maps.MapInterface
import com.example.fleet.model.CommonModel
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.utils.*
import com.example.fleet.viewmodels.tracking.TrackingViewModel
import com.example.fleet.views.home.DashboardActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.JsonObject
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.Provider
import kotlin.collections.ArrayList

open class TrackingActivity : BaseActivity(), OnMapReadyCallback, LocationListener, SocketInterface,
    GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, MapInterface,
    DialogssInterface,
    GoogleMap.OnInfoWindowClickListener, Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity : Activity?) {
        Log.e("LifeCycle: ", "Pause")
        /* var service = Intent(this, LocationTrackingService::class.java)
         startService(service)*/

        gpsService?.startTracking()

    }

    var gpsService : BackgroundLocationService? = null
    override fun onActivityResumed(activity : Activity?) {
        Log.e("LifeCycle: ", "Resumed")
        /*  var service = Intent(this, LocationTrackingService::class.java)
          stopService(service)*/
        gpsService?.stopTracking()

    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className : ComponentName,
            service : IBinder
        ) {
            // gpsService = ((BackgroundLocationService.LocationServiceBinder) service).getService()
            val binder = service as BackgroundLocationService.LocationServiceBinder
            gpsService = binder.service
        }

        override fun onServiceDisconnected(name : ComponentName) {
            gpsService = null
        }
    }

    override fun onActivityStarted(activity : Activity?) {
        Log.e("LifeCycle: ", "Started")
    }

    override fun onActivityDestroyed(activity : Activity?) {
        Log.e("LifeCycle: ", "Destroyed")
    }

    override fun onActivitySaveInstanceState(activity : Activity?, outState : Bundle?) {
        Log.e("LifeCycle: ", "SavedInstance")
    }

    override fun onActivityStopped(activity : Activity?) {
        Log.e("LifeCycle: ", "Stopped")

    }

    override fun onActivityCreated(activity : Activity?, savedInstanceState : Bundle?) {
        Log.e("LifeCycle: ", "Created")
    }

    internal var cameraZoom = 16.0f
    private var mAddress = ""
    private var mInterface : DialogssInterface? = null
    private var mGoogleMap : GoogleMap? = null
    private var mFusedLocationClass : FusedLocationClass? = null
    private var mLocation : Location? = null
    private var mLatitude : String? = null
    private var mLongitude : String? = null
    private var utils : Utils? = null
    private var mHandler = Handler()
    private var mMapClass = MapClass()
    private var mContext : Context? = null
    private var sharedPrefClass = SharedPrefClass()
    private var check : Int = 0
    private val points = ArrayList<LatLng>()
    private var mPermissionCheck = false
    private var mGoogleApiClient : GoogleApiClient? = null
    private var mLine : Polyline? = null
    private var socket = SocketClass.socket
    private var dialog : Dialog? = null
    private var locationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private var click_settings = 1
    private var click_gps = 1
    private var scan = 0
    private var start = 0
    private var permanent_deny = 0
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private var mJsonObject = JSONObject()
    private val mMarkers = java.util.ArrayList<Marker>()
    var jobId = ""
    var destlat = ""
    var destLong = ""
    var istrack = ""
    private lateinit var fkip : LatLng
    private lateinit var destLocation : LatLng
    lateinit var trackingViewModel : TrackingViewModel
    lateinit var trackingActivityBinding : FragmentTrackingBinding
    private lateinit var locationsViewModel : LocationsViewModel
    var runCall = 0
    var serviceIntent : Intent? = null
    var polyPath : MutableList<LatLng>? = null
    private var confirmationDialog : Dialog? = null

    companion object {
        var categoryListids : ArrayList<String>? = null

    }

    private val mRunnable = object : Runnable {
        override fun run() {
            if (mFusedLocationClass != null) {
                mLocation = mFusedLocationClass!!.getLastLocation(mContext!!)

                if (mLocation != null) {
                    Log.e("OnLocationChange1: ", "Called")
                    val mAddress = utils!!.getAddressFromLocation(
                        mContext!!,
                        mLocation!!.latitude,
                        mLocation!!.longitude,
                        "Address"
                    )
                    mLatitude = (mLocation!!.latitude).toString() + ""
                    mLongitude = (mLocation!!.longitude).toString() + ""
                    // Log.d(TAG, "get_current_address: $mAddress")
                    runCall++
                    // showToastSuccess(runCall.toString() + " " + mLocation!!.latitude.toString() + " " + mLocation!!.longitude.toString())
                    val mCameraPosition = CameraPosition.Builder()
                        .target(
                            LatLng(
                                java.lang.Double.parseDouble(mLatitude!!),
                                java.lang.Double.parseDouble(mLongitude!!)
                            )
                        )
                        .zoom(15.5f)
                        .tilt(30f)
                        .build()
                    mGoogleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition))
                    // mHandler.postDelayed(this, 1000)
                    mHandler.removeCallbacks(this)

                } else
                    mHandler.postDelayed(this, 1000)
            } else
                mHandler.postDelayed(this, 1000)
        }
    }

    override fun getLayoutId() : Int {
        return R.layout.fragment_tracking
    }

    override fun onBackPressed() {
        //  super.onBackPressed()
        var intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)

    }

    override fun initViews() {
        trackingActivityBinding = viewDataBinding as FragmentTrackingBinding
        trackingViewModel = ViewModelProviders.of(this).get(TrackingViewModel::class.java)
        mFusedLocationClass = FusedLocationClass(this)

        locationsViewModel = ViewModelProvider(this).get(LocationsViewModel::class.java)
        trackingActivityBinding.trackingViewModel = trackingViewModel
//        var records = locationsViewModel.allLocations
        application.registerActivityLifecycleCallbacks(this);
        sharedPrefClass = SharedPrefClass()
        GlobalConstants.JOB_STARTED = "true"
        categoryListids = ArrayList()
        try {
            mJsonObject = JSONObject(intent.extras?.get("data")?.toString())
            jobId = mJsonObject.get("jobId").toString()
            destlat = mJsonObject.get("dest_lat").toString()
            destLong = mJsonObject.get("dest_long").toString()
            istrack = mJsonObject.get("trackOrStart").toString()

            if (!TextUtils.isEmpty(destLong)) {
                val latLng = LatLng(destlat.toDouble(), destLong.toDouble())
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title("Destination Position")
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            }
        } catch (e : JSONException) {
            e.printStackTrace()
        }
        //service
        serviceIntent = Intent(this.getApplication(), BackgroundLocationService::class.java)
        this.getApplication().startService(serviceIntent)
        this.getApplication()
            .bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        var jobsDetail = locationsViewModel.getJob(jobId)
        if (istrack != resources?.getString(R.string.track)) {
            var jobDetails = JobDetails()
            jobDetails.complete_status = "0"
            jobDetails.job_id = jobId
            jobDetails.status = "0"
            jobDetails.start_date = ""
            jobDetails.end_date = ""
            jobDetails.user_id = sharedPrefClass!!.getPrefValue(
                MyApplication.instance,
                GlobalConstants.USERID
            ).toString()
            locationsViewModel.insertJob(jobDetails)
        }
        mContext = this
        val supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mMapClass.setSupportMapFragmentAsync(supportMapFragment!!, this)
        mMapClass.getGeoDataClient(mContext!!)
        mInterface = this
        utils = Utils(this)

        dialog = Dialog(this)
        dialog = mDialogClass.setPermissionDialog(this, this)
        locationDialog = mDialogClass.setDefaultDialog(
            this,
            mInterface!!,
            "GPSCheck",
            getString(R.string.GPS_enabled)
        )
        //Socket Initialization
        Log.e("Connect Socket", "Track activity")
        socket.updateSocketInterface(this)
        socket.onConnect()

        destLocation = LatLng(destlat.toDouble(), destLong.toDouble())

        trackingViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "btn_clear" -> {
                        confirmationDialog = mDialogClass.setDefaultDialog(
                            this,
                            this,
                            "Finish Job",
                            getString(R.string.warning_finish_job)
                        )
                        confirmationDialog?.show()

                    }
                }
            })
        )

        trackingViewModel.startCompleteJob().observe(this,
            Observer<CommonModel> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            UtilsFunctions.showToastSuccess(message!!)
                            GlobalConstants.JOB_STARTED = "false"
                           val intent = Intent(this, DashboardActivity::class.java)
                            //startActivity(Intent(this, DashboardActivity::class.java))
                            intent.putExtra("from", "home")
                            startActivity(intent)
                            finish()
                        }
                        else -> message?.let {
                            if(message.equals("Job does not exist.")){
                                GlobalConstants.JOB_STARTED = "false"
                               /* startActivity(Intent(this, DashboardActivity::class.java))*/
                                val intent = Intent(this, DashboardActivity::class.java)
                                intent.putExtra("from", "home")
                                startActivity(intent)
                                finish()
                            }
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })

    }

    @Synchronized
    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()

    }

    override fun onDestroy() {
        super.onDestroy()
        // mSocket.disconnect();
        if (mGoogleApiClient != null) {
            // mGoogleApiClient!!.disconnect()

        }

    }

    override fun onCameraIdle(cameraPosition : CameraPosition) {
        cameraZoom = cameraPosition.zoom
    }

    override fun onCameraMoveStarted() {
//Empty
    }

    override fun onInfoWindowClick(marker : Marker) {
//Not In Use
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onResume() {
        if (click_settings > 0) {
            checkPermission()
        }

        if (click_gps > 0) {
            checkGPS()
            click_gps = 0
        }

        super.onResume()
    }

    override fun onMapReady(googleMap : GoogleMap) {
        this.mGoogleMap = googleMap
        mGoogleMap!!.setMinZoomPreference(5f)
        buildGoogleApiClient()
        googleMap.uiSettings.isCompassEnabled = false
        googleMap.isTrafficEnabled = false
        googleMap.isMyLocationEnabled = true

        mHandler.postDelayed(mRunnable, 500)
        mPermissionCheck = false
        check = 0

    }

    override fun onAutoCompleteListener(place : Place) {
//Not In Use
    }

    override fun onConnected(bundle : Bundle?) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
            mGoogleApiClient
        )
        if (mLastLocation != null) {
            val latLng = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title("Current Position")
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        }
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 3000 //5 seconds
        mLocationRequest.fastestInterval = 3000 //3 seconds
        mLocationRequest.smallestDisplacement = 0.1f //added
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleApiClient,
            mLocationRequest,
            this
        )
    }

    override fun onConnectionSuspended(i : Int) {
//Not In Use
    }

    override fun onConnectionFailed(connectionResult : ConnectionResult) {
//Not In Use
    }

    override fun onPause() {
        super.onPause()

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onLocationChanged(location : Location) {
        getCurrentLocation(location)
        val mCameraPosition = CameraPosition.Builder()
            .target(
                LatLng(
                    location.latitude,
                    location.longitude
                )
            )         // Sets the center of the map to Mountain View
            .zoom(17.0f)
            .tilt(30f)
            .build()
        if (!SharedPrefClass().getPrefValue(
                MyApplication.instance.applicationContext,
                GlobalConstants.JOBID
            ).toString().equals("null") && !SharedPrefClass().getPrefValue(
                MyApplication.instance.applicationContext,
                GlobalConstants.JOBID
            ).toString().equals("0")
        ) {
            // var icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_car)
            var icon = bitmapDescriptorFromVector(this, R.drawable.ic_car)
            mGoogleMap!!.addMarker(
                MarkerOptions()
                    .position(LatLng(location.latitude, location.longitude))
                    //.snippet(points[0].longitude.toString() + "")
                    .icon(icon)
            )

            fkip = LatLng(location.latitude, location.longitude)
            var jobLocationsDetails = JobLocationsDetails()
            jobLocationsDetails.job_id = jobId
            jobLocationsDetails.job_lat = location.latitude.toString()
            jobLocationsDetails.job_long = location.longitude.toString()
            jobLocationsDetails.status = "0"
            locationsViewModel.insert(jobLocationsDetails)
            var upload = UploadDataToServer()
            //showToastSuccess("OnLocationCalled")
            Log.e("OnLocationChange: ", "Called")
            if (UtilsFunctions.isNetworkConnectedWithoutToast()) {
                drawPolyline()
                upload.synchData(jobId, "track")
            } else {
                drawLine(polyPath!!)
                val jobScheduler =
                    getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                val jobInfo = JobInfo.Builder(
                    11,
                    ComponentName(this@TrackingActivity, NetworkJobService::class.java)
                )
                    // only add if network access is required
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build()
                jobScheduler.schedule(jobInfo)
            }
        }

    }

    fun bitmapDescriptorFromVector(context : Context, @DrawableRes vectorDrawableResourceId : Int) : BitmapDescriptor {
        var background = ContextCompat.getDrawable(this, R.drawable.ic_car);
        background?.setBounds(
            0,
            0,
            background.getIntrinsicWidth(),
            background.getIntrinsicHeight()
        )
        var vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable?.setBounds(
            40,
            20,
            vectorDrawable.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight()
        )
        var bitmap = Bitmap.createBitmap(
            background!!.getIntrinsicWidth(),
            background.getIntrinsicHeight(),
            Bitmap.Config.ARGB_8888
        )
        var canvas = Canvas(bitmap)
        background?.draw(canvas)
        vectorDrawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private fun getCurrentLocation(location : Location) {
        mAddress =
            utils!!.getAddressFromLocation(this, location.latitude, location.longitude, "Address")
        mLongitude = location.longitude.toString() + ""
        mLatitude = location.latitude.toString() + ""


        if (start == 0) {
            mGoogleMap!!.addMarker(
                MarkerOptions()
                    .position(LatLng(location.latitude, location.longitude))
                    //.snippet(points[0].longitude.toString() + "")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
            start = 1
        }
    }

    private fun removeMarkers(mMarkers1 : java.util.ArrayList<Marker>) {
        for (marker in mMarkers1) {
            marker.remove()
        }

        mMarkers.clear()

    }

    //SOCKET FUNCTIONALITY
    override fun onSocketCall(onMethadCall : String, vararg jsonObject : Any) {
        val serverResponse = jsonObject[0] as JSONObject
        var methodName = serverResponse.get("method")
        Log.e("", "serverResponse: " + serverResponse)
        try {
            this.runOnUiThread {
                when (methodName) {
                    "updateLocation" -> try {
                        //callSocketMethods("getLocation")
                    } catch (e1 : Exception) {
                        e1.printStackTrace()
                    }
                    "getLocation" -> try {
                        val innerResponse = serverResponse.get("data") as JSONArray
                        val obj = innerResponse[0] as JSONObject
                        obj.optString("to_lat")
                        obj.getString("to_lat")
                        obj.get("to_lat").toString()
                    } catch (e1 : Exception) {
                        e1.printStackTrace()
                    }
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
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

    fun checkPermission() {
        if (!mPermissionCheck) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mContext != null)
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (dialog != null) {
                            dialog!!.dismiss()
                        }
                        mPermissionCheck = true
                    } else {
                        if (dialog != null) {
                            dialog!!.dismiss()
                        }
                        mPermissionCheck = false

                        requestPermissions(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ), MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
            } else {
                if (dialog != null) {
                    dialog!!.dismiss()
                }
                mPermissionCheck = true
            }
        }
    }

    override fun onDialogConfirmAction(mView : View?, mKey : String) {
        when (mKey) {
            "GPSCheck" -> {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                click_gps++
                // locationDialog.dismiss();
                startActivity(intent)
            }
            "Finish Job" -> {
                confirmationDialog?.dismiss()
                GlobalConstants.JOB_STARTED = "false"
                locationsViewModel.updateJobStatus("1", jobId)
                var upload = UploadDataToServer()
                upload.synchData(jobId, "track")

                SharedPrefClass().putObject(
                    MyApplication.instance.applicationContext,
                    GlobalConstants.JOB_STARTED,
                    "false"
                )

                SharedPrefClass().putObject(
                    MyApplication.instance.applicationContext,
                    GlobalConstants.JOBID,
                    0
                )

                SharedPrefClass().putObject(
                    MyApplication.instance.applicationContext,
                    GlobalConstants.DEST_LAT,
                    ""
                )

                SharedPrefClass().putObject(
                    MyApplication.instance.applicationContext,
                    GlobalConstants.DEST_LONG,
                    ""
                )

                gpsService?.stopTracking()
                gpsService?.stopService(
                    Intent(
                        this,
                        BackgroundLocationService::class.java
                    )
                )

                try {
                    this.getApplication()
                        .unbindService(serviceConnection)
                } catch (e : Exception) {
                    e.printStackTrace()
                }

                mFusedLocationClass?.stopLocationUpdates()
                if (UtilsFunctions.isNetworkConnected()) {
                    trackingViewModel.startJob("1", jobId.toString())
                } else {
                    showToastSuccess(getString(R.string.job_finished_msg))
                    /*startActivity(Intent(this, DashboardActivity::class.java))*/
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("from", "home")
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onDialogCancelAction(mView : View?, mKey : String) {
        when (mKey) {
            "GPSCheck" -> {
                dialog!!.dismiss()
                locationDialog!!.dismiss()
            }
            "Finish Job" -> {
                confirmationDialog?.dismiss()
            }
        }

    }

    fun checkGPS() {
        val mLocationManager =
            mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        if (mLocationManager != null) {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
        if (!gps_enabled || !network_enabled) {
            checkPermission()
            locationDialog!!.show()
        } else {
            if (locationDialog!!.isShowing()) {
                locationDialog!!.dismiss()
            }
            // locationDialog.dismiss();
            checkPermission()
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode : Int, permissions : Array<String>,
        grantResults : IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION && permissions.size > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dialog!!.dismiss()
                    mPermissionCheck = true
                } else {
                    if (scan > 0) {
                        scan = 0
                    } else {
                        val permission1 = permissions[0]
                        val showRationale = shouldShowRequestPermissionRationale(permission1)
                        if (click_settings > 0) {
                            click_settings = 0
                            dialog!!.show()
                        } else {
                            if (!showRationale && permanent_deny > 0) {
                                click_settings++
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts("package", this.packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            } else if (permanent_deny == 0 && !showRationale) {
                                dialog!!.show()
                                permanent_deny++
                            } else {
                                permanent_deny++
                                dialog!!.show()
                            }
                        }

                    }
                }
            }
        }
    }

    private fun drawPolyline() {
        var path : MutableList<LatLng> = ArrayList()
        val context = GeoApiContext.Builder()
            .apiKey(getString(R.string.api_key))
            .build()
        val req = DirectionsApi.getDirections(
            context,
            fkip.latitude.toString().plus(",").plus(fkip.longitude.toString()),
            destLocation.latitude.toString().plus(",").plus(destLocation.longitude.toString())
        )
        try {
            val res = req.await()
            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.isNotEmpty()) {
                val route = res.routes[0]
                if (route.legs != null) {
                    for (i in 0 until route.legs.size) {
                        val leg = route.legs[i]
                        if (leg.steps != null) {
                            for (j in 0 until leg.steps.size) {
                                val step = leg.steps[j]
                                if (step.steps != null && step.steps.isNotEmpty()) {
                                    for (k in 0 until step.steps.size) {
                                        val step1 = step.steps[k]
                                        val points1 = step1.polyline
                                        if (points1 != null) {
                                            //Decode polyline and add points to latLongList of route coordinates
                                            val coords1 = points1.decodePath()
                                            for (coord1 in coords1) {
                                                // path.add(LatLng(coord1.lat, coord1.lng))
                                                path.add(LatLng(coord1.lat, coord1.lng))
                                            }
                                        }
                                    }
                                } else {
                                    val points = step.polyline
                                    if (points != null) {
                                        //Decode polyline and add points to latLongList of route coordinates
                                        val coords = points.decodePath()
                                        for (coord in coords) {
                                            path.add(LatLng(coord.lat, coord.lng))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                //  mGoogleMap.drawPolyline("Destination is not detected,unable to draw path")
                Log.d("MapPath", "Unable to draw path")
            }
        } catch (ex : Exception) {
            ex.printStackTrace()
        }

        drawLine(path)

    }

    private fun drawLine(path : MutableList<LatLng>) {
        polyPath = path
        mGoogleMap?.clear()
        //  var icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_car)
        var icon = bitmapDescriptorFromVector(this, R.drawable.ic_car)
        mGoogleMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(fkip.latitude, fkip.longitude))
                //.snippet(points[0].longitude.toString() + "")
                .icon(icon)
        )
        mGoogleMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(destLocation.latitude, destLocation.longitude))
                //.snippet(points[0].longitude.toString() + "")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        )
        if (polyPath!!.size > 0) {
            val opts = PolylineOptions().addAll(path).color(R.color.colorRed).width(20f)
            /*polylineFinal = */mGoogleMap?.addPolyline(opts)
        }

    }

}









