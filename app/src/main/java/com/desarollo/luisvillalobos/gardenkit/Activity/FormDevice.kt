package com.desarollo.luisvillalobos.gardenkit.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.desarollo.luisvillalobos.gardenkit.Controller.DBHelper
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity
import com.desarollo.luisvillalobos.gardenkit.Model.Device
import com.desarollo.luisvillalobos.gardenkit.R
import kotlinx.android.synthetic.main.form_device.*
import android.widget.EditText



class FormDevice : AppCompatActivity(), View.OnClickListener {

    private var key: Int = 0
    private lateinit var returnIntent: Intent
    private var device: Device? = null
    private var requestOption: Int = 0
    private var idDevice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.form_device)
        setup()
    }

    private fun setup() {
        //Screen
        SetUpActivity.hiderActionBar(this)
        SetUpActivity.hideStatusBar(this)
        SetUpActivity.hideSoftKeyboard(this)

        //Listeners
        btn_action.setOnClickListener(this)
        btn_home.setOnClickListener(this)

        //Extras
        returnIntent = intent
        returnIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
        key = Integer.parseInt("" + getSharedPreferences(Login.PREFS_NAME, Context.MODE_PRIVATE).getString(Login.USERID, null))

        //GetExtras
        device = intent.getParcelableExtra(ListDevices.DEVICE_PARCEABLE_TAG)
        requestOption = intent.extras!!.getInt(ListDevices.DEVICE_OPTION_TAG)
        idDevice = intent.extras!!.getInt(ListDevices.DEVICE_ID_TAG)

        when (requestOption) {
            ListDevices.REQUEST_FORMDEVICE_READ -> {
                in_name.setText(device?.name)
                in_device.setText(device?.device_request)
                in_apikey.setText(device?.apikey_request)
                in_description.setText(device?.description)

                in_name.inputType = InputType.TYPE_NULL
                in_device.inputType = InputType.TYPE_NULL
                in_apikey.inputType = InputType.TYPE_NULL
                in_description.inputType = InputType.TYPE_NULL
                btn_action.visibility = View.INVISIBLE
            }
            ListDevices.REQUEST_FORMDEVICE_UPDATE -> {
                in_name.setText(device?.name)
                in_device.setText(device?.device_request)
                in_apikey.setText(device?.apikey_request)
                in_description.setText(device?.description)
                btn_action.setText("Actualizar dispositivo")
            }
        }
    }

    //ClickListeners buttons implementation
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_action -> actionBtnClick()
            R.id.btn_home -> homeBtnClick()
        }
    }

    private fun actionBtnClick() {
        if (!fieldsNotNull()) {
            toast("Verifique los datos")
            return
        }

        when (requestOption) {
            ListDevices.REQUEST_FORMDEVICE_ADD -> {
                val deviceAux = Device(in_name.text.toString(),
                        in_device.text.toString(),
                        in_apikey.text.toString(),
                        in_description.text.toString(),
                        key)
                returnIntent.putExtra(ListDevices.DEVICE_PARCEABLE_TAG, deviceAux)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
            ListDevices.REQUEST_FORMDEVICE_UPDATE -> {
                val deviceAux = Device(in_name.text.toString(),
                        in_device.text.toString(),
                        in_apikey.text.toString(),
                        in_description.text.toString(),
                        key)
                returnIntent.putExtra(ListDevices.DEVICE_PARCEABLE_TAG, deviceAux)
                returnIntent.putExtra(ListDevices.DEVICE_ID_TAG, idDevice)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }
    }

    private fun homeBtnClick() {
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finishFromChild(parent)
    }

    //Extras
    private fun fieldsNotNull(): Boolean {
        return in_apikey.text.isNotEmpty() &&
                in_description.text.isNotEmpty() &&
                in_device.text.isNotEmpty() &&
                in_name.text.isNotEmpty()
    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        //editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null
        editText.setBackgroundColor(Color.TRANSPARENT)
    }

    //Message
    private fun toast(message: String) {
        Toast.makeText(baseContext, "$message", Toast.LENGTH_LONG).show()
    }

    private fun log(message: String) {
        Log.e("BitZero", "$message")
    }
}