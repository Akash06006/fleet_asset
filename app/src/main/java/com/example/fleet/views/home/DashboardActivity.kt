package com.example.fleet.views.home

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.fleet.R
import com.example.fleet.application.MyApplication
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.databinding.ActivityDashboardBinding
import com.example.fleet.model.CommonModel
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.socket.TrackingActivity
import com.example.fleet.utils.BaseActivity
import com.example.fleet.utils.DialogClass
import com.example.fleet.utils.DialogssInterface
import com.example.fleet.views.asset.AssetJobsHistoryActivity
import com.example.fleet.views.asset.TrackingMapsActivity
import com.example.fleet.views.asset.home.AssetHomeFragment
import com.example.fleet.views.asset.home.AssetJobRequestsFragment
import com.example.fleet.views.authentication.LoginActivity
import com.example.fleet.views.fuel.AddFuelDetailActivity
import com.example.fleet.views.fuel.FuelEntryList
import com.example.fleet.views.notifications.NotificationsListActivity
import com.example.fleet.views.profile.ProfileActivity
import com.example.fleet.views.services.ServicesListActivity
import com.example.fleet.views.settings.MyAccountsActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout

class DashboardActivity : BaseActivity(),
        DialogssInterface {
    var activityDashboardBinding: ActivityDashboardBinding? = null
    private var navigationView: NavigationView? = null
    private var drawer: DrawerLayout? = null
    private var confirmationDialog: Dialog? = null
    private var ratingDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    private var dashboardViewModel : DashboardViewModel? = null
    private var removedFrag : String = ""
    private var fleetHomeSelected : Boolean =
        true //this param according to the user role if employee of asset management make it false and if of fleet than true else for both also true
    var roleAssigned : String = ""
    var fragment : Fragment? = null
    var from = ""
    //    companion object {
//        @get:Synchronized
//        lateinit var toolBarText : TextView
//        lateinit var toolBarImage : ImageView
//        var removedFrag : String = ""
//
//    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initViews() {
        activityDashboardBinding = viewDataBinding as ActivityDashboardBinding
        navigationView = activityDashboardBinding!!.navView
        navigationView!!.alpha = 0.9f
        drawer = activityDashboardBinding!!.drawerLayout
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        activityDashboardBinding!!.dashboardViewModel = dashboardViewModel
        // toolBarText = activityDashboardBinding!!.toolbarCommon.imgToolbarText
        // toolBarImage = activityDashboardBinding!!.toolbarCommon.imgRight
        /** Show Rating Dialog here**/
        // checkForRating(0)
        /*****************/
        roleAssigned = SharedPrefClass().getPrefValue(
            MyApplication.instance.applicationContext,
            GlobalConstants.USERROLE
        ).toString()
        if (roleAssigned.equals(GlobalConstants.DRIVER)) {
            fleetHomeSelected = true
            activityDashboardBinding!!.llAsset.visibility = View.GONE
            activityDashboardBinding!!.llFleet.visibility = View.VISIBLE
            fragment = HomeFragment()

        } else if (roleAssigned.equals(GlobalConstants.WORKER)) {
            fleetHomeSelected = false
            activityDashboardBinding!!.llAsset.visibility = View.VISIBLE
            activityDashboardBinding!!.llFleet.visibility = View.GONE
            fragment = AssetHomeFragment()

        } else if (roleAssigned.equals(GlobalConstants.BOTH)) {
            fleetHomeSelected = true
            activityDashboardBinding!!.llAsset.visibility = View.VISIBLE
            activityDashboardBinding!!.llFleet.visibility = View.VISIBLE
            fragment = HomeFragment()

        }
        activityDashboardBinding!!.toolbarCommon.toolbar.setImageResource(R.drawable.ic_sidebar)
        activityDashboardBinding!!.toolbarCommon.imgRight.visibility = View.VISIBLE
        activityDashboardBinding!!.toolbarCommon.imgRight.setImageResource(R.drawable.ic_notifications)

        from = intent.extras?.get("from").toString()
        if (from.equals("notification")) {
            activityDashboardBinding!!.tablayout.getTabAt(1)!!.select()
            fragment = JobRequestsFragment()
            callFragments(fragment, supportFragmentManager, false, "send_data", "")
        } else {
            fragment = HomeFragment()
            callFragments(fragment, supportFragmentManager, false, "send_data", "")
        }
        val image = SharedPrefClass().getPrefValue(
                MyApplication.instance.applicationContext,
                GlobalConstants.USER_IAMGE
        )
        // ic_profile
        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.user)
                .into(activityDashboardBinding!!.icProfile)
        val name = SharedPrefClass().getPrefValue(
                MyApplication.instance.applicationContext,
                getString(R.string.first_name)
        )
        activityDashboardBinding!!.tvName.text = name.toString()
       // fragment = HomeFragment()
       // callFragments(fragment, supportFragmentManager, false, "send_data", "")
        dashboardViewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "img_right" -> {
                        val intent = Intent(this, NotificationsListActivity::class.java)
                        startActivity(intent)
                    }
                    "tv_nav_notification" -> {
                        val intent = Intent(this, NotificationsListActivity::class.java)
                        startActivity(intent)
                    }
                    "tv_nav_fuel" -> {
                        val intent = Intent(this, FuelEntryList::class.java)
                        startActivity(intent)
                    }
                    "tv_nav_history" -> {
                        val intent = Intent(this, JobsHistoryActivity::class.java)
                        startActivity(intent)
                    }
                    "tv_nav_services" -> {
                        val intent = Intent(this, ServicesListActivity::class.java)
                        intent.putExtra("from", "home")
                        startActivity(intent)
                    }
                    "tv_nav_home" -> {
                        fleetHomeSelected = true
                        activityDashboardBinding!!.toolbarCommon.imgRight.visibility = View.VISIBLE
                        activityDashboardBinding!!.toolbarCommon.imgRight.setImageDrawable(
                            getDrawable(R.drawable.ic_notifications)
                        )
                        val fragment = HomeFragment()
                        activityDashboardBinding!!.toolbarCommon.imgToolbarText.text =
                            getString(R.string.fleet_home)
                        activityDashboardBinding!!.drawerLayout.closeDrawers()
                        this.callFragments(
                            fragment,
                            supportFragmentManager,
                            false,
                            "send_data",
                            ""
                    )
                    activityDashboardBinding!!.tablayout.getTabAt(0)?.select()
                    activityDashboardBinding!!.drawerLayout.closeDrawers()

                    }
                    "tv_nav_contact" -> {
                        showToastSuccess("Coming Soon")
                        /* val intent = Intent(this, TrackingActivity::class.java)
                         startActivity(intent)*/
                    }
                    "tv_asset_home" -> {
                        fleetHomeSelected = false
                        activityDashboardBinding!!.toolbarCommon.imgRight.visibility = View.VISIBLE
                        activityDashboardBinding!!.toolbarCommon.imgRight.setImageDrawable(
                            getDrawable(R.drawable.ic_notifications)
                        )
                        val fragment = AssetHomeFragment()
                        activityDashboardBinding!!.toolbarCommon.imgToolbarText.text =
                            getString(R.string.asset_home)
                        activityDashboardBinding!!.drawerLayout.closeDrawers()
                        this.callFragments(
                            fragment,
                            supportFragmentManager,
                            false,
                            "send_data",
                            ""
                        )
                        activityDashboardBinding!!.tablayout.getTabAt(0)?.select()
                        activityDashboardBinding!!.drawerLayout.closeDrawers()
                        /*val intent = Intent(this, TrackingMapsActivity::class.java)
                        startActivity(intent)*/

                    }
                    "tv_asset_job_history" -> {
                        val intent = Intent(this, AssetJobsHistoryActivity::class.java)
                        startActivity(intent)

                    }
                    "ic_profile" -> {
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                    }
                    "tv_nav_account" -> {
                        val intent = Intent(this, MyAccountsActivity::class.java)
                        startActivity(intent)
                    }
                    "img_nav_cancel" -> {
                        activityDashboardBinding!!.drawerLayout.closeDrawers()
                    }
                    "tv_nav_logout" -> {
                        confirmationDialog = mDialogClass.setDefaultDialog(
                            this,
                            this,
                            "logout",
                            getString(R.string.warning_logout)
                    )
                    confirmationDialog?.show()

                }
                "toolbar" -> {
                    val image = SharedPrefClass().getPrefValue(
                            MyApplication.instance.applicationContext,
                            GlobalConstants.USER_IAMGE
                    )
                    // ic_profile
                    Glide.with(this)
                            .load(image)
                            .placeholder(R.drawable.user)
                            .into(activityDashboardBinding!!.icProfile)
                        val name = SharedPrefClass().getPrefValue(
                            MyApplication.instance.applicationContext,
                            getString(R.string.first_name)
                    )
                    activityDashboardBinding!!.tvName.text = name.toString()
                    if (drawer!!.isDrawerOpen(GravityCompat.START)) {
                        drawer!!.closeDrawer(Gravity.LEFT) //CLOSE Nav Drawer!
                    } else {
                        drawer!!.openDrawer(Gravity.LEFT)
                    }
                    val fragmentType =
                            supportFragmentManager.findFragmentById(R.id.frame_layout)
                    when (fragmentType) {
                        is HomeFragment -> {
                            activityDashboardBinding!!.toolbarCommon.imgRight.visibility =
                                    View.VISIBLE
                        }
                    }
                }
            }
        })
        )

        dashboardViewModel!!.getLogoutReposne.observe(this,
                Observer<CommonModel> { logoutResponse ->
                    this.stopProgressDialog()

                    if (logoutResponse != null) {
                        val message = logoutResponse.message

                        if (logoutResponse.code == 200) {
                            SharedPrefClass().putObject(
                                    this,
                                    "isLogin",
                                    false
                            )
                            SharedPrefClass().putObject(
                                    this,
                                    GlobalConstants.USER_IAMGE,
                                    "null"
                            )
                            showToastSuccess(getString(R.string.logout_msg))
                            val intent1 = Intent(this, LoginActivity::class.java)
                            startActivity(intent1)
                            finish()

                        } else {
                            showToastError(message)
                        }
                    }
                })

        dashboardViewModel!!.isLoading().observe(this, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                this.startProgressDialog()
            } else {
                this.stopProgressDialog()
            }
        })

        activityDashboardBinding!!.tablayout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var fragment: Fragment? = null
                //   activityDashboardBinding!!.toolbarCommon.imgRight.visibility = View.GONE
                when (tab!!.position) {
                    0 ->
                        if (fleetHomeSelected) { // if this param is true than fleet home is selected else asset home is selected
                            fragment = HomeFragment()
                        } else {
                            fragment = AssetHomeFragment()
                        }
                    1 ->
                        if (fleetHomeSelected) { // if this param is true than fleet home is selected else asset home is selected
                            fragment = JobRequestsFragment()
                        } else {
                            fragment = AssetJobRequestsFragment()
                        }
                }
                callFragments(fragment, supportFragmentManager, false, "send_data", "")

                Handler().postDelayed({
                    setHeadings()
                }, 300)

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
        return R.layout.activity_dashboard
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setHeadings() {
        val fragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
        when (fragment) {
            is HomeFragment -> {
                activityDashboardBinding!!.toolbarCommon.imgToolbarText.text =
                    getString(R.string.fleet_home)
                getString(R.string.calendar)

            }
            is AssetHomeFragment -> {
                activityDashboardBinding!!.toolbarCommon.imgToolbarText.text =
                    getString(R.string.asset_home)
                getString(R.string.calendar)

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        setHeadings()
        /* if (GlobalConstants.selectedCheckedFragment == 100) {
             activityDashboardBinding!!.tablayout.getTabAt(GlobalConstants.selectedFragment)!!.select()
             GlobalConstants.selectedCheckedFragment = 0
         }*/
        val image = SharedPrefClass().getPrefValue(
                MyApplication.instance.applicationContext,
                GlobalConstants.USER_IAMGE
        )
        // ic_profile

        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.user)
                .into(activityDashboardBinding!!.icProfile)
        val name = SharedPrefClass().getPrefValue(
                MyApplication.instance.applicationContext,
                getString(R.string.first_name)
        )
        activityDashboardBinding!!.tvName.text = name.toString()

    }

    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        when (mKey) {
            "logout" -> {
                confirmationDialog?.dismiss()
                dashboardViewModel!!.callLogoutApi()
                // dashboardViewModel!!.callLogoutApi()

            }
//            "rating" -> {
//                ratingDialog?.dismiss()
//            }
        }
    }

    override fun onDialogCancelAction(mView: View?, mKey: String) {
        when (mKey) {
            "logout" -> confirmationDialog?.dismiss()
            "rating" -> ratingDialog?.dismiss()
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}