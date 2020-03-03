package com.example.fleet.views.services

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleet.R
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.databinding.FragmentServicesBinding
import com.example.fleet.model.LoginResponse
import com.example.fleet.model.home.JobsResponse
import com.example.fleet.model.vehicle.ServicesListResponse
import com.example.fleet.utils.BaseFragment
import com.example.fleet.viewmodels.services.ServicesViewModel
import com.google.gson.JsonObject
import com.uniongoods.adapters.MyJobsListAdapter
import com.uniongoods.adapters.ServicesListAdapter

class UpcomingServicesFragment : BaseFragment() {
    private var servicesList = ArrayList<ServicesListResponse.Data>()
    override fun getLayoutResId() : Int {
        return R.layout.fragment_services
    }

    private lateinit var fragmentServicesBinding : FragmentServicesBinding
    private lateinit var servicesViewModel : ServicesViewModel
    private val mJsonObject = JsonObject()
    override fun initView() {
        fragmentServicesBinding = viewDataBinding as FragmentServicesBinding
        servicesViewModel = ViewModelProviders.of(this).get(ServicesViewModel::class.java)
        fragmentServicesBinding.homeViewModel = servicesViewModel
        baseActivity.startProgressDialog()
        //acceptStatus
        mJsonObject.addProperty(
            "acceptStatus", "1"/* sharedPrefClass!!.getPrefValue(
                MyApplication.instance,
                GlobalConstants.USERID
            ).toString()*/
        )
        servicesViewModel.getServices("0")
        //   servicesViewModel.getServicesList()
        servicesViewModel.getServicesList().observe(this,
            Observer<ServicesListResponse> { response->
                if (response != null) {
                    val message = response.message
                    baseActivity.stopProgressDialog()
                    when {
                        response.code == 200 -> {
                            servicesList.addAll(response.data!!)
                            fragmentServicesBinding.rvServices.visibility = View.VISIBLE
                            fragmentServicesBinding.tvNoRecord.visibility = View.GONE
                            initRecyclerView()
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }

                }
            })
        // initRecyclerView()
    }

    private fun initRecyclerView() {
        var servicesListAdapter =
            ServicesListAdapter(this@UpcomingServicesFragment, servicesList, activity!!)
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        fragmentServicesBinding.rvServices.layoutManager = linearLayoutManager
        fragmentServicesBinding.rvServices.adapter = servicesListAdapter
        fragmentServicesBinding.rvServices.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })
    }

}