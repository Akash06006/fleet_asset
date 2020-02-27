package com.example.fleet.views.home

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.view.Gravity
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.fleet.R
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.databinding.ActivityDashboardBinding
import com.example.fleet.model.CommonModel
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.socket.TrackingActivity
import com.example.fleet.utils.BaseActivity
import com.example.fleet.utils.DialogClass
import com.example.fleet.utils.DialogssInterface
import com.example.fleet.views.authentication.LoginActivity
import com.example.fleet.views.authentication.RegisterActivity
import com.example.fleet.views.settings.MyAccountsActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout

class DashboardActivity : BaseActivity(),
    DialogssInterface {
    var activityDashboardBinding : ActivityDashboardBinding? = null
    private var navigationView : NavigationView? = null
    private var drawer : DrawerLayout? = null
    private var confirmationDialog : Dialog? = null
    private var ratingDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private var dashboardViewModel : DashboardViewModel? = null
    private var removedFrag : String = ""
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
        dashboardViewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                activityDashboardBinding!!.toolbarCommon.imgRight.visibility = View.GONE

                when (it) {
                    "tv_nav_home" -> {
                        activityDashboardBinding!!.toolbarCommon.imgRight.visibility = View.VISIBLE
                        activityDashboardBinding!!.toolbarCommon.imgRight.setImageDrawable(
                            getDrawable(R.drawable.ic_email)
                        )
                        val fragment = HomeFragment()
                        activityDashboardBinding!!.toolbarCommon.imgToolbarText.text =
                            getString(R.string.home)
                        activityDashboardBinding!!.drawerLayout.closeDrawers()
                        this.callFragments(
                            fragment,
                            supportFragmentManager,
                            false,
                            "send_data",
                            ""
                        )
                        activityDashboardBinding!!.tablayout.getTabAt(2)!!.select()
                        activityDashboardBinding!!.drawerLayout.closeDrawers()

                    }
                   "tv_nav_contact" -> {
                        val intent = Intent(this, TrackingActivity::class.java)
                        startActivity(intent)
                    }
                    "tv_nav_account" -> {
                        val intent = Intent(this, MyAccountsActivity::class.java)
                        startActivity(intent)
                    }
                    "img_nav_cancel" -> {
                        activityDashboardBinding!!.drawerLayout.closeDrawers()
                    }
                    "toolbar" -> {
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
            Observer<CommonModel> { logoutResponse->
                this.stopProgressDialog()

                if (logoutResponse != null) {
                    val message = logoutResponse.message

                    if (logoutResponse.code == 200) {
                        SharedPrefClass().putObject(
                            this,
                            "isLogin",
                            false
                        )

                        showToastSuccess(message)
                        val intent1 = Intent(this, LoginActivity::class.java)
                        startActivity(intent1)
                        finish()

                    } else {
                        showToastError(message)
                    }
                }
            })

        dashboardViewModel!!.isLoading().observe(this, Observer<Boolean> { aBoolean->
            if (aBoolean!!) {
                this.startProgressDialog()
            } else {
                this.stopProgressDialog()
            }
        })

        activityDashboardBinding!!.tablayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab : TabLayout.Tab?) {
                var fragment : Fragment? = null
                activityDashboardBinding!!.toolbarCommon.imgRight.visibility = View.GONE

                when (tab!!.position) {
                    0 -> fragment = HomeFragment()
                    1-> fragment = HomeFragment()
                }
                callFragments(fragment, supportFragmentManager, false, "send_data", "")

                Handler().postDelayed({
                    setHeadings()
                }, 300)

            }

            override fun onTabUnselected(tab : TabLayout.Tab?) {

            }

            override fun onTabReselected(tab : TabLayout.Tab?) {
                //var fragment : Fragment? = null

               //Not In use
            }
        })

    }

    override fun getLayoutId() : Int {
        return R.layout.activity_dashboard
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setHeadings() {
        val fragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
        when (fragment) {
            is HomeFragment -> {
                activityDashboardBinding!!.toolbarCommon.imgToolbarText.text =
                    getString(R.string.home)
                getString(R.string.calendar)

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        setHeadings()

        if (GlobalConstants.selectedCheckedFragment == 100) {
            activityDashboardBinding!!.tablayout.getTabAt(GlobalConstants.selectedFragment)!!.select()
            GlobalConstants.selectedCheckedFragment = 0
        }

    }

    override fun onDialogConfirmAction(mView : View?, mKey : String) {
        when (mKey) {
            "logout" -> {
                confirmationDialog?.dismiss()
                dashboardViewModel!!.callLogoutApi()

            }
//            "rating" -> {
//                ratingDialog?.dismiss()
//            }
        }
    }

    override fun onDialogCancelAction(mView : View?, mKey : String) {
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