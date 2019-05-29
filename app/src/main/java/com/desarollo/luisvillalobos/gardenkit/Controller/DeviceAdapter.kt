package com.desarollo.luisvillalobos.gardenkit.Controller


import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.desarollo.luisvillalobos.gardenkit.Model.Device
import com.desarollo.luisvillalobos.gardenkit.R
import kotlinx.android.synthetic.main.list_devices.*

class DeviceAdapter(internal var context: Context, private var deviceList: ArrayList<Device>) : BaseAdapter() {

    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItem(position: Int): Any {
        return deviceList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return deviceList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if(convertView == null){
            view = inflater.inflate(R.layout.item_device,parent,false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val currentItem = deviceList[position] as Device

        viewHolder.lblDescription.text =currentItem.description
        viewHolder.lblDevice.text =currentItem.device_request
        viewHolder.lblApiKey.text =currentItem.apikey_request
        viewHolder.lblUser.text = currentItem.name


        convertView?.setOnClickListener() { v ->
            Log.e("Bitzero", "getView(): item clicked ${v.id}")

        }

        return view
    }

    class ViewHolder(view: View) {
        val lblDescription = view.findViewById(R.id.lblDescription) as TextView
        val lblDevice = view.findViewById(R.id.lblDevice) as TextView
        val lblApiKey = view.findViewById(R.id.lblApiKey) as TextView
        val lblUser = view.findViewById(R.id.lblUser) as TextView
    }
}


/*
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.desarollo.luisvillalobos.gardenkit.Model.Device
import com.desarollo.luisvillalobos.gardenkit.R

class DeviceAdapter : BaseAdapter {
    private var deviceList = ArrayList<Device>()
    private var context: Context? = null

    constructor(context: Context, deviceList: ArrayList<Device>) :super(){
        this.deviceList = deviceList
        this.context = context
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View?
        val viewHolder : ViewHolder

        if (convertView == null){
            view = layoutInflater.from(convertView).inflate(R.layout.list_devices,parent,false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
            Log.i("BitZero","Set TAG for ViewHolder,position: $position")
        }else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.lblApiKey?.text = deviceList[position].apikey_request
        viewHolder.lblDevice?.text = deviceList[position].device_request
        viewHolder.lblUser?.text = deviceList[position].name
        viewHolder.lblDescription?.text = deviceList[position].description

        return view
    }

    override fun getItem(position: Int): Any {
        return deviceList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return deviceList.size
    }

    private class ViewHolder(view: View?){
        val lblDescription: TextView?
        val lblDevice: TextView?
        val lblApiKey :TextView?
        val lblUser: TextView?
        init {
            this.lblDescription = view?.findViewById(R.id.lblDescription) as TextView
            this.lblDevice = view?.findViewById(R.id.lblDevice) as TextView
            this.lblApiKey = view?.findViewById(R.id.lblApiKey) as TextView
            this.lblUser = view?.findViewById(R.id.lblUser) as TextView
        }
    }
}
        **///