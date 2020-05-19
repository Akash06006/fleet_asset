package com.example.fleet.views.asset

import android.app.Dialog
import android.content.Intent
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.example.fleet.R
import androidx.lifecycle.Observer
import com.example.fleet.application.MyApplication
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.databinding.ActivityAssetJobDetailBinding
import com.example.fleet.model.LoginResponse
import com.example.fleet.model.asset.JobdetailResponse
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.utils.BaseActivity
import com.example.fleet.utils.DialogClass
import com.example.fleet.utils.Utils
import com.example.fleet.utils.ValidationsClass
import com.example.fleet.viewmodels.AssetJobDetailViewModel
import com.example.fleet.views.profile.ProfileActivity
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*

class AssetJobDetailActivity : BaseActivity() {
    private lateinit var activityAssetJobDetailBinding : ActivityAssetJobDetailBinding
    private lateinit var assetJobDetailViewModel : AssetJobDetailViewModel
    private var sharedPrefClass : SharedPrefClass? = null
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private val mJsonObject = JsonObject()
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private var profileImage = ""

    override fun getLayoutId() : Int {
        return R.layout.activity_asset_job_detail
    }

    override fun initViews() {
        activityAssetJobDetailBinding = viewDataBinding as ActivityAssetJobDetailBinding
        assetJobDetailViewModel = ViewModelProviders.of(this).get(AssetJobDetailViewModel::class.java)
        activityAssetJobDetailBinding.assetJobDetailViewModel = assetJobDetailViewModel
        activityAssetJobDetailBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.job_detail)
        //  makeEnableDisableViews(false)
        mJsonObject.addProperty(
            "id", "id"/* sharedPrefClass!!.getPrefValue(
                MyApplication.instance,
                GlobalConstants.USERID
            ).toString()*/
        )
        val id = intent.getStringExtra(GlobalConstants.job_id)

        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
            assetJobDetailViewModel.getjobDetail(id)
        }

        assetJobDetailViewModel.getDetail().observe(this,
            Observer<JobdetailResponse> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            activityAssetJobDetailBinding.assetJobDetailModel = response.body
                            activityAssetJobDetailBinding.tvDateTime.text = Utils(this).getDate(
                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                response.body!!.scheduleDatetime,
                                "dd-MMM,yyyy | hh:mm a"
                            )
                        }
                        else -> showToastError(message)
                    }

                }
            })


        assetJobDetailViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "tv_warehouse_pickup" -> {
                        val intent = Intent(this, TrackingMapsActivity::class.java)
                        startActivity(intent)
                    }
                    "tv_warehouse_pickup" -> {
                        val intent = Intent(this, TrackingMapsActivity::class.java)
                        startActivity(intent)
                    }
                    "rl_drop_warehouse" -> {
                        val intent = Intent(this, TrackingMapsActivity::class.java)
                        startActivity(intent)
                    }
                    "rl_pick_warehouse" -> {
                        val intent = Intent(this, TrackingMapsActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
        )

    }

    private fun showError(textView : TextView, error : String) {
        textView.requestFocus()
        textView.error = error
    }
}

