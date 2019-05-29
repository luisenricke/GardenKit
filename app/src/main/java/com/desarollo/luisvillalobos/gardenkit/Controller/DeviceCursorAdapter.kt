package com.desarollo.luisvillalobos.gardenkit.Controller

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import com.desarollo.luisvillalobos.gardenkit.Model.Device
import com.desarollo.luisvillalobos.gardenkit.R

class DeviceCursorAdapter(context: Context, cursor: Cursor) : CursorAdapter(context, cursor, 0) {
    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return LayoutInflater.from(context).inflate(R.layout.item_device, parent, false)
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val lblDescription = view!!.findViewById(R.id.lblDescription) as TextView
        lblDescription.text = cursor!!.getString(cursor.getColumnIndexOrThrow(Device.COLUMN_DESCRIPTION))

        val lblDevice = view!!.findViewById(R.id.lblDevice) as TextView
        lblDevice.text = cursor!!.getString(cursor.getColumnIndexOrThrow(Device.COLUMN_DEVICE_REQUEST))

        val lblApiKey = view!!.findViewById(R.id.lblApiKey) as TextView
        lblApiKey.text = cursor!!.getString(cursor.getColumnIndexOrThrow(Device.COLUMN_APIKEY_REQUEST))

        val lblUser = view!!.findViewById(R.id.lblUser) as TextView
        lblUser.text = cursor!!.getString(cursor.getColumnIndexOrThrow(Device.COLUMN_NAME))
    }
}