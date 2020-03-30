package com.uniongoods.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fleet.R
import com.example.fleet.databinding.FuelEntryItemBinding
import com.example.fleet.databinding.RequestItemBinding
import com.example.fleet.model.fuel.FuelListResponse
import com.example.fleet.utils.Utils
import com.example.fleet.views.fuel.FuelEntryList

class FuelEntryListAdapter(
    context : FuelEntryList,
    jobsList : ArrayList<FuelListResponse.Data>,
    var activity : Context
) :
    RecyclerView.Adapter<FuelEntryListAdapter.ViewHolder>() {
    private val mContext : FuelEntryList
    private var viewHolder : ViewHolder? = null
    private var fuelEntryList : ArrayList<FuelListResponse.Data>

    init {
        this.mContext = context
        this.fuelEntryList = jobsList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.fuel_entry_item,
            parent,
            false
        ) as FuelEntryItemBinding
        return ViewHolder(binding.root, viewType, binding, mContext, fuelEntryList)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        //holder.binding!!.tvAddress.text = fuelEntryList!![position]
        /* holder.binding!!.tvFromLocationName.text = fuelEntryList[position].from_location
         holder.binding.tvToLocationName.text = fuelEntryList[position].to_location
         holder.binding.tvTimeValue.text = Utils(activity).getDate(
             "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
             fuelEntryList[position].scheduleDatetime,
             "dd-MMM,yyyy | hh:mm a"
         )
         holder.binding.tvTypeValue.text = fuelEntryList[position].jobType
         if (fuelEntryList[position].jobType == "Taxi") {
             holder.binding.tvLoad.text = mContext.getString(R.string.persons)
             holder.binding.tvLoadValue.text = fuelEntryList[position].passengers
         } else {
             holder.binding.tvLoad.text = mContext.getString(R.string.load)
             holder.binding.tvLoadValue.text =
                 fuelEntryList[position].loadTones + " " + mContext.getString(R.string.tons)
         }*/
        holder.binding!!.tvDateValue.text = fuelEntryList[position].entry_date /*Utils(activity).getDate(
               "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            fuelEntryList[position].entry_date,
               "dd-MMM-yyyy"
           )*/

        holder.binding!!.tvAmountValue.text = fuelEntryList[position].price
        holder.binding!!.tvVehicleName.text = fuelEntryList[position].vehicleDetail?.name

    }

    override fun getItemCount() : Int {
        return fuelEntryList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : FuelEntryItemBinding?,
        mContext : FuelEntryList,
        jobsList : ArrayList<FuelListResponse.Data>
    ) : RecyclerView.ViewHolder(v) {
        /* init {
             binding!!.linAddress.setOnClickListener {
                 mContext.deleteAddress(adapterPosition)
             }
         }*/
    }
}