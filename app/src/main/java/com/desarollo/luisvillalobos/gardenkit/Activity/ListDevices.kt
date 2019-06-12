package com.desarollo.luisvillalobos.gardenkit.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.*
import com.desarollo.luisvillalobos.gardenkit.Controller.DBHelper
import com.desarollo.luisvillalobos.gardenkit.Controller.DeviceAdapter
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity
import com.desarollo.luisvillalobos.gardenkit.Model.Device
import com.desarollo.luisvillalobos.gardenkit.R
import kotlinx.android.synthetic.main.list_devices.*

class ListDevices : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemClickListener/*, AdapterView.OnItemLongClickListener*/ {

    companion object {
        const val DEVICE_PARCEABLE_TAG = "device"
        const val DEVICE_OPTION_TAG = "option"
        const val DEVICE_ID_TAG = "id_device"
        const val REQUEST_FORMDEVICE_ADD = 123
        const val REQUEST_FORMDEVICE_READ = 234
        const val REQUEST_FORMDEVICE_UPDATE = 345
    }

    private var key: Int = 0
    private lateinit var deviceList: ArrayList<Device>
    private lateinit var deviceAdapter: DeviceAdapter

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

            val intent = Intent(this, Login::class.java)
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
        editor.putBoolean(Login.IS_LOGGED, false)
        editor.remove(Login.USERID)
        editor.apply()

        val intent = Intent(this, Login::class.java)
        finish()
        startActivity(intent)
    }

    private fun addBtnClick() {
        val intent: Intent = Intent(this, FormDevice::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        intent.putExtra(DEVICE_OPTION_TAG, REQUEST_FORMDEVICE_ADD)
        startActivityForResult(intent, REQUEST_FORMDEVICE_ADD)
    }

    private fun homeBtnClick() {}

    //ClickListener from ListView implementation
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val lblName: TextView = view!!.findViewById(R.id.lblUser)
        val aux = lblName.text.toString()
        val device: Device = Device.readDevice(aux)!!
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

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val info: AdapterView.AdapterContextMenuInfo = item?.menuInfo as AdapterView.AdapterContextMenuInfo
        val deviceSelected = deviceAdapter.getItem(info.position) as Device

        return when (item.itemId) {
            R.id.item_consultar -> {
                val intent: Intent = Intent(this, FormDevice::class.java)
                intent.putExtra(DEVICE_PARCEABLE_TAG, deviceSelected)
                intent.putExtra(DEVICE_OPTION_TAG, REQUEST_FORMDEVICE_READ)
                intent.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivityForResult(intent, REQUEST_FORMDEVICE_UPDATE)
                return true
            }
            R.id.item_modificar -> {
                val intent: Intent = Intent(this, FormDevice::class.java)
                intent.putExtra(DEVICE_PARCEABLE_TAG, deviceSelected)
                intent.putExtra(DEVICE_ID_TAG, deviceSelected.id)
                intent.putExtra(DEVICE_OPTION_TAG, REQUEST_FORMDEVICE_UPDATE)
                intent.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivityForResult(intent, REQUEST_FORMDEVICE_UPDATE)
                return true
            }
            R.id.item_eliminar -> {
                if (Device.deleteDevice(deviceSelected.id)) {
                    deviceAdapter.delete(deviceSelected)
                    deviceAdapter.notifyDataSetChanged()
                    deviceAdapter.update(Device.readDevicesWithUser(key)!!)
                    toast("Dispositivo eliminado")
                } else
                    toast("Hubo algun problema al eliminarlo")
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

        if (resultCode == Activity.RESULT_OK) {
            val device: Device? = data?.extras!!.getParcelable(DEVICE_PARCEABLE_TAG)
            DBHelper.openDB(baseContext)
            if (requestCode == REQUEST_FORMDEVICE_ADD) {
                if (Device.createDevice(device!!)) {
                    deviceAdapter.add(device)
                    deviceAdapter.notifyDataSetChanged()
                    deviceAdapter.update(Device.readDevicesWithUser(key)!!)
                    toast("Dispositivo agregado")
                } else
                    toast("El nombre ya ha sido utilizado, intente otro")
            }

            if (requestCode == REQUEST_FORMDEVICE_UPDATE) {
                device?.id = data.extras!!.getInt(DEVICE_ID_TAG)
                if (Device.updateDevice(device!!)) {
                    deviceAdapter.update(Device.readDevicesWithUser(key)!!)
                    deviceAdapter.notifyDataSetChanged()
                    toast("Dispositivo actualizado")
                } else
                    toast("No se ha podido actualizar el dispositivo")
            }
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