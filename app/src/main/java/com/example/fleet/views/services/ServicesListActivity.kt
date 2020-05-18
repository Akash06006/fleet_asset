package com.example.fleet.views.services

import android.os.Build
import android.os.Handler
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleet.R
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.databinding.ActivityServicesListBinding
import com.example.fleet.model.LoginResponse
import com.example.fleet.utils.BaseActivity
import com.example.fleet.viewmodels.services.ServicesViewModel
import com.example.fleet.views.home.HomeFragment
import com.example.fleet.views.home.JobRequestsFragment
import com.google.android.material.tabs.TabLayout
import com.uniongoods.adapters.ServicesListAdapter

class ServicesListActivity : BaseActivity() {
    lateinit var servicesListBinding: ActivityServicesListBinding
    lateinit var servicesViewModel: ServicesViewModel
    private var servicesList = ArrayList<LoginResponse.Data>()
    var fragment: Fragment? = null
    var from = ""

    override fun initViews() {
        servicesListBinding = viewDataBinding as ActivityServicesListBinding
        servicesViewModel = ViewModelProviders.of(this).get(ServicesViewModel::class.java)
        servicesListBinding.servicesViewModel = servicesViewModel
        fragment = UpcomingServicesFragment()
        callFragments(fragment, supportFragmentManager, false, "send_data", "")
        servicesListBinding.toolbarCommon.imgToolbarText.text =
                resources.getString(R.string.services)

        from = intent.extras?.get("from").toString()
        servicesListBinding!!.tablayout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var fragment: Fragment? = null
                servicesListBinding!!.toolbarCommon.imgRight.visibility = View.GONE

                when (tab!!.position) {
                    0 -> fragment = UpcomingServicesFragment()
                    1 -> fragment = CompletedServicesFragment()
                }
                callFragments(fragment, supportFragmentManager, false, "send_data", "")
                /* Handler().postDelayed({
                     setHeadings()
                 }, 300)*/

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //var fragment : Fragment? = null
                //Not In use
            }
        })


    }

    override fun getLayoutId(): Int {
        return R.layout.activity_services_list
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()

        if (GlobalConstants.selectedCheckedFragment == 100) {
            servicesListBinding!!.tablayout.getTabAt(GlobalConstants.selectedFragment)!!.select()
            GlobalConstants.selectedCheckedFragment = 0
        }

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}
