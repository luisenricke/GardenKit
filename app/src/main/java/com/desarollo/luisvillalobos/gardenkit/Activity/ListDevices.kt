package com.desarollo.luisvillalobos.gardenkit.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.desarollo.luisvillalobos.gardenkit.Controller.DBHelper
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity
import com.desarollo.luisvillalobos.gardenkit.Model.Device
import com.desarollo.luisvillalobos.gardenkit.R
import kotlinx.android.synthetic.main.list_devices.*

class ListDevices : AppCompatActivity(), View.OnClickListener {

    private var key: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_devices)
        setup()

        try {
            key = Integer.parseInt("" + getSharedPreferences(Login.PREFS_NAME, Context.MODE_PRIVATE).getString("user_id", null))
        } catch (e: NumberFormatException) {
            var settingss: SharedPreferences = getSharedPreferences(Login.PREFS_NAME, Context.MODE_PRIVATE)
            var editor: SharedPreferences.Editor = settingss.edit()
            editor.putBoolean("logged", false)
            editor.remove("user_id")
            editor.apply()

            val intent = Intent(this, Login::class.java)//FIXME: Check flags of intent
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        var listDevices: ArrayList<Device>? = Device.readDevicesWithUser(key)

/*      var deviceAdapter = DeviceAdapter(this, listDevices!!)
        lvDevice.adapter = deviceAdapter
*/
    }

    override fun onResume() {
        DBHelper.openDB(baseContext)
        super.onResume()
    }

    override fun onPause() {
        DBHelper.closeDB()
        super.onPause()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_logout -> logOutBtnClick()
            R.id.btn_add -> addBtnClick()
            R.id.btn_home -> homeBtnClick()
        }
    }

    private fun setup() {
        //Screen
        SetUpActivity.hiderActionBar(this)
        SetUpActivity.hideStatusBar(this)
        SetUpActivity.hideSoftKeyboard(this)

        //Listeners
        btn_logout.setOnClickListener(this)
        btn_add.setOnClickListener(this)
        btn_home.setOnClickListener(this)

        //Database
        DBHelper.openDB(baseContext)
    }

    private fun logOutBtnClick() {
        var settingss: SharedPreferences = getSharedPreferences(Login.PREFS_NAME, Context.MODE_PRIVATE)
        var editor: SharedPreferences.Editor = settingss.edit()
        editor.putBoolean("logged", false)
        editor.remove("user_id")
        editor.apply()

        val intent = Intent(this, Login::class.java)//FIXME: Check flags of intent
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        finish()
        startActivity(intent)
    }

    private fun addBtnClick() {}
    private fun homeBtnClick() {}


    inner class DeviceAdapter : BaseAdapter {
        private var deviceList = ArrayList<Device>()
        private var context: Context? = null

        constructor(context: Context, deviceList: ArrayList<Device>) : super() {
            this.deviceList = deviceList
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val view: View?
            val viewHolder: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.list_devices, parent, false)
                viewHolder = ViewHolder(view)
                view.tag = viewHolder
                Log.i("BitZero", "Set TAG for ViewHolder,position: $position")
            } else {
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

    }

    private class ViewHolder(view: View?) {
        val lblDescription: TextView?
        val lblDevice: TextView?
        val lblApiKey: TextView?
        val lblUser: TextView?

        init {
            this.lblDescription = view?.findViewById(R.id.lblDescription) as TextView
            this.lblDevice = view?.findViewById(R.id.lblDevice) as TextView
            this.lblApiKey = view?.findViewById(R.id.lblApiKey) as TextView
            this.lblUser = view?.findViewById(R.id.lblUser) as TextView
        }
    }


    override fun onBackPressed() {
        //Para no usar el boton hacia atras
        //super.onBackPressed();
        //overridePendingTransition( 0, 0);
        //System.exit(0);
        moveTaskToBack(true)
    }

    private fun toast(message: String) {
        Toast.makeText(baseContext, "$message", Toast.LENGTH_LONG).show()
    }

    private fun log(message: String) {
        Log.e("BitZero", "$message")
    }

}