package com.example.fleet.views

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.fleet.R
import com.example.fleet.application.MyApplication
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.databinding.ActivitySplashBinding
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.socket.TrackingActivity
import com.example.fleet.utils.BaseActivity
import com.example.fleet.views.authentication.LoginActivity
import com.example.fleet.views.home.DashboardActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import java.util.*

class SplashActivity : BaseActivity() {
    private var mActivitySplashBinding: ActivitySplashBinding? = null
    private var sharedPrefClass: SharedPrefClass? = null
    private var mContext: Context? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun initViews() {
        mContext = this
        mActivitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }
                    // Get new Instance ID token
                    val token = task.result?.token
                    SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.NOTIFICATION_TOKEN,
                            token
                    )

                })

        sharedPrefClass = SharedPrefClass()
        /*var token = sharedPrefClass!!.getPrefValue(
                applicationContext,
                GlobalConstants.NOTIFICATION_TOKEN
        ) as String

        if (token != null) {
            sharedPrefClass!!.putObject(
                    applicationContext,
                    GlobalConstants.NOTIFICATION_TOKEN,
                    token
            )
        }*/

        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    checkScreenType()
                }
            }
        }, 3000)
    }

    private fun checkScreenType() {
        var login = ""
        if (checkObjectNull(
                        SharedPrefClass().getPrefValue(
                                MyApplication.instance,
                                "isLogin"
                        )
                )
        )
            login = sharedPrefClass!!.getPrefValue(this, "isLogin").toString()
        var jobId = sharedPrefClass!!.getPrefValue(this, GlobalConstants.JOBID).toString()
        val intent = if (login == "true") {
            if (!jobId.equals("null") && !jobId.equals("0")) {
                var mJsonObjectStartJob = JsonObject()
                var destLat =
                        sharedPrefClass!!.getPrefValue(this, GlobalConstants.DEST_LAT).toString()
                var destLong =
                        sharedPrefClass!!.getPrefValue(this, GlobalConstants.DEST_LONG).toString()
                mJsonObjectStartJob.addProperty(
                        "jobId", jobId
                )
                mJsonObjectStartJob.addProperty(
                        "dest_lat", destLat
                )
                mJsonObjectStartJob.addProperty(
                        "dest_long", destLong
                )
                mJsonObjectStartJob.addProperty(
                        "trackOrStart", "Track"
                )
                val intent = Intent(this, TrackingActivity::class.java)
                intent.putExtra("data", mJsonObjectStartJob.toString())
                //startActivity(intent)
            } else {
                val intent = Intent(this, DashboardActivity::class.java)
                //  val intent = Intent(this, DashboardActivity::class.java)
                //startActivity(Intent(this, DashboardActivity::class.java))
                intent.putExtra("from", "home")
                // startActivity(intent)
            }

        } else {
            Intent(this, LoginActivity::class.java)
            //Intent(this, DashboardActivity::class.java)

        }

        startActivity(intent)
        finish()
    }

}
