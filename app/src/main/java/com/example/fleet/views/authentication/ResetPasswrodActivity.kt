package com.example.fleet.views.authentication

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.fleet.R
import com.example.fleet.application.MyApplication
import com.example.fleet.databinding.ActivityResetPasswordBinding
import com.example.fleet.model.CommonModel
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.utils.BaseActivity
import com.example.fleet.utils.Utils
import com.example.fleet.viewmodels.ResetPasswordModel
import com.google.gson.JsonObject

class ResetPasswrodActivity : BaseActivity() {
    private lateinit var resetPasswordModel : ResetPasswordModel
    private lateinit var activityResetPasswordBinding : ActivityResetPasswordBinding
    private var sharedPrefClass = SharedPrefClass()
    override fun getLayoutId() : Int {
        return R.layout.activity_reset_password
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initViews() {
        activityResetPasswordBinding = viewDataBinding as ActivityResetPasswordBinding
        activityResetPasswordBinding.toolbarCommon.imgToolbarText.text = ""
        resetPasswordModel = ViewModelProviders.of(this).get(ResetPasswordModel::class.java)
        activityResetPasswordBinding.resetPasswordModel = resetPasswordModel


        resetPasswordModel.loading.observe(
                this,
                Observer<Boolean> { aBoolean->
                    if (aBoolean!!) {
                        startProgressDialog()
                    } else {
                        stopProgressDialog()
                    }
                }
        )

        resetPasswordModel.isClick().observe(
                this, Observer<String>(function =
        fun(it : String?) {
            val password = activityResetPasswordBinding.etPassword.text.toString()
            val confirm = activityResetPasswordBinding.etConfirm.text.toString()

            when (it) {
                "btn_continue" -> {
                    when {
                        password.isEmpty() -> showPassError(
                                getString(R.string.empty) + " " + getString(
                                        R.string.enter_new_password
                                )
                        )

                        confirm.isEmpty() -> showConfirmError(
                                getString(R.string.empty) + " " + getString(
                                        R.string.confirm_password
                                )
                        )

                        !Utils(this).isValidPassword(password) -> showPassError(
                                MyApplication.instance.getString(
                                        R.string.regex_message
                                )
                        )

                        password != confirm -> showConfirmError(MyApplication.instance.getString(R.string.mismatch_paaword))
                        else -> {
                            val mJsonObject = JsonObject()
                            mJsonObject.addProperty("password", password)
                            mJsonObject.addProperty(
                                    getString(R.string.key_phone),
                                    sharedPrefClass.getPrefValue(
                                            MyApplication.instance,
                                            getString(R.string.key_phone)
                                    ) as String
                            )

                            resetPasswordModel.callResetPassword(mJsonObject)
                        }
                    }

                }
            }
        })
        )





        resetPasswordModel.getResetPasswordResponse().observe(this,
                Observer<CommonModel> { commonResponse->
                    if (commonResponse != null) {
                        val message = commonResponse.message!!

                        when {
                            commonResponse.code == 200 -> this.eventCreatedDialog(this, "reset_password", message)
                            else -> showToastError(message)
                        }

                    }
                })

    }

    private fun showPassError(error : String) {
        activityResetPasswordBinding.etPassword.requestFocus()
        activityResetPasswordBinding.etPassword.error = error
    }

    private fun showConfirmError(passError : String) {
        activityResetPasswordBinding.etConfirm.requestFocus()
        activityResetPasswordBinding.etConfirm.error = passError
    }

}
