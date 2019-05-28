package com.desarollo.luisvillalobos.gardenkit.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.desarollo.luisvillalobos.gardenkit.Controller.DBHelper
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity

import com.desarollo.luisvillalobos.gardenkit.Model.Device
import com.desarollo.luisvillalobos.gardenkit.Model.User
import com.desarollo.luisvillalobos.gardenkit.R
import kotlinx.android.synthetic.main.login.*
import net.sqlcipher.database.SQLiteDatabase

class Login : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val PREFS_NAME: String = "SGKLog"

    }
    private var actionSelectedOption: Boolean = true//true if select login and false if select signup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        setup()
/*
        var settingss: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var editor: SharedPreferences.Editor = settingss.edit()
        editor.putBoolean("logged", false)
        editor.remove("user_id")
        editor.apply()
*/
        val settings: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (settings.getBoolean("logged", true)) {
            val intent = Intent(baseContext, ListDevices::class.java) //FIXME: Check flags of intent
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
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
            R.id.btn_login -> loginSignUpBtnClick()
            R.id.btn_action -> actionBtnClick()
            R.id.btn_signup -> signUpBtnClick()
        }
    }

    private fun setup() {
        //Screen
        SetUpActivity.hiderActionBar(this)
        SetUpActivity.hideStatusBar(this)
        SetUpActivity.hideSoftKeyboard(this)
        SetUpActivity.setWindowPortrait(this)

        //Listeners
        btn_action.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        btn_signup.setOnClickListener(this)

        //Database
        SQLiteDatabase.loadLibs(baseContext)
        DBHelper.openDB(baseContext)

        if (User.getCount() <= 2L)
            User.fillTable()
        if (Device.getCount() <= 1L)
            Device.fillTable()
    }

    private fun actionBtnClick() {
        if (in_name.text.isEmpty() && in_password.text.isEmpty()) {
            toast("Está vacio los campos de el usuario y/o contraseña")
            return
        }

        if (actionSelectedOption) {
            val id: String? = User.readUser(User(in_name.text.toString(), in_password.text.toString()))
            if (id != null) {
                var settings: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                var editor: SharedPreferences.Editor = settings.edit().putBoolean("logged", true).putString("user_id", id)
                editor.apply()
                toast("Ha iniciado correctamente sesión")
                val intent = Intent(baseContext, ListDevices::class.java)//FIXME: Check flags of intents
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            } else
                toast("Los datos son incorrectos")
        } else {
            if (in_name.text.isNotEmpty() && in_password.text.trim().length > 4) {// FIXME: create function to validate fields
                var user = User("", in_name.text.toString(), in_password.text.toString(), 3)//FIXME: Validate email
                if (User.createUser(user)) {
                    toast("Se ha registrado correctamente")
                    btn_login.performClick()
                } else
                    toast("El usuario ya se encuentra registrado")
            } else
                toast("El usuario y/o la contraseña son muy cortos")
        }
    }

    private fun loginSignUpBtnClick() {
        if (!actionSelectedOption) {
            in_name.setText("")
            in_password.setText("")
            btn_login.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            btn_signup.setTextColor(ContextCompat.getColor(this, R.color.black))
            btn_action.setText("Iniciar")
            actionSelectedOption = true
        }
    }

    private fun signUpBtnClick() {
        if (actionSelectedOption) {
            in_name.setText("")
            in_password.setText("")
            btn_signup.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            btn_login.setTextColor(ContextCompat.getColor(this, R.color.black))
            btn_action.setText("Registrar")
            actionSelectedOption = false
        }
    }

    private fun toast(message: String) {
        Toast.makeText(baseContext, "$message", Toast.LENGTH_LONG).show()
    }
}