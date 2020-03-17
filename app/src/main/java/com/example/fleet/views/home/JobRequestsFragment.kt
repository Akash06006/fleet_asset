package com.example.fleet.views.home

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleet.R
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.databinding.FragmentHomeBinding
import com.example.fleet.model.CommonModel
import com.example.fleet.model.home.JobsResponse
import com.example.fleet.utils.BaseFragment
import com.example.fleet.viewmodels.home.HomeViewModel
import com.google.gson.JsonObject
import com.uniongoods.adapters.JobRequestsAdapter

class JobRequestsFragment : BaseFragment() {
    private var pendingJobsList = ArrayList<JobsResponse.Data>()
    private var myJobsListAdapter : JobRequestsAdapter? = null
    override fun getLayoutResId() : Int {
        return R.layout.fragment_home
    }

    private lateinit var fragmentHomeBinding : FragmentHomeBinding
    private lateinit var homeViewModel : HomeViewModel
    private val mJsonObject = JsonObject()
    override fun initView() {
        fragmentHomeBinding = viewDataBinding as FragmentHomeBinding
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        fragmentHomeBinding.homeViewModel = homeViewModel
        baseActivity.startProgressDialog()
        //acceptStatus
        mJsonObject.addProperty(
            "acceptStatus", "0"
        )

        if (UtilsFunctions.isNetworkConnected()) {
            homeViewModel.getMyJobs("0")
        } else {
            baseActivity.stopProgressDialog()
        }

        homeViewModel.getJobs().observe(this,
            Observer<JobsResponse> { response->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            pendingJobsList.addAll(response.data!!)
                            fragmentHomeBinding.rvJobs.visibility = View.VISIBLE
                            fragmentHomeBinding.tvNoRecord.visibility = View.GONE
                            initRecyclerView()
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)

                            fragmentHomeBinding.rvJobs.visibility = View.GONE
                            fragmentHomeBinding.tvNoRecord.visibility = View.VISIBLE
                        }
                    }

                }
            })

        homeViewModel.acceptReject().observe(this,
            Observer<CommonModel> { response->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            pendingJobsList.clear()
                            pendingJobsList = ArrayList<JobsResponse.Data>()
                            homeViewModel.getMyJobs("0")

                            UtilsFunctions.showToastSuccess(message!!)
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }

                }
            })

    }

    fun jobAccpetReject(jobId : Int?, status : String) {
        mJsonObject.addProperty(
            "acceptStatus", status
        )
        mJsonObject.addProperty(
            "jobId", jobId.toString()
        )
        homeViewModel.acceptRejectJob(mJsonObject)
    }

    private fun initRecyclerView() {
        myJobsListAdapter =
            JobRequestsAdapter(this@JobRequestsFragment, pendingJobsList, activity!!)
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        fragmentHomeBinding.rvJobs.layoutManager = linearLayoutManager
        fragmentHomeBinding.rvJobs.adapter = myJobsListAdapter
        fragmentHomeBinding.rvJobs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })
    }
}