package com.desarollo.luisvillalobos.gardenkit.Model

import android.content.ContentValues
import android.content.Context
import com.desarollo.luisvillalobos.gardenkit.Controller.DBHelper
import net.sqlcipher.Cursor
import net.sqlcipher.DatabaseUtils
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteException

class User {
    var id: Int = 0
    var email: String? = null
    var username: String? = null
    var password: String? = null
    var rol: Int = 0

    constructor()

    constructor(id: Int, email: String?, username: String?, password: String?, rol: Int) {
        this.id = id
        this.email = email
        this.username = username
        this.password = password
        this.rol = rol
    }

    constructor(email: String?, username: String?, password: String?, rol: Int) {
        this.email = email
        this.username = username
        this.password = password
        this.rol = rol
    }

    companion object {
        const val TABLE_NAME = "User"

        const val COLUMN_ID = "_id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_USERNAME = "username"
        /**
         * @Field: Rol
         * 1 -> SuperAdministrador
         * 2 -> Administrador
         * 3 -> Normal
         */
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_ROL = "rol"

        const val CREATE_TABLE = ("CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                COLUMN_EMAIL + " VARCHAR(90) NOT NULL," +
                COLUMN_USERNAME + " VARCHAR(35) NOT NULL UNIQUE, " +
                COLUMN_PASSWORD + " VARCHAR(75) NOT NULL, " +
                COLUMN_ROL + " INTEGER NOT NULL" +
                ")"
                )

        fun fillTable(context: Context) {
            DBHelper.openDB(context)
            //Inizialize table
            createUser(context,
                    User("superadmin@superadmin.com",
                            "superadmin",
                            "superadmin",
                            1))
            createUser(context,
                    User("admin@admin.com",
                            "admin",
                            "admin",
                            2))
            createUser(context,
                    User("test@test.com",
                            "test",
                            "test",
                            3))
            DBHelper.closeDB()
        }

        fun createUser(context: Context, user: User) {
            //Check and prepare values
            var newRow = ContentValues()
            newRow.put(COLUMN_EMAIL, user.email)
            newRow.put(COLUMN_USERNAME, user.username)
            newRow.put(COLUMN_PASSWORD, user.password)
            newRow.put(COLUMN_ROL, user.rol)

            //Execute SQL statement
            DBHelper.openDB(context)
            try {
                DBHelper.database!!.insertWithOnConflict(TABLE_NAME,
                        null,
                        newRow,
                        SQLiteDatabase.CONFLICT_ROLLBACK)
            } catch (e: SQLiteException) {
                e.printStackTrace()
                //Toast.makeText(context, "El usuario ya ha sido registrado", Toast.LENGTH_LONG).show()
            } finally {
                DBHelper.closeDB()
            }
        }

        fun readUser(context: Context, id: Int): User? {
            var user = User()
            var cursor: Cursor? = null
            DBHelper.openDB(context)
            try {
                cursor = DBHelper.database!!.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ? ", arrayOf(id.toString()))
                cursor?.moveToFirst()
                user.id = cursor.getInt(0)
                user.email = cursor.getString(1)
                user.username = cursor.getString(2)
                user.password = cursor.getString(3)
                user.rol = cursor.getInt(4)
            } catch (e: Exception) {
                e.printStackTrace()
                //Toast.makeText(context, "El usuario con el $id no se encuentra registrado", Toast.LENGTH_LONG).show()
            } finally {
                cursor?.close()
                DBHelper.closeDB()
            }
            return user
        }

        fun readUser(context: Context, user: User): String? {
            var id: String? = null
            var cursor: Cursor? = null
            DBHelper.openDB(context)
            try {
                if (user.username != null && user.password != null)
                    cursor = DBHelper.database!!.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?", arrayOf(user.username, user.password))
                if (user.username != null)
                    cursor = DBHelper.database!!.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_USERNAME = ? ", arrayOf(user.username))
                cursor?.moveToFirst()
                id = cursor?.getString(0)
            } catch (e: Exception) {
                e.printStackTrace()
                //Toast.makeText(context, "El usuario $username no se encuentra registrado", Toast.LENGTH_LONG).show()
            } finally {
                cursor?.close()
                DBHelper.closeDB()
            }
            return id
        }

        fun readUsersWithPermission(context: Context, rol: Int): List<User>? {
            var userList = ArrayList<User>()
            var cursor: Cursor? = null
            DBHelper.openDB(context)
            try {
                cursor = DBHelper.database!!.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ROL >= ? ", arrayOf(rol.toString()))
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    var aux = User()
                    aux.id = cursor.getInt(0)
                    aux.email = cursor.getString(1)
                    aux.username = cursor.getString(2)
                    aux.password = cursor.getString(3)
                    aux.rol = cursor.getInt(4)
                    userList.add(aux)
                    cursor.moveToNext()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                //Toast.makeText(context, "No hay ningun usuario registrado", Toast.LENGTH_LONG).show()
            } finally {
                cursor?.close()
                DBHelper.closeDB()
            }
            return userList
        }

        // TODO: Validar si hay los mismo campos
        fun updateUser(context: Context, user: User?): Boolean {
            val userCheck: User? = readUser(context, user!!.id)
            var values = ContentValues()
            DBHelper.openDB(context)
            if (user.id == 0 && userCheck != null)
                return false
            if (user.email != null)
                values.put(COLUMN_EMAIL, user.email)
            if (user.rol != 0)
                values.put(COLUMN_ROL, user.rol)
            if (user.username != null)
                values.put(COLUMN_USERNAME, user.username)
            if (user.password != null)
                values.put(COLUMN_PASSWORD, user.password)
            try {
                return DBHelper.database!!.updateWithOnConflict(TABLE_NAME, values, "$COLUMN_ID = ? ", arrayOf(user.id.toString()), SQLiteDatabase.CONFLICT_ROLLBACK) > 0
            } catch (e: SQLiteException) {
                e.printStackTrace()
            } finally {
                DBHelper.closeDB()
            }
            return false
        }

        fun deleteUser(context: Context, id: Int): Boolean {
            DBHelper.openDB(context)
            try {
                return DBHelper.database!!.delete(User.TABLE_NAME, "$COLUMN_ID = ? ", arrayOf(id.toString())) > 0
            } catch (e: SQLiteException) {
                e.printStackTrace()
            } finally {
                DBHelper.closeDB()
            }
            return false
        }

        fun deleteAllUser(context: Context): Boolean {
            DBHelper.openDB(context)
            try {
                return DBHelper.database!!.delete(User.TABLE_NAME, "$COLUMN_ID > ? ", arrayOf("0")) > 0
            } catch (e: SQLiteException) {
                e.printStackTrace()
            } finally {
                DBHelper.closeDB()
            }
            return false
        }

        fun getCount(context: Context): Long {
            DBHelper.openDB(context)
            var count: Long = 0
            try {
                count = DatabaseUtils.queryNumEntries(DBHelper.database!!, TABLE_NAME)
            } catch (e: KotlinNullPointerException) {
                e.printStackTrace()
            } finally {
                DBHelper.closeDB()
            }
            return count
        }
    }
}