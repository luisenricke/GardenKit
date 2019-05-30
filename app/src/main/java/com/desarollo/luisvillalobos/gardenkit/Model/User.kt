package com.desarollo.luisvillalobos.gardenkit.Model

import android.content.ContentValues
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

    constructor(username: String?, password: String?) {
        this.username = username
        this.password = password
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

        fun fillTable() {
            //Initialize table
            createUser(
                    User("superadmin@superadmin.com",
                            "superadmin",
                            "superadmin",
                            1))
            createUser(
                    User("admin@admin.com",
                            "admin",
                            "admin",
                            2))
            createUser(
                    User("test@test.com",
                            "test",
                            "test",
                            3))
        }

        fun createUser(user: User): Boolean {
            //Check and prepare values
            var newRow = ContentValues()
            newRow.put(COLUMN_EMAIL, user.email)
            newRow.put(COLUMN_USERNAME, user.username)
            newRow.put(COLUMN_PASSWORD, user.password)
            newRow.put(COLUMN_ROL, user.rol)

            //Execute SQL statement
            try {
                return DBHelper.database!!.insertWithOnConflict(TABLE_NAME,
                        null,
                        newRow,
                        SQLiteDatabase.CONFLICT_ROLLBACK) != -1L
            } catch (e: SQLiteException) {
                e.printStackTrace()
            }
            return false
        }

        fun readUser(id: Int): User? {
            var user = User()
            var cursor: Cursor? = null
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
            } finally {
                cursor?.close()
            }
            return user
        }

        fun readUser(user: User): String? {
            var id: String? = null
            var cursor: Cursor? = null
            try {
                if (user.username != null)
                    cursor = DBHelper.database!!.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_USERNAME = ? ", arrayOf(user.username))

                if (user.username != null && user.password != null)
                    cursor = DBHelper.database!!.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?", arrayOf(user.username, user.password))

                cursor?.moveToFirst()
                id = cursor?.getString(0)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
            return id
        }

        fun readUsersWithPermission(rol: Int): List<User>? {
            var userList = ArrayList<User>()
            var cursor: Cursor? = null
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
            } finally {
                cursor?.close()
            }
            return userList
        }

        fun updateUser(user: User?): Boolean {
            val userCheck: User? = readUser(user!!.id)
            var values = ContentValues()
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
            }
            return false
        }

        fun deleteUser(id: Int): Boolean {
            try {
                return DBHelper.database!!.delete(User.TABLE_NAME, "$COLUMN_ID = ? ", arrayOf(id.toString())) > 0
            } catch (e: SQLiteException) {
                e.printStackTrace()
            }
            return false
        }

        fun deleteAllUser(): Boolean {
            try {
                return DBHelper.database!!.delete(User.TABLE_NAME, "$COLUMN_ID > ? ", arrayOf("0")) > 0
            } catch (e: SQLiteException) {
                e.printStackTrace()
            }
            return false
        }

        fun getCount(): Long {
            var count: Long = 0
            try {
                count = DatabaseUtils.queryNumEntries(DBHelper.database!!, TABLE_NAME)
            } catch (e: KotlinNullPointerException) {
                e.printStackTrace()
            }
            return count
        }
    }
}