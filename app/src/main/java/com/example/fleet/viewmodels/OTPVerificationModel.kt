package com.example.fleet.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.text.TextUtils
import android.view.View
import com.example.fleet.R
import com.example.fleet.application.MyApplication
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.model.CommonModel
import com.example.fleet.model.LoginResponse
import com.example.fleet.repositories.LoginRepository
import com.example.fleet.sharedpreference.SharedPrefClass
import com.google.gson.JsonObject
import org.json.JSONObject

class OTPVerificationModel : BaseViewModel() {
    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating   }

    override fun isClick() : LiveData<String> {
        return btnClick     }

    override fun clickListener(v : View) {
        btnClick.value=v.resources.getResourceName(v.id).split("/")[1]
    }

    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()




    val loading : LiveData<Boolean>
        get() = mIsUpdating






}