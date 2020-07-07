package com.uniongoods.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fleet.R
import com.example.fleet.databinding.ServiceItemBinding
import com.example.fleet.model.services.ServicesListResponse
import com.example.fleet.utils.Utils
import com.example.fleet.views.services.UpcomingServicesFragment
import com.example.fleet.views.services.UpdateServiceActivity
import com.google.gson.JsonObject
import java.util.*
import kotlin.collections.ArrayList

class ServicesListAdapter(
    context : UpcomingServicesFragment,
    jobsList : ArrayList<ServicesListResponse.Data>,
    var activity : Context
) :
    RecyclerView.Adapter<ServicesListAdapter.ViewHolder>() {
    private val mContext : UpcomingServicesFragment
    private var viewHolder : ViewHolder? = null
    private var servicesList : ArrayList<ServicesListResponse.Data>

    init {
        this.mContext = context
        this.servicesList = jobsList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.service_item,
            parent,
            false
        ) as ServiceItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, servicesList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder

        holder.binding!!.tvDueDate.text = Utils(activity).getDate(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            servicesList[position].service_date,
            "yyyy/MM/dd"
        )
       // holder.binding.tvServiceType.text = servicesList[position].vehicle_type

        if (!TextUtils.isEmpty(servicesList[position].vendor?.vendorName)) {
            holder.binding.tvVendorName.visibility = View.VISIBLE
            holder.binding.tvVendorNameValue.visibility = View.VISIBLE
            holder.binding.tvVendorNameValue.text = servicesList[position].vendor?.vendorName
        } else {
            holder.binding.tvVendorName.visibility = View.GONE
            holder.binding.tvVendorNameValue.visibility = View.GONE
        }
        if (servicesList[position].service_for.equals("service")) {
            holder.binding.tvServiceType.text = mContext.getString(R.string.service)
        } else {
            holder.binding.tvServiceType.text = mContext.getString(R.string.renewal)
        }
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        var cur_date = year.toString() + "/" + (month + 1) + "/" + day.toString()
        var date1 = Date(cur_date)
        var date2 = Date(holder.binding.tvDueDate.text.toString())
// To calculate the time difference of two dates
        var Difference_In_Time = date2.getTime() - date1.getTime()
// To calculate the no. of days between two dates
        var Difference_In_Days = Difference_In_Time / (1000 * 3600 * 24)
        if (Difference_In_Days < 0) {
            holder.binding.mainLayout.setBackgroundColor(mContext.resources.getColor(R.color.colorRed))
        } else if (Difference_In_Days >= 0 && Difference_In_Days <= 3) {
            holder.binding.mainLayout.setBackgroundColor(mContext.resources.getColor(R.color.colorSuccess))
        } else if (Difference_In_Days > 3 ) {
            holder.binding.mainLayout.setBackgroundColor(mContext.resources.getColor(R.color.colorLightBlue))
        }
        holder.binding.btnUpdateService.setOnClickListener {
            var vendorId = "0"
            if (!TextUtils.isEmpty(servicesList[position].vendor?.id)) {
                vendorId = servicesList[position].vendor?.id!!
            }
            val mJsonObject = JsonObject()
            mJsonObject.addProperty(
                "vendor_id", vendorId
            )
            mJsonObject.addProperty(
                "service_id", servicesList[position].service_id
            )
            mJsonObject.addProperty(
                "vendor_name", servicesList[position].vendor?.vendorName
            )
            mJsonObject.addProperty(
                "vehicle_id", servicesList[position].vehicle?.id
            )
            val intent = Intent(mContext.baseActivity, UpdateServiceActivity::class.java)
            intent.putExtra("data", mJsonObject.toString())
            mContext.startActivity(intent)
        }

    }

    override fun getItemCount() : Int {
        return servicesList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : ServiceItemBinding?,
        mContext : UpcomingServicesFragment,
        jobsList : ArrayList<ServicesListResponse.Data>
    ) : RecyclerView.ViewHolder(v) {
        /* init {
             binding!!.linAddress.setOnClickListener {
                 mContext.deleteAddress(adapterPosition)
             }
         }*/
    }
}