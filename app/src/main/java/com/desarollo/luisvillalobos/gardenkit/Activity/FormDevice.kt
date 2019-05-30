package com.desarollo.luisvillalobos.gardenkit.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.desarollo.luisvillalobos.gardenkit.Controller.DBHelper
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity
import com.desarollo.luisvillalobos.gardenkit.Model.Device
import com.desarollo.luisvillalobos.gardenkit.R
import kotlinx.android.synthetic.main.form_device.*

class FormDevice : AppCompatActivity(), View.OnClickListener {

    private var key: Int = 0
    private lateinit var returnIntent: Intent

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
        key = Integer.parseInt("" + getSharedPreferences(Login.PREFS_NAME, Context.MODE_PRIVATE).getString("user_id", null))
    }

    //ClickListeners buttons implementation
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_action -> actionBtnClick()
            R.id.btn_home -> homeBtnClick()
        }
    }

    private fun actionBtnClick() {
        if (fieldsNotNull()) {
            val deviceAux = Device(in_name.text.toString(),
                    in_device.text.toString(),
                    in_apikey.text.toString(),
                    in_description.text.toString(),
                    key)
            returnIntent.putExtra(ListDevices.DEVICE_PARCEABLE_TAG, deviceAux)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        } else
            toast("Verifique los datos")
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

    //Message
    private fun toast(message: String) {
        Toast.makeText(baseContext, "$message", Toast.LENGTH_LONG).show()
    }

    private fun log(message: String) {
        Log.e("BitZero", "$message")
    }
}