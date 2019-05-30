package com.desarollo.luisvillalobos.gardenkit.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.desarollo.luisvillalobos.gardenkit.Controller.DBHelper
import com.desarollo.luisvillalobos.gardenkit.Controller.DeviceAdapter
import com.desarollo.luisvillalobos.gardenkit.Controller.DeviceCursorAdapter
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity
import com.desarollo.luisvillalobos.gardenkit.Model.Device
import com.desarollo.luisvillalobos.gardenkit.Model.User
import com.desarollo.luisvillalobos.gardenkit.R
import kotlinx.android.synthetic.main.list_devices.*

class ListDevices : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private var key: Int = 0
    private lateinit var deviceList: ArrayList<Device>
    private lateinit var deviceAdapter: DeviceAdapter
    //ADD REQUEST

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

        try {
            //var cursor:Cursor = Device.readDevicesWithUser(key)!!
            //val deviceAdapter = DeviceCursorAdapter(applicationContext,cursor)

            deviceList = Device.readDevicesWithUser(key)!!
            deviceAdapter = DeviceAdapter(this, deviceList)
            lvDevice.adapter = deviceAdapter
        } catch (ex: Exception) {
            log("${ex.message}")
        }
    }

    override fun onStart() {
        super.onStart()
        DBHelper.openDB(baseContext)
    }

    override fun onResume() {
        super.onResume()
        DBHelper.openDB(baseContext)
    }

    override fun onPause() {
        super.onPause()
        DBHelper.closeDB()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_logout -> logOutBtnClick()
            R.id.btn_add -> addBtnClick()
            R.id.btn_home -> homeBtnClick()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        return true
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
        finish()
        startActivity(intent)
    }

    private fun addBtnClick() {
        val intent: Intent = Intent(this, FormDevice::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        startActivityForResult(intent, 1)
    }

    private fun homeBtnClick() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val device: Device? = data?.extras!!.getParcelable("device")
            DBHelper.openDB(baseContext)
            if (Device.createDevice(device!!)) {
                deviceAdapter.add(device)
                deviceAdapter.notifyDataSetChanged()
                toast("Dispositivo agregado")
            } else
                toast("El nombre ya ha sido utilizado, intente otro")
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun toast(message: String) {
        Toast.makeText(baseContext, "$message", Toast.LENGTH_LONG).show()
    }

    private fun log(message: String) {
        Log.e("BitZero", "$message")
    }
}