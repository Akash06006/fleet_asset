package com.example.fleet.views.notifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fleet.R
import com.example.fleet.application.MyApplication
import com.example.fleet.common.UtilsFunctions
import com.example.fleet.databinding.ActivityNotificationsListBinding
import com.example.fleet.model.CommonModel
import com.example.fleet.model.home.JobsResponse
import com.example.fleet.model.notificaitons.NotificationsListResponse
import com.example.fleet.utils.BaseActivity
import com.example.fleet.utils.Utils
import com.example.fleet.viewmodels.notifications.NotificationsViewModel
import com.uniongoods.adapters.MyJobsListAdapter
import com.uniongoods.adapters.NotificationsListAdapter

class NotificationsListActivity : BaseActivity() {
    lateinit var notificationsListBinding : ActivityNotificationsListBinding
    lateinit var notificationsViewModel : NotificationsViewModel
    private var notificationList = ArrayList<NotificationsListResponse.Data>()
    override fun getLayoutId() : Int {
        return R.layout.activity_notifications_list
    }

    override fun initViews() {
        notificationsListBinding = viewDataBinding as ActivityNotificationsListBinding
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        notificationsViewModel.getNotificationList()
        notificationsListBinding.notificationViewModel = notificationsViewModel
        notificationsListBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.notifications)
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
        }

        notificationsViewModel.getNotificationList().observe(this,
            Observer<NotificationsListResponse> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            notificationList.addAll(response.data!!)
                            notificationsListBinding.rvNotification.visibility = View.VISIBLE
                            notificationsListBinding.tvNoRecord.visibility = View.GONE
                            initRecyclerView()
                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> message?.let {
                            UtilsFunctions.showToastError(message)
                            notificationsListBinding.rvNotification.visibility = View.GONE
                            notificationsListBinding.tvNoRecord.visibility = View.VISIBLE
                            notificationsListBinding.btnClear.visibility = View.GONE
                        }
                    }

                }
            })

        notificationsViewModel.clearAll().observe(this,
            Observer<CommonModel> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            UtilsFunctions.showToastSuccess(message!!)
                            notificationList.clear()
                            notificationsListBinding.rvNotification.visibility = View.GONE
                            notificationsListBinding.tvNoRecord.visibility = View.VISIBLE
                            notificationsListBinding.btnClear.visibility = View.GONE

                        }
                        /* response.code == 204 -> {
                             FirebaseFunctions.sendOTP("signup", mJsonObject, this)
                         }*/
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }

                }
            })

        notificationsViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "btn_clear" -> {
                        notificationsViewModel.clearAllNotification()
                    }
                }
            })
        )

    }

    private fun initRecyclerView() {
        var notificationsListAdapter =
            NotificationsListAdapter(
                this@NotificationsListActivity,
                notificationList,
                this@NotificationsListActivity
            )
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        notificationsListBinding.rvNotification.layoutManager = linearLayoutManager
        notificationsListBinding.rvNotification.adapter = notificationsListAdapter
        notificationsListBinding.rvNotification.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })
    }

}
