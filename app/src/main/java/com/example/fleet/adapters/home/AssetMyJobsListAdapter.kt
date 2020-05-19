package com.uniongoods.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fleet.R
import com.example.fleet.application.MyApplication
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.databinding.AssetJobItemBinding
import com.example.fleet.databinding.JobItemBinding
import com.example.fleet.model.home.JobsResponse
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.utils.Utils
import com.example.fleet.views.asset.home.AssetHomeFragment
import com.example.fleet.views.home.HomeFragment
import java.util.*
import kotlin.collections.ArrayList

class AssetMyJobsListAdapter(
    context : AssetHomeFragment, addressList : ArrayList<JobsResponse.Data>, var activity : Context
) : RecyclerView.Adapter<AssetMyJobsListAdapter.ViewHolder>() {
    private val mContext : AssetHomeFragment
    private var viewHolder : ViewHolder? = null
    private var jobsList : ArrayList<JobsResponse.Data>

    init {
        this.mContext = context
        this.jobsList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.asset_job_item,
            parent,
            false
        ) as AssetJobItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, jobsList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        holder.binding!!.tvFromLocationName.text = jobsList[position].assignWarehouse!!.fromWarehouse!!.wareName
        holder.binding.tvToLocationName.text = jobsList[position].assignWarehouse!!.toWarehouse!!.wareName
        holder.binding.tvTimeValue.text = Utils(activity).getDate(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            jobsList[position].scheduleDatetime,
            "dd-MMM,yyyy | hh:mm a"
        )
        holder.binding.tvTypeValue.text = jobsList[position].jobType?.jobType
        if (jobsList[position].jobType?.jobType == "Taxi") {
            holder.binding.tvLoad.text = mContext.getString(R.string.persons)
            holder.binding.tvLoadValue.text = jobsList[position].passengers
        } else {
            holder.binding.tvLoad.text = mContext.getString(R.string.load)
            holder.binding.tvLoadValue.text =
                jobsList[position].loadTones + " " + mContext.getString(R.string.tons)
        }
        if (SharedPrefClass().getPrefValue(
                MyApplication.instance.applicationContext,
                GlobalConstants.JOBID
            ).toString().equals("null") && SharedPrefClass().getPrefValue(
                MyApplication.instance.applicationContext,
                GlobalConstants.JOBID
            ).toString().equals("0")
        ) {
            if (jobsList[position].jobId.toString().equals(
                    SharedPrefClass().getPrefValue(
                        MyApplication.instance.applicationContext,
                        GlobalConstants.JOBID
                    ).toString()
                )
            ) {
                holder.binding.btnStart.setText(mContext.getString(com.example.fleet.R.string.track))
            } else {
                holder.binding.btnStart.isEnabled = false
                holder.binding.btnStart.alpha = 0.5f
            }
        }
        // }
        var date = Utils(activity).getDate(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            jobsList[position].scheduleDatetime,
            "dd/MM/yyyy"
        )
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        var cur_date = day.toString() + "/" + (month + 1) + "/" + year
        var date1 = Date(cur_date)
        var date2 = Date(date)
// To calculate the time difference of two dates
        var Difference_In_Time = date2.getTime() - date1.getTime()
// To calculate the no. of days between two dates
        var Difference_In_Days = Difference_In_Time / (1000 * 3600 * 24)
        if (Difference_In_Days <= 0) {
            // holder.binding.mainLayout.setBackgroundColor(mContext.resources.getColor(R.color.colorRed))
            holder.binding.btnStart.isEnabled = true
            holder.binding.btnStart.alpha = 1.0f
        } else {
            holder.binding.btnStart.isEnabled = false
            holder.binding.btnStart.alpha = 0.5f
        }

        holder.binding.btnCancel.setOnClickListener {
            mContext.cancelJob(jobsList[position].id, "2")
        }

        holder.binding.btnStart.setOnClickListener {
            mContext.startJob(
                jobsList[position].id,
                jobsList[position].to_lat,
                jobsList[position].to_longt,
                holder.binding.btnStart.text.toString()
            )
        }
        holder.binding.cardView.setOnClickListener {
            mContext.openJobDetail(jobsList[position].id)
        }
    }

    override fun getItemCount() : Int {
        return jobsList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : AssetJobItemBinding?,
        mContext : AssetHomeFragment,
        addressList : ArrayList<JobsResponse.Data>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}