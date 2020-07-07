package com.uniongoods.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fleet.R
import com.example.fleet.databinding.ServiceItemBinding
import com.example.fleet.model.services.ServicesListResponse
import com.example.fleet.utils.Utils
import com.example.fleet.views.services.CompletedServicesFragment

class ServicesHistoryListAdapter(
    context : CompletedServicesFragment,
    jobsList : ArrayList<ServicesListResponse.Data>,
    var activity : Context
) :
    RecyclerView.Adapter<ServicesHistoryListAdapter.ViewHolder>() {
    private val mContext : CompletedServicesFragment
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        holder.binding!!.btnUpdateService.visibility = View.GONE
        holder.binding.tvDueCompleted.setText(mContext.getString(R.string.completed_date))
        holder.binding!!.tvDueDate.text = Utils(activity).getDate(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            servicesList[position].service_date,
            "yyyy/MM/dd"
        )
        //holder.binding.tvServiceType.text = servicesList[position].vehicle_type

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

    }

    override fun getItemCount() : Int {
        return servicesList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : ServiceItemBinding?,
        mContext : CompletedServicesFragment,
        jobsList : ArrayList<ServicesListResponse.Data>
    ) : RecyclerView.ViewHolder(v) {
        /* init {
             binding!!.linAddress.setOnClickListener {
                 mContext.deleteAddress(adapterPosition)
             }
         }*/
    }
}