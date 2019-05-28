package com.desarollo.luisvillalobos.gardenkit.Controller

import android.content.Context
import com.desarollo.luisvillalobos.gardenkit.BuildConfig
import com.desarollo.luisvillalobos.gardenkit.Model.Device
import com.desarollo.luisvillalobos.gardenkit.Model.PaswordReset
import com.desarollo.luisvillalobos.gardenkit.Model.User
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper

private const val DB_PASSWORD = BuildConfig.DB_PASSWORD
private const val DB_NAME = BuildConfig.DB_NAME
private const val DB_VERSION = 8

class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {

        @Volatile
        private var instance: DBHelper? = null
        var database: SQLiteDatabase? = null

        private fun getInstance(context: Context): DBHelper = instance ?: synchronized(this) {
            instance ?: DBHelper(context).also { instance = it }
        }

        fun openDB(context: Context) {
            database = DBHelper.getInstance(context).getWritableDatabase(DB_PASSWORD)
        }

        fun closeDB() {
            if (database != null) {
                database!!.close()
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(User.CREATE_TABLE)
        db?.execSQL(PaswordReset.CREATE_TABLE)
        db?.execSQL(Device.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + Device.TABLE_NAME)
        db?.execSQL("DROP TABLE IF EXISTS " + PaswordReset.TABLE_NAME)
        db?.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME)
        onCreate(db)
    }
}