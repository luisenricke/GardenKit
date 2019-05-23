package com.desarollo.luisvillalobos.gardenkit.Activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.desarollo.luisvillalobos.gardenkit.Model.Device
import com.desarollo.luisvillalobos.gardenkit.Model.User
import com.desarollo.luisvillalobos.gardenkit.R
import net.sqlcipher.database.SQLiteDatabase

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        setup(baseContext)
        testDevice()

    }

    private fun setup(baseContext: Context): Unit {
        SQLiteDatabase.loadLibs(this)

        if (User.getCount(baseContext) <= 2L)
            User.fillTable(baseContext)
        if (Device.getCount(baseContext) <= 1L)
            Device.fillTable(baseContext)
    }

    private fun testDevice(): Unit {
        var device = Device.readDevice(baseContext, 1)
        Log.e("Bitzero", "nombre: ${device?.name} \n" +
                "dispositivo: ${device?.device_request} \n" +
                "api: ${device?.apikey_request} \n" +
                "descripcion: ${device?.description} \n" +
                "idUsuario: ${device?.idUser} \n")

        device = Device()
        device.name = "Dispositivo 1"
        device.device_request = "device@dev_bitbot_test.dev_bitbot_test"
        device.apikey_request = "d8a08f2ed52ec23463402769c3b0ccb6a8e4c6fa4bb6ea77f320cdbf553a2521"
        device.description = "DEV"
        device.idUser = 3
        var log = Device.readDevice(baseContext, device)
        Log.e("Bitzero", "key: $log")

        Device.readDevices(baseContext)?.forEachIndexed { index, device ->
            Log.e("Bitzero", "nombre: ${device.name} \n" +
                    "dispositivo: ${device.device_request} \n" +
                    "api: ${device?.apikey_request} \n" +
                    "descripcion: ${device.description} \n" +
                    "idUsuario: ${device.idUser} \n" +
                    "indice: $index")
        }

        Device.readDevicesWithUser(baseContext, 3)?.forEachIndexed { index, device ->
            Log.e("Bitzero", "nombre: ${device.name} \n" +
                    "dispositivo: ${device.device_request} \n" +
                    "api: ${device.apikey_request} \n" +
                    "descripcion: ${device.description} \n" +
                    "idUsuario: ${device.idUser} \n" +
                    "indice: $index")
        }

        device = Device()
        device.id = 2
        device.apikey_request = "Se cambio"
        device.device_request = "Se cambio"

        Log.e("Bitzero", "Se actualizo ${Device.updateDevice(baseContext, device)}")
        device = Device.readDevice(baseContext, 2)
        Log.e("Bitzero", "nombre: ${device?.name} \n" +
                "dispositivo: ${device?.device_request} \n" +
                "api: ${device?.apikey_request} \n" +
                "descripcion: ${device?.description} \n" +
                "idUsuario: ${device?.idUser} \n")

        Log.e("Bitzero", "Se elimino ${Device.deleteDevice(baseContext, 2)}")
        Log.e("Bitzero", "No : ${Device.getCount(baseContext)}")
        Log.e("Bitzero", "Se elimino todo ${Device.deleteAllDevices(baseContext)}")
        Log.e("Bitzero", "No : ${Device.getCount(baseContext)}")
    }

    private fun testUser(): Unit {
        var user: User? = User.readUser(baseContext, 1)
        Log.e("Bitzero", "email: ${user?.email} " +
                "usuario: ${user?.username} \n" +
                "password: ${user?.password} \n" +
                "rol: ${user?.rol} \n")

        user = User.readUser(baseContext, 2)
        Log.e("Bitzero", "email: ${user?.email} \n" +
                "usuario: ${user?.username} \n" +
                "password: ${user?.password} \n" +
                "rol: ${user?.rol} \n")

        user = User.readUser(baseContext, 3)
        Log.e("Bitzero", "email: ${user?.email} \n" +
                "usuario: ${user?.username} \n" +
                "password: ${user?.password} \n" +
                "rol: ${user?.rol} \n")

        user = User()
        user.username = "test"
        var log = User.readUser(baseContext, user)
        Log.e("Bitzero", "key: $log")

        User.readUsersWithPermission(baseContext, 1)?.forEachIndexed { index, user ->
            Log.e("Bitzero", "email: ${user.email} " +
                    "usuario: ${user.username} \n" +
                    "password: ${user.password} \n" +
                    "rol: ${user.rol} \n" +
                    "Indice: $index")
        }

        user = User()
        user.id = 3
        user.password = "contra"
        User.updateUser(baseContext, user)
        user = User.readUser(baseContext, user.id)!!
        Log.e("Bitzero", "email: ${user?.email} \n" +
                "usuario: ${user?.username} \n" +
                "password: ${user?.password} \n" +
                "rol: ${user?.rol} \n")

        User.deleteUser(baseContext, 3)
        Log.e("Bitzero", "No : ${User.getCount(baseContext)}")
        User.deleteAllUser(baseContext)
        Log.e("Bitzero", "No : ${User.getCount(baseContext)}")
    }
}