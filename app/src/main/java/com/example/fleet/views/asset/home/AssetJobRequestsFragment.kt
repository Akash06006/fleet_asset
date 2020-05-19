package com.example.fleet.views.asset.home

import android.app.Dialog
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleet.R
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.databinding.FragmentAssetHomeBinding
import com.example.fleet.databinding.FragmentHomeBinding
import com.example.fleet.model.CommonModel
import com.example.fleet.model.home.JobsResponse
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.utils.BaseFragment
import com.example.fleet.utils.DialogClass
import com.example.fleet.utils.DialogssInterface
import com.example.fleet.viewmodels.home.AssetHomeViewModel
import com.example.fleet.viewmodels.home.HomeViewModel
import com.example.fleet.views.asset.AssetJobDetailActivity
import com.google.gson.JsonObject
import com.uniongoods.adapters.AssetJobRequestsAdapter
import com.uniongoods.adapters.JobRequestsAdapter

class AssetJobRequestsFragment : BaseFragment(), DialogssInterface {
    private var pendingJobsList = ArrayList<JobsResponse.Data>()
    private var myAssetJobsRequestAdapter : AssetJobRequestsAdapter? = null
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private val mJobListObject = JsonObject()
    override fun getLayoutResId() : Int {
        return R.layout.fragment_asset_home
    }

    private lateinit var fragmentAssetHomeBinding : FragmentAssetHomeBinding
    private lateinit var assetHomeViewModel : AssetHomeViewModel
    private val mJsonObject = JsonObject()
    override fun initView() {
        fragmentAssetHomeBinding = viewDataBinding as FragmentAssetHomeBinding
        assetHomeViewModel = ViewModelProviders.of(this).get(AssetHomeViewModel::class.java)
        fragmentAssetHomeBinding.assetHomeViewModel = assetHomeViewModel
        baseActivity.startProgressDialog()
        //acceptStatus
        mJsonObject.addProperty(
            "acceptStatus", "0"
        )

        if (UtilsFunctions.isNetworkConnected()) {
            mJobListObject.addProperty(
                "employeeId", SharedPrefClass()!!.getPrefValue(activity!!, GlobalConstants.USERID) as String
            )
            mJobListObject.addProperty(
                "acceptStatus", "0"
            )
            assetHomeViewModel.getMyJobs(mJobListObject)
        } else {
            baseActivity.stopProgressDialog()
        }

        assetHomeViewModel.getJobs().observe(this,
            Observer<JobsResponse> { response->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            pendingJobsList.addAll(response.data!!)
                            fragmentAssetHomeBinding.rvJobs.visibility = View.VISIBLE
                            fragmentAssetHomeBinding.tvNoRecord.visibility = View.GONE
                            initRecyclerView()
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)

                            fragmentAssetHomeBinding.rvJobs.visibility = View.GONE
                            fragmentAssetHomeBinding.tvNoRecord.visibility = View.VISIBLE
                        }
                    }

                }
            })

        assetHomeViewModel.acceptReject().observe(this,
            Observer<CommonModel> { response->
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            pendingJobsList.clear()
                            pendingJobsList = ArrayList<JobsResponse.Data>()
                            assetHomeViewModel.getMyJobs(mJobListObject)

                            UtilsFunctions.showToastSuccess(message!!)
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            baseActivity.stopProgressDialog()
                        }
                    }

                }
            })

    }

    fun jobAccpetReject(jobId : String?, status : String) {
        mJsonObject.addProperty(
            "acceptStatus", status
        )
        mJsonObject.addProperty(
            "jobId", jobId
        )
        mJsonObject.addProperty(
            "employeeId", SharedPrefClass()!!.getPrefValue(activity!!, GlobalConstants.USERID) as String
        )
        if (status.equals("2")) {
            confirmationDialog = mDialogClass.setDefaultDialog(
                activity!!,
                this,
                "Reject Job",
                getString(R.string.warning_reject_job)
            )
            confirmationDialog?.show()
        } else {
            baseActivity.startProgressDialog()
            assetHomeViewModel.acceptRejectJob(mJsonObject)
        }

    }

    fun openJobDetail(jobId : String?) {
        val intent = Intent(baseActivity, AssetJobDetailActivity::class.java)
        intent.putExtra(GlobalConstants.job_id, jobId)
        startActivity(intent)
    }

    override fun onDialogConfirmAction(mView : View?, mKey : String) {
        when (mKey) {
            "Reject Job" -> {
                baseActivity.startProgressDialog()
                confirmationDialog?.dismiss()
                assetHomeViewModel.acceptRejectJob(mJsonObject)

            }
        }
    }

    override fun onDialogCancelAction(mView : View?, mKey : String) {
        when (mKey) {
            "Reject Job" -> confirmationDialog?.dismiss()
        }
    }

    private fun initRecyclerView() {
        myAssetJobsRequestAdapter =
            AssetJobRequestsAdapter(this@AssetJobRequestsFragment, pendingJobsList, activity!!)
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        fragmentAssetHomeBinding.rvJobs.layoutManager = linearLayoutManager
        fragmentAssetHomeBinding.rvJobs.adapter = myAssetJobsRequestAdapter
        fragmentAssetHomeBinding.rvJobs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })
    }
}