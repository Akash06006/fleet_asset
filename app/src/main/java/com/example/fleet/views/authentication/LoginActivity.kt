package com.example.fleet.views.authentication

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.IntentFilter
import androidx.databinding.DataBindingUtil
import android.text.TextUtils
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.fleet.R
import com.example.fleet.application.MyApplication
import com.example.fleet.common.FirebaseFunctions
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.databinding.ActivityLoginBinding
import com.example.fleet.model.CommonModel
import com.example.fleet.model.LoginResponse
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.utils.BaseActivity
import com.example.fleet.viewmodels.LoginViewModel
import com.example.fleet.views.home.DashboardActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.gson.JsonObject

class LoginActivity : BaseActivity() {
    private lateinit var activityLoginbinding : ActivityLoginBinding
    private lateinit var loginViewModel : LoginViewModel
    override fun getLayoutId() : Int {
        return R.layout.activity_login
    }

    private val mJsonObject = JsonObject()

    override fun initViews() {
        activityLoginbinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        activityLoginbinding.loginViewModel = loginViewModel
        checkAndRequestPermissions()
        loginViewModel.checkEmailExistence().observe(this,
            Observer<LoginResponse> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            FirebaseFunctions.sendOTP("login", mJsonObject, this)
                            mJsonObject.addProperty(
                                "phoneNumber",
                                activityLoginbinding.etPhoneNo.text.toString()
                            )
                            mJsonObject.addProperty(
                                "countryCode",
                                "+" + activityLoginbinding.btnCcp.selectedCountryCode
                            )


                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.USERID,
                                response.data!!.id
                            )
                            if (!TextUtils.isEmpty(response.data!!.profile_image)) {
                                SharedPrefClass().putObject(
                                    this,
                                    GlobalConstants.USER_IAMGE,
                                    response.data!!.profileImageUrl + response.data!!.profile_image
                                )
                            } //profileImageUrl +profileImage


                            SharedPrefClass().putObject(
                                this, GlobalConstants.USERDATA,
                                response.data!!
                            )

                            SharedPrefClass().putObject(
                                applicationContext,
                                getString(R.string.first_name),
                                response.data!!.firstName + " " + response.data!!.lastName
                            )
                            SharedPrefClass().putObject(
                                this,
                                getString(R.string.key_phone),
                                activityLoginbinding.etPhoneNo.text.toString()
                            )
                            SharedPrefClass().putObject(
                                this,
                                getString(R.string.key_country_code),
                                "+" + activityLoginbinding.btnCcp.selectedCountryCode
                            )
                            SharedPrefClass().putObject(
                                this,
                                GlobalConstants.ACCESS_TOKEN,
                                response.data!!.authToken
                            )
                            /*SharedPrefClass().putObject(
                                MyApplication.instance.applicationContext,
                                "isLogin",
                                true
                            )
                            val intent = Intent(this, DashboardActivity::class.java)
                            intent.putExtra("data", mJsonObject.toString())
                            startActivity(intent)
                            finish()*/
                            /*  val intent = Intent(this, OTPVerificationActivity::class.java)
                              intent.putExtra("data", mJsonObject.toString())
                              startActivity(intent)*/

                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> showToastError(message)
                    }

                }
            })


        loginViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                val phone = activityLoginbinding.etPhoneNo.text.toString()
                when (it) {
                    "btn_login" -> {
                        when {
                            TextUtils.isEmpty(phone) -> run {
                                activityLoginbinding.etPhoneNo.requestFocus()
                                activityLoginbinding.etPhoneNo.error =
                                    getString(R.string.empty) + " " + getString(
                                        R.string.phone_number
                                    )
                            }
                            else -> {
                                mJsonObject.addProperty("phoneNumber", phone)
                                mJsonObject.addProperty(
                                    "deviceToken",
                                    SharedPrefClass().getPrefValue(
                                        MyApplication.instance,
                                        GlobalConstants.NOTIFICATION_TOKEN
                                    ) as String
                                )
                                val versionName = MyApplication.instance.packageManager
                                    .getPackageInfo(MyApplication.instance.packageName, 0)
                                    .versionName
                                val androidId = UtilsFunctions.getAndroidID()
//deviceToken
                                mJsonObject.addProperty("email", "")
                                mJsonObject.addProperty("password", "")
                                mJsonObject.addProperty("deviceType", GlobalConstants.PLATFORM)
                                mJsonObject.addProperty(
                                    "countryCode",
                                    "+" + activityLoginbinding.btnCcp.selectedCountryCode
                                )
                                mJsonObject.addProperty("deviceId", androidId)
                                // mJsonObject.addProperty("app-version", versionName)
                                loginViewModel.checkPhoneExistence(mJsonObject)

                                SharedPrefClass().putObject(
                                    applicationContext,
                                    getString(R.string.key_phone),
                                    phone
                                )

                                SharedPrefClass().putObject(
                                    applicationContext,
                                    getString(R.string.key_country_code),
                                    "+" + activityLoginbinding.btnCcp.selectedCountryCode
                                )
                                //  FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                                val mSmsBroadcastReceiver = SMSBroadcastReciever()
                                //set google api client for hint request
                                //  mSmsBroadcastReceiver.setOnOtpListeners(baseContext)
                                val intentFilter = IntentFilter()
                                intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
                                LocalBroadcastManager.getInstance(this)
                                    .registerReceiver(mSmsBroadcastReceiver, intentFilter)

                            }
                        }
                    }
                }

            })
        )


        loginViewModel.isLoading().observe(this, Observer<Boolean> { aBoolean->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })

    }

}