package com.uniongoods.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fleet.R
import com.example.fleet.databinding.JobItemBinding
import com.example.fleet.model.home.JobsResponse
import com.example.fleet.utils.Utils
import com.example.fleet.views.home.HomeFragment
import com.example.fleet.views.home.JobsHistoryActivity

class JobsHistoryListAdapter(
    context : JobsHistoryActivity,
    addressList : ArrayList<JobsResponse.Data>,
    var activity : Context
) :
    RecyclerView.Adapter<JobsHistoryListAdapter.ViewHolder>() {
    private val mContext : JobsHistoryActivity
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
            R.layout.job_item,
            parent,
            false
        ) as JobItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, jobsList)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        holder.binding!!.btnLayout.visibility = View.GONE
        holder.binding!!.tvFromLocationName.text = jobsList[position].from_location
        holder.binding.tvToLocationName.text = jobsList[position].to_location
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
    }

    override fun getItemCount() : Int {
        return jobsList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : JobItemBinding?,
        mContext : JobsHistoryActivity,
        addressList : ArrayList<JobsResponse.Data>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}