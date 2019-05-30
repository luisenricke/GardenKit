package com.desarollo.luisvillalobos.gardenkit.Model

import android.content.ContentValues
import android.os.Parcel
import android.os.Parcelable
import com.desarollo.luisvillalobos.gardenkit.Controller.DBHelper
import net.sqlcipher.Cursor
import net.sqlcipher.DatabaseUtils
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteException

data class Device(var name: String?, var device_request: String?, var apikey_request: String?, var description: String?, var idUser: Int) : Parcelable {

    var id: Int = 0

    constructor(parcel: Parcel) : this(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt())

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(name)
        dest?.writeString(device_request)
        dest?.writeString(apikey_request)
        dest?.writeString(description)
        dest?.writeInt(idUser)
    }

    override fun describeContents(): Int {
        return 0
    }

    constructor() : this(null, null, null, null, 0)

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<Device> {
            override fun createFromParcel(parcel: Parcel): Device {
                return Device(parcel)
            }

            override fun newArray(size: Int): Array<Device?> {
                return arrayOfNulls(size)
            }
        }

        const val TABLE_NAME = "Device"

        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DEVICE_REQUEST = "device_request"
        const val COLUMN_APIKEY_REQUEST = "apikey_request"
        const val COLUMN_DESCRIPTION = "descripton"
        const val COLUMN_IDUSER = "idUSer"

        const val CREATE_TABLE = ("CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " VARCHAR(50) NOT NULL, " +
                COLUMN_DEVICE_REQUEST + " VARCHAR(100) NOT NULL, " +
                COLUMN_APIKEY_REQUEST + " VARCHAR(100) NOT NULL, " +
                COLUMN_DESCRIPTION + " VARCHAR(100) NOT NULL, " +
                COLUMN_IDUSER + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_IDUSER + ") REFERENCES " + User.TABLE_NAME + "(" + User.COLUMN_ID + ")" +
                ")"
                )

        fun fillTable() {
            //Initialize table
            createDevice(
                    Device("Dispositivo 1",
                            "device@dev_bitbot_test.dev_bitbot_test",
                            "d8a08f2ed52ec23463402769c3b0ccb6a8e4c6fa4bb6ea77f320cdbf553a2521",
                            "DEV",
                            3))
            createDevice(
                    Device("Dispositivo 2",
                            "device@dev_bitbot_test.dev_bitbot_test",
                            "d8a08f2ed52ec23463402769c3b0ccb6a8e4c6fa4bb6ea77f320cdbf553a2521",
                            "TEST",
                            2))
        }

        fun createDevice(device: Device): Boolean {
            //Check and prepare values
            var newRow = ContentValues()
            newRow.put(COLUMN_NAME, device.name)
            newRow.put(COLUMN_DEVICE_REQUEST, device.device_request)
            newRow.put(COLUMN_APIKEY_REQUEST, device.apikey_request)
            newRow.put(COLUMN_DESCRIPTION, device.description)
            newRow.put(COLUMN_IDUSER, device.idUser)

            //Execute SQL statement
            try {
                return DBHelper.database!!.insertWithOnConflict(TABLE_NAME,
                        null,
                        newRow,
                        SQLiteDatabase.CONFLICT_ROLLBACK) != 1L
            } catch (e: SQLiteException) {
                e.printStackTrace()
            }
            return false
        }

        fun readDevice(id: Int): Device? {
            var device = Device()
            var cursor: Cursor? = null
            try {
                cursor = DBHelper.database!!.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ? ", arrayOf(id.toString()))
                cursor?.moveToFirst()
                device.id = cursor.getInt(0)
                device.name = cursor.getString(1)
                device.device_request = cursor.getString(2)
                device.apikey_request = cursor.getString(3)
                device.description = cursor.getString(4)
                device.idUser = cursor.getInt(5)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
            return device
        }

        fun readDevice(device: Device): String? {
            var id: String? = null
            var cursor: Cursor? = null
            try {
                cursor = DBHelper.database!!.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME = ? AND " +
                        "$COLUMN_DEVICE_REQUEST = ? AND " +
                        "$COLUMN_APIKEY_REQUEST = ? AND " +
                        "$COLUMN_DESCRIPTION = ? AND " +
                        "$COLUMN_IDUSER = ? ", arrayOf(device.name, device.device_request, device.apikey_request, device.description, device.idUser))
                cursor?.moveToFirst()
                id = cursor?.getString(0)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
            return id
        }

        fun readDevices(): List<Device>? {
            var deviceList = ArrayList<Device>()
            var cursor: Cursor? = null
            try {
                cursor = DBHelper.database!!.rawQuery("SELECT * FROM $TABLE_NAME ", null)
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    var aux = Device()
                    aux.id = cursor.getInt(0)
                    aux.name = cursor.getString(1)
                    aux.device_request = cursor.getString(2)
                    aux.apikey_request = cursor.getString(3)
                    aux.description = cursor.getString(4)
                    aux.idUser = cursor.getInt(5)
                    deviceList.add(aux)
                    cursor.moveToNext()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
            return deviceList
        }

        fun readDevicesWithUser(fk_user: Int): ArrayList<Device>? {
            var deviceList = ArrayList<Device>()
            var cursor: Cursor? = null
            try {
                cursor = DBHelper.database!!.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_IDUSER = ? ", arrayOf(fk_user.toString()))
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    var aux = Device()
                    aux.id = cursor.getInt(0)
                    aux.name = cursor.getString(1)
                    aux.device_request = cursor.getString(2)
                    aux.apikey_request = cursor.getString(3)
                    aux.description = cursor.getString(4)
                    aux.idUser = cursor.getInt(5)
                    deviceList.add(aux)
                    cursor.moveToNext()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
            return deviceList
        }

        fun readDevicesWithUserCursor(fk_user: Int): Cursor? {
            var cursor: Cursor? = null
            try {
                cursor = DBHelper.database!!.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_IDUSER = ? ", arrayOf(fk_user.toString()))
                cursor.moveToFirst()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return cursor
        }

        fun updateDevice(device: Device?): Boolean {
            val deviceCheck: Device? = readDevice(device!!.id)
            var values = ContentValues()
            if (device.id == 0 && deviceCheck != null)
                return false
            if (device.name != null)
                values.put(COLUMN_NAME, device.name)
            if (device.device_request != null)
                values.put(COLUMN_DEVICE_REQUEST, device.device_request)
            if (device.apikey_request != null)
                values.put(COLUMN_APIKEY_REQUEST, device.apikey_request)
            if (device.description != null)
                values.put(COLUMN_DESCRIPTION, device.description)
            if (device.idUser != 0)
                values.put(COLUMN_IDUSER, device.idUser)
            try {
                return DBHelper.database!!.updateWithOnConflict(TABLE_NAME, values, "$COLUMN_ID = ? ", arrayOf(device.id.toString()), SQLiteDatabase.CONFLICT_ROLLBACK) > 0
            } catch (e: SQLiteException) {
                e.printStackTrace()
            } finally {
            }
            return false
        }

        fun deleteDevice(id: Int): Boolean {
            try {
                return DBHelper.database!!.delete(TABLE_NAME, "$COLUMN_ID = ? ", arrayOf(id.toString())) > 0
            } catch (e: SQLiteException) {
                e.printStackTrace()
            } finally {
            }
            return false
        }

        fun deleteAllDevices(): Boolean {
            try {
                return DBHelper.database!!.delete(TABLE_NAME, "$COLUMN_ID > ? ", arrayOf("0")) > 0
            } catch (e: SQLiteException) {
                e.printStackTrace()
            } finally {
            }
            return false
        }

        fun getCount(): Long {
            var count: Long = 0
            try {
                count = DatabaseUtils.queryNumEntries(DBHelper.database!!, TABLE_NAME)
            } catch (e: KotlinNullPointerException) {
                e.printStackTrace()
            } finally {
            }
            return count
        }
    }
}