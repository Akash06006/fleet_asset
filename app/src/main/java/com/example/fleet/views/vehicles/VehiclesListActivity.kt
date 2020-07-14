package com.example.fleet.views.vehicles

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleet.R
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.databinding.ActivityFuelEntryListBinding
import com.example.fleet.model.vehicle.VehicleListResponse
import com.example.fleet.utils.BaseActivity
import com.example.fleet.viewmodels.fuel.FuelViewModel
import com.example.fleet.views.notifications.NotificationsListActivity
import com.uniongoods.adapters.VehiclesListAdapter

class VehiclesListActivity : BaseActivity() {
    lateinit var activityFuelEntryList: ActivityFuelEntryListBinding
    private lateinit var fuelViewModel: FuelViewModel
    private var vehicleList = ArrayList<VehicleListResponse.Data>()
    override fun getLayoutId(): Int {
        return R.layout.activity_fuel_entry_list
    }

    override fun initViews() {
        activityFuelEntryList = viewDataBinding as ActivityFuelEntryListBinding
        fuelViewModel = ViewModelProviders.of(this).get(FuelViewModel::class.java)
        activityFuelEntryList.fuelViewModel = fuelViewModel
        activityFuelEntryList.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.vehicles)


        activityFuelEntryList.imgAddFuel.visibility = View.GONE
        //initRecyclerView()
        fuelViewModel.getVehicleList().observe(this,
            Observer<VehicleListResponse> { response ->
                if (response != null) {
                    stopProgressDialog()
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            vehicleList.clear()
                            if (response.data != null && response.data!!.size > 0) {
                                vehicleList.addAll(response.data!!)
                                activityFuelEntryList.rvJobs.visibility = View.VISIBLE
                                activityFuelEntryList.tvNoRecord.visibility = View.GONE
                                initRecyclerView()
                            } else {
                                message?.let { UtilsFunctions.showToastError(it) }
                            }

                        }

                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }

                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check if the request code is same as what is passed  here it is 2
        if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            if (UtilsFunctions.isNetworkConnected()) {
                fuelViewModel.getFuelEntryList()
                vehicleList.clear()
                startProgressDialog()
            }

        }
    }

    private fun initRecyclerView() {
        var fuelListAdapter = VehiclesListAdapter(this@VehiclesListActivity, vehicleList!!, this!!)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        activityFuelEntryList.rvJobs.layoutManager = linearLayoutManager
        activityFuelEntryList.rvJobs.adapter = fuelListAdapter
        activityFuelEntryList.rvJobs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (UtilsFunctions.isNetworkConnected()) {
            vehicleList.clear()
            fuelViewModel.getVehcileList()
            startProgressDialog()
        }

    }

    fun openDetailActivity(id: String?) {
        val intent = Intent(this, VehicleDetailActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }
}
