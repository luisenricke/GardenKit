package com.desarollo.luisvillalobos.gardenkit.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.*
import com.desarollo.luisvillalobos.gardenkit.Controller.DBHelper
import com.desarollo.luisvillalobos.gardenkit.Controller.DeviceAdapter
import com.desarollo.luisvillalobos.gardenkit.Controller.DeviceCursorAdapter
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity
import com.desarollo.luisvillalobos.gardenkit.Model.Device
import com.desarollo.luisvillalobos.gardenkit.Model.User
import com.desarollo.luisvillalobos.gardenkit.R
import kotlinx.android.synthetic.main.list_devices.*

class ListDevices : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemClickListener/*, AdapterView.OnItemLongClickListener*/ {

    companion object {
        const val DEVICE_PARCEABLE_TAG = "device"
    }

    private var key: Int = 0
    private lateinit var deviceList: ArrayList<Device>
    private lateinit var deviceAdapter: DeviceAdapter
    private val REQUEST_FORMDEVICE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_devices)

        //Check if user is logged
        try {
            key = Integer.parseInt("" + getSharedPreferences(Login.PREFS_NAME, Context.MODE_PRIVATE).getString(Login.USERID, null))
        } catch (e: NumberFormatException) {
            var settingss: SharedPreferences = getSharedPreferences(Login.PREFS_NAME, Context.MODE_PRIVATE)
            var editor: SharedPreferences.Editor = settingss.edit()
                    .putBoolean(Login.IS_LOGGED, false)
                    .remove(Login.USERID)
            editor.apply()

            val intent = Intent(this, Login::class.java)//FIXME: Check flags of intent
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        //SetUp All Activity
        setup()
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
        lvDevice.onItemClickListener = this

        //Database
        DBHelper.openDB(baseContext)

        //Setting ListView
        try {
            //var cursor:Cursor = Device.readDevicesWithUser(key)!!
            //val deviceAdapter = DeviceCursorAdapter(applicationContext,cursor)
            deviceList = Device.readDevicesWithUser(key)!!
            deviceAdapter = DeviceAdapter(this, deviceList)
            lvDevice.adapter = deviceAdapter
            registerForContextMenu(lvDevice)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //ClickListeners buttons implementation
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_logout -> logOutBtnClick()
            R.id.btn_add -> addBtnClick()
            R.id.btn_home -> homeBtnClick()
        }
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

    //ClickListener from ListView implementation
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val lblName: TextView = view!!.findViewById(R.id.lblUser)
        var device: Device = Device.Companion.readDevice(lblName.text.toString())!!
        var intent: Intent = Intent(this, Graphs::class.java)
        intent.putExtra(DEVICE_PARCEABLE_TAG, device)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }

    // ContexMenu implementation
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v?.id == R.id.lvDevice) {
            var menuInflater: MenuInflater = menuInflater
            menuInflater.inflate(R.menu.listview_menu, menu)
            menu?.setHeaderTitle("Selecciones una operación")
        }
    }

    //TODO: make function to delete and modify items
    //TODO: evaluate if get view for choose action
    override fun onContextItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.item_modificar -> {
                log("click modificar")
                return true
            }
            R.id.item_eliminar -> {
                log("click eliminar")
                return true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    //Cycle of life from Activity
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

    //Extras
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FORMDEVICE && resultCode == Activity.RESULT_OK) {
            val device: Device? = data?.extras!!.getParcelable(DEVICE_PARCEABLE_TAG)
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

    //Message
    private fun toast(message: String) {
        Toast.makeText(baseContext, "$message", Toast.LENGTH_LONG).show()
    }

    private fun log(message: String) {
        Log.e("BitZero", "$message")
    }
}