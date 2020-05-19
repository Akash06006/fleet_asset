package com.example.fleet.views.asset

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleet.R
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.databinding.ActivityAssetJobsHistoryBinding
import com.example.fleet.databinding.ActivityJobsHistoryBinding
import com.example.fleet.model.home.JobsResponse
import com.example.fleet.utils.BaseActivity
import com.example.fleet.viewmodels.home.AssetHomeViewModel
import com.example.fleet.viewmodels.home.HomeViewModel
import com.uniongoods.adapters.AssetJobsHistoryListAdapter
import com.uniongoods.adapters.JobsHistoryListAdapter
import com.uniongoods.adapters.MyJobsListAdapter

class AssetJobsHistoryActivity : BaseActivity() {
    private var jobsList = ArrayList<JobsResponse.Data>()
    lateinit var assetJobsHistoryBinding : ActivityAssetJobsHistoryBinding
    lateinit var assetHomeViewModel : AssetHomeViewModel
    override fun getLayoutId() : Int {
        return R.layout.activity_asset_jobs_history
    }

    override fun initViews() {
        assetJobsHistoryBinding = viewDataBinding as ActivityAssetJobsHistoryBinding
        assetHomeViewModel = ViewModelProviders.of(this).get(AssetHomeViewModel::class.java)
        assetJobsHistoryBinding.assetHomeViewModel = assetHomeViewModel
        assetJobsHistoryBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.asset_job_history)

        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
            //acceptStatus
            assetHomeViewModel.getMyJobsHistory("1")
        }

        assetHomeViewModel.getJobsHistory().observe(this,
            Observer<JobsResponse> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            jobsList.addAll(response.data!!)
                            assetJobsHistoryBinding.rvJobHistory.visibility = View.VISIBLE
                            assetJobsHistoryBinding.tvNoRecord.visibility = View.GONE
                            initRecyclerView()
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }

                }
            })
        // initRecyclerView()

    }

    private fun initRecyclerView() {
        var assetJobsHistoryListAdapter =
            AssetJobsHistoryListAdapter(this@AssetJobsHistoryActivity, jobsList, this@AssetJobsHistoryActivity)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        assetJobsHistoryBinding.rvJobHistory.layoutManager = linearLayoutManager
        assetJobsHistoryBinding.rvJobHistory.adapter = assetJobsHistoryListAdapter
        assetJobsHistoryBinding.rvJobHistory.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })
    }

}