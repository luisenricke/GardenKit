package com.desarollo.luisvillalobos.gardenkit.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.desarollo.luisvillalobos.gardenkit.Model.Device;
import com.desarollo.luisvillalobos.gardenkit.Model.User;

public class DatabaseAccess {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;
    private Context context;

    private final String TABLE_DEVICE = "Dispositivo";
    private final String TDEVICE_DESCRIPTION = "description";
    private final String TDEVICE_APIKEY = "apiKey";
    private final String TDEVICE_DEVICE = "device";
    private final String TDEVICE_USER = "user";

    private final String TABLE_USER = "Usuario";
    private final String TU_USER = "username";
    private final String TU_PASSWORD = "password";

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {

        this.openHelper = new DatabaseOpenHelper(context);
        this.context = context;
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public Cursor getDevices() {
        Log.d("prueba", "");
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_DEVICE + ";", null);
        Log.d("prueba", cursor.toString());
        cursor.moveToFirst();
        return cursor;
    }

    public String getDevice(String description, String apikey, String device, String user) {
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM Dispositivo WHERE description = '" + description + "' AND apiKey = '" + apikey + "' AND device = '" + device + "' AND user = '" + user + "'", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                return (cursor.getString(cursor.getColumnIndexOrThrow("_id")));
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    public Device getDevice(String _id) {
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM Dispositivo WHERE _id = '" + _id + "'", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Device device = new Device();
                device.setDescripcion(cursor.getString(0));
                device.setApiKey(cursor.getString(1));
                device.setDevice(cursor.getString(2));
                device.setUser(cursor.getString(3));
                return device;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void setDevice(String description, String apikey, String device, String user) {

        /**
         *  New Query: INSERT INTO Dispositivo (description,apiKey,device,user,fk_id)
         * VALUES ("qwe","cef8f456d2ec6bebd28021dc8b1bbcfc0330ad558a0c0b2e1b4b19f8bb514d51","test_prueba@spikedev.spikedev","Jardin 2",1);
         */
        try {
            ContentValues newRow = new ContentValues();
            newRow.put("description", description);
            newRow.put("apiKey", apikey);
            newRow.put("device", device);
            newRow.put("user", user);
            database.insertWithOnConflict("Dispositivo", null, newRow, SQLiteDatabase.CONFLICT_ROLLBACK);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //---deletes a particular title---
    public boolean deleteDevice(String _id) {

        //database.rawQuery("DELETE FROM Dispositivo WHERE _id = '" + _id + "'", null);
        return database.delete("Dispositivo", "_id" + "=" + _id, null) > 0;
    }

    public void setUser(String username, String password) {
        try {
            ContentValues newRow = new ContentValues();
            newRow.put(TU_USER, username);
            newRow.put(TU_PASSWORD, password);
            database.insertWithOnConflict(TABLE_USER, null, newRow, SQLiteDatabase.CONFLICT_ROLLBACK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUser(String username, String password) {
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + TU_USER + " = '" + username + "' AND " + TU_PASSWORD + "= '" + password + "'", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                return (cursor.getString(cursor.getColumnIndexOrThrow("_id")));
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public User getUser(String _id) {
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE _id = '" + _id + "'", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                User user = new User();
                user.setUsername(cursor.getString(0));
                user.setPassword(cursor.getString(1));
                return user;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


/**
 * Read all quotes from the database.
 *
 * @return a List of quotes
 */
    /*
    public List<String> getQuotes() {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM quotes", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
*/
}
