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
import com.bumptech.glide.Glide
import com.example.fleet.R
import com.example.fleet.constants.GlobalConstants
import com.example.fleet.databinding.VehicleItemBinding
import com.example.fleet.model.vehicle.VehicleListResponse
import com.example.fleet.views.vehicles.VehiclesListActivity

class VehiclesListAdapter(
    context: VehiclesListActivity,
    jobsList: ArrayList<VehicleListResponse.Data>,
    var activity: Context
) :
    RecyclerView.Adapter<VehiclesListAdapter.ViewHolder>() {
    private val mContext: VehiclesListActivity
    private var viewHolder: ViewHolder? = null
    private var vehicleList: ArrayList<VehicleListResponse.Data>

    init {
        this.mContext = context
        this.vehicleList = jobsList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.vehicle_item,
            parent,
            false
        ) as VehicleItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, vehicleList)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        viewHolder = holder

        holder.binding!!.txtName.text = vehicleList[position].vehicle?.name
        holder.binding!!.txtNumber.text = vehicleList[position].vehicle?.regNumber

        if (!TextUtils.isEmpty(vehicleList[position].vehicle?.image)) {
            Glide.with(mContext)
                .load(GlobalConstants.BASE_URL + "" + vehicleList[position].vehicle?.image)
                .placeholder(R.drawable.ic_truck_placeholder)
                .into(holder.binding!!.imgVehicle)

        }


        holder.binding.rlRow.setOnClickListener {
            mContext.openDetailActivity(vehicleList[position].vehicle?.id)
        }
    }

    override fun getItemCount(): Int {
        return vehicleList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: VehicleItemBinding?,
        mContext: VehiclesListActivity,
        jobsList: ArrayList<VehicleListResponse.Data>
    ) : RecyclerView.ViewHolder(v) {
        /* init {
             binding!!.linAddress.setOnClickListener {
                 mContext.deleteAddress(adapterPosition)
             }
         }*/
    }
}