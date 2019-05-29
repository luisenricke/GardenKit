package com.desarollo.luisvillalobos.gardenkit.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.luisvillalobos.gardenkit.Activity.Login;
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity;
import com.desarollo.luisvillalobos.gardenkit.Controller.DatabaseAccess;
import com.desarollo.luisvillalobos.gardenkit.Model.DeviceJ;
import com.desarollo.luisvillalobos.gardenkit.R;


public class ListDevicesJ extends AppCompatActivity {

    private ListView lvDevice;
    private FloatingActionButton btnLogOut, btnAdd, btnHome;

    private Context context;
    private DatabaseAccess databaseAccess;
    public static final String PREFS_NAME = "SGKLog";
    private int fk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_devices);
        context = getBaseContext();

        try {
            fk = Integer.parseInt(getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString("_id", null));
        } catch (NumberFormatException ex) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("logged", false);
            editor.remove("_id");
            editor.commit();

            Intent intent = new Intent(context, com.desarollo.luisvillalobos.gardenkit.Activity.Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }

        SetUpActivity.hiderActionBar(this);
        SetUpActivity.hideStatusBar(this);
        SetUpActivity.hideSoftKeyboard(this);

        lvDevice = (ListView) findViewById(R.id.lvDevice);
        btnLogOut = (FloatingActionButton) findViewById(R.id.btn_logout);
        btnAdd = (FloatingActionButton) findViewById(R.id.btn_add);
        btnHome = (FloatingActionButton) findViewById(R.id.btn_home);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        Cursor cursor = databaseAccess.getDevices(fk);
        cursor.moveToFirst();
        //DeviceCursorAdapterJ adapter = new DeviceCursorAdapterJ(context, cursor);
        //lvDevice.setAdapter(adapter);

        lvDevice.setOnItemClickListener(new lvDeviceClick());
        lvDevice.setOnItemLongClickListener(new lvDeviceLongClick());
        btnAdd.setOnClickListener(new btnAdd());
        btnLogOut.setOnClickListener(new btnLogOut());
    }

    class lvDeviceLongClick implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView lblDescription = view.findViewById(R.id.lblDescription);
            TextView lblApikey = view.findViewById(R.id.lblApiKey);
            TextView lblDevice = view.findViewById(R.id.lblDevice);
            TextView lblUser = view.findViewById(R.id.lblUser);

            String _id = databaseAccess.getDevice(lblDescription.getText().toString(), lblApikey.getText().toString(), lblDevice.getText().toString(), lblUser.getText().toString(), fk);
            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();

            if (databaseAccess.deleteDevice(_id)) {
                Toast.makeText(context, "Se ha borrado satisfactoriamente", Toast.LENGTH_LONG).show();
                //recreate();
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            } else {
                Toast.makeText(context, "Hubo problemas con el reqistro", Toast.LENGTH_LONG).show();
            }
            databaseAccess.close();
            return true;
        }
    }

    class lvDeviceClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView lblDescription = view.findViewById(R.id.lblDescription);
            TextView lblApikey = view.findViewById(R.id.lblApiKey);
            TextView lblDevice = view.findViewById(R.id.lblDevice);
            TextView lblUser = view.findViewById(R.id.lblUser);

            String _id = databaseAccess.getDevice(lblDescription.getText().toString(), lblApikey.getText().toString(), lblDevice.getText().toString(), lblUser.getText().toString(), fk);
            Intent intent = new Intent(context, graphsJ.class);
            intent.putExtra("_id", _id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
    }

    class btnAdd implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, FormDeviceJ.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivityForResult(intent, 1);
        }
    }

    class btnLogOut implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("logged", false);
            editor.remove("_id");
            editor.commit();
            Intent intent = new Intent(context, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                DeviceJ device = data.getParcelableExtra("object");
                databaseAccess = DatabaseAccess.getInstance(this);
                databaseAccess.open();
                databaseAccess.setDevice(device.getDescripcion(), device.getApiKey(), device.getDevice(), device.getUser(), fk);

                Cursor cursor = databaseAccess.getDevices(fk);
                //DeviceCursorAdapterJ adapter = new DeviceCursorAdapterJ(context, cursor);
                //lvDevice.setAdapter(adapter);
                databaseAccess.close();
                recreate();
                Toast.makeText(context, "Se ha agredado satisfactoriamente", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(context, "No se rellenaron los campos correctamente", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "No se rellenaron los campos correctamente", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        //Para no usar el boton hacia atras
        //super.onBackPressed();
        //overridePendingTransition( 0, 0);
        //System.exit(0);
        moveTaskToBack(true);
    }
}
