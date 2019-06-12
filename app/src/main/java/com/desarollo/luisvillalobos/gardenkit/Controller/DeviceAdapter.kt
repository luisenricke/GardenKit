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

    fun add(device: Device) {
        deviceList.add(device)

    }

    fun delete(device: Device) {
        deviceList.remove(device)
    }

    fun update(list: ArrayList<Device>) {
        deviceList = list
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_device, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val currentItem = deviceList[position]

        viewHolder.lblDescription.text = currentItem.description
        viewHolder.lblDevice.text = currentItem.device_request
        viewHolder.lblApiKey.text = currentItem.apikey_request
        viewHolder.lblUser.text = currentItem.name

        return view
    }

    class ViewHolder(view: View) {
        val lblDescription = view.findViewById(R.id.lblDescription) as TextView
        val lblDevice = view.findViewById(R.id.lblDevice) as TextView
        val lblApiKey = view.findViewById(R.id.lblApiKey) as TextView
        val lblUser = view.findViewById(R.id.lblUser) as TextView
    }
}