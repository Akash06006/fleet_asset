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
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.databinding.AssetJobItemBinding
import com.example.fleet.databinding.JobItemBinding
import com.example.fleet.model.home.JobsResponse
import com.example.fleet.utils.Utils
import com.example.fleet.views.asset.AssetJobDetailActivity
import com.example.fleet.views.asset.AssetJobsHistoryActivity
import com.example.fleet.views.home.HomeFragment
import com.example.fleet.views.home.JobsHistoryActivity
import com.example.fleet.views.services.ServicesListActivity

class AssetJobsHistoryListAdapter(
    context : AssetJobsHistoryActivity,
    addressList : ArrayList<JobsResponse.Data>,
    var activity : Context
) :
    RecyclerView.Adapter<AssetJobsHistoryListAdapter.ViewHolder>() {
    private val mContext : AssetJobsHistoryActivity
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
        holder.binding!!.btnLayout.visibility = View.GONE
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
        holder.binding.cardView.setOnClickListener {
            val intent = Intent(mContext, AssetJobDetailActivity::class.java)
            intent.putExtra(GlobalConstants.job_id, jobsList[position].id)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount() : Int {
        return jobsList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : AssetJobItemBinding?,
        mContext : AssetJobsHistoryActivity,
        addressList : ArrayList<JobsResponse.Data>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}