package com.example.fleet.views.authentication

import android.annotation.TargetApi
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.os.CountDownTimer
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.fleet.R
import com.example.fleet.application.MyApplication
import com.example.fleet.callbacks.OtpReceivedInterface
import com.example.fleet.common.FirebaseFunctions
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.databinding.ActivityOtpVerificationBinding
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.utils.BaseActivity
import com.example.fleet.viewmodels.OTPVerificationModel
import com.example.fleet.views.home.DashboardActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject

class OTPVerificationActivity : BaseActivity(),
    OtpReceivedInterface, GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0 : ConnectionResult) {
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun onOtpReceived(otp : String?) {
        activityOtpVerificationBinding.pinview.value = otp

    }

    override fun onOtpTimeout() {
        //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var otpVerificationModel : OTPVerificationModel
    private lateinit var activityOtpVerificationBinding : ActivityOtpVerificationBinding
    private var mAuth = FirebaseAuth.getInstance()
    private var mJsonObject = JSONObject()
    private var action = "login"
    private var mVerificationId : String? = null
    private var mGoogleApiClient : GoogleApiClient? = null
    private var RESOLVE_HINT = 2;

    override fun getLayoutId() : Int {
        return R.layout.activity_otp_verification
    }

    public override fun initViews() {
        activityOtpVerificationBinding = viewDataBinding as ActivityOtpVerificationBinding
        activityOtpVerificationBinding.toolbarCommon.imgToolbarText.text =
            resources.getString(R.string.otp_verify)
        otpVerificationModel = ViewModelProviders.of(this).get(OTPVerificationModel::class.java)
        activityOtpVerificationBinding.verifViewModel = otpVerificationModel


        try {
            mJsonObject = JSONObject(intent.extras.get("data").toString())
            //  action = intent.extras.get("action").toString()
            var mSmsBroadcastReceiver = SMSBroadcastReciever()
            //set google api client for hint request
//            mSmsBroadcastReceiver.setOnOtpListeners(this)
//
//            val intentFilter = IntentFilter()
//            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
//            LocalBroadcastManager.getInstance(this).registerReceiver(mSmsBroadcastReceiver, intentFilter)

        } catch (e : JSONException) {
            e.printStackTrace()
        }

        otpVerificationModel.loading.observe(this, Observer<Boolean> { aBoolean->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })






        otpVerificationModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                val otp = activityOtpVerificationBinding.pinview.value.toString()
                when (it) {
                    "btn_send" -> {
                        if (TextUtils.isEmpty(otp)) {
                            showToastError(getString(R.string.empty) + " " + getString(R.string.OTP))
                        } else if (otp.length < 6) {
                            showToastError(
                                getString(R.string.invalid) + " " + getString(
                                    R.string.OTP
                                )
                            )
                        } else {
                            try {
                                mVerificationId = SharedPrefClass().getPrefValue(
                                    MyApplication.instance.applicationContext,
                                    GlobalConstants.OTP_VERIFICATION_ID
                                ).toString()

                            } catch (e : JSONException) {
                                e.printStackTrace()
                            }
                            startProgressDialog()
                            verifyVerificationCode(otp)

                        }

                    }
                    "tv_resend" -> {
                        val mJsonObject1 = JsonObject()
                        mJsonObject1.addProperty(
                            "countryCode",
                            mJsonObject.get("countryCode").toString()
                        )
                        mJsonObject1.addProperty(
                            "phoneNumber",
                            mJsonObject.get("phoneNumber").toString()
                        )
                        FirebaseFunctions.sendOTP("resend", mJsonObject1, this)
                        startProgressDialog()

                        object : CountDownTimer(60000, 1000) {
                            override fun onTick(millisUntilFinished : Long) {
                                activityOtpVerificationBinding.resendOTP = 1
                                activityOtpVerificationBinding.tvResendTime.text =
                                    "00:" + millisUntilFinished / 1000
                                //here you can have your logic to set text to edittext
                            }

                            override fun onFinish() {
                                activityOtpVerificationBinding.resendOTP = 0
                                // mTextField.setText("done!")
                            }

                        }.start()

                    }
                }

            })
        )

    }

    public fun getHintPhoneNumber() {
        var hintRequest =
            HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        var mIntent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
        try {
            startIntentSenderForResult(mIntent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (e : IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }

    private fun startSMSListener() {
        val mClient = SmsRetriever.getClient(this)
        val mTask = mClient.startSmsRetriever()
//        mTask.addOnSuccessListener(object : OnSuccessListener<Void> {
//            override fun onSuccess(aVoid : Void) {
//                //layoutInput.setVisibility(View.GONE)
//                //  layoutVerify.setVisibility(View.VISIBLE)
//                // Toast.makeText(this@MainActivity, "SMS Retriever starts", Toast.LENGTH_LONG).show()
//            }
//        })
//        mTask.addOnFailureListener(object : OnFailureListener {
//            override fun onFailure(@NonNull e : Exception) {
//                // Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
//            }
//        })

    }

    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential : PhoneAuthCredential) {
            //Getting the code sent by SMS
            stopProgressDialog()
            val code = phoneAuthCredential.smsCode
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                // verifying the code

            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        override fun onVerificationFailed(e : FirebaseException) {
            if ((e as FirebaseAuthException).errorCode == "ERROR_INVALID_PHONE_NUMBER")
                showToastError(e.message.toString().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
            else
                showToastError(getString(R.string.server_not_reached))
        }

    }

    private fun verifyVerificationCode(code : String) {
        //creating the credential
        val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)
        //signing the user
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential : PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this,
                { task->
                    stopProgressDialog()
                    if (task.isSuccessful) {
                        SharedPrefClass().putObject(
                            MyApplication.instance.applicationContext,
                            "isLogin",
                            true
                        )

                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("data", mJsonObject.toString())
                        startActivity(intent)
                        finish()
                        // otpVerificationModel.login(mJsonObject)
                        /* if (action.equals("signup"))
                         {
                             val intent = Intent(this, RegisterActivity::class.java)
                             startActivity(intent)
                         }

                         if (action.equals("forgot")) {
                             val intent = Intent(this, ResetPasswrodActivity::class.java)
                             startActivity(intent)
                         }*/

                    } else {
                        //verification unsuccessful.. display an error message
                        var message = getString(R.string.something_error)

                        if (task.exception is FirebaseAuthException) {
                            message = getString(R.string.invalid_otp)
                        }

                        showToastError(message)
                        //Toast.makeText(OtpVerificationFirebase.this, message, Toast.LENGTH_SHORT).show();

                    }
                })
    }

}
