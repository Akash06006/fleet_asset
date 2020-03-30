package com.example.fleet.views.fuel

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleet.R
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.databinding.ActivityFuelEntryListBinding
import com.example.fleet.model.fuel.FuelListResponse
import com.example.fleet.model.home.JobsResponse
import com.example.fleet.utils.BaseActivity
import com.example.fleet.viewmodels.fuel.FuelViewModel
import com.example.fleet.views.home.DashboardActivity
import com.uniongoods.adapters.FuelEntryListAdapter
import com.uniongoods.adapters.MyJobsListAdapter

class FuelEntryList : BaseActivity() {
    lateinit var activityFuelEntryList : ActivityFuelEntryListBinding
    private lateinit var fuelViewModel : FuelViewModel
    private var fuelList = ArrayList<FuelListResponse.Data>()
    override fun getLayoutId() : Int {
        return R.layout.activity_fuel_entry_list
    }

    override fun initViews() {
        activityFuelEntryList = viewDataBinding as ActivityFuelEntryListBinding
        fuelViewModel = ViewModelProviders.of(this).get(FuelViewModel::class.java)
        activityFuelEntryList.fuelViewModel = fuelViewModel
        activityFuelEntryList.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.fuel_entry_list)

        if (UtilsFunctions.isNetworkConnected()) {
            fuelViewModel.getFuelEntryList()
            startProgressDialog()
        }


        fuelViewModel.getFuelList().observe(this,
            Observer<FuelListResponse> { response->
                if (response != null) {
                    stopProgressDialog()
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            fuelList.addAll(response.data!!)
                            activityFuelEntryList.rvJobs.visibility = View.VISIBLE
                            activityFuelEntryList.tvNoRecord.visibility = View.GONE
                            initRecyclerView()
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }

                }
            })


        fuelViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "img_add_fuel" -> {
                        val intent = Intent(this, AddFuelDetailActivity::class.java)
                        startActivity(intent)
                    }
                }

            })
        )
    }

    private fun initRecyclerView() {
        var fuelListAdapter = FuelEntryListAdapter(this@FuelEntryList, fuelList!!, this!!)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        activityFuelEntryList.rvJobs.layoutManager = linearLayoutManager
        activityFuelEntryList.rvJobs.adapter = fuelListAdapter
        activityFuelEntryList.rvJobs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })
    }
}
