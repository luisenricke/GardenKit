package com.desarollo.luisvillalobos.gardenkit.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.luisvillalobos.gardenkit.Controller.DeviceCursorAdapter;
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity;
import com.desarollo.luisvillalobos.gardenkit.Controller.DatabaseAccess;
import com.desarollo.luisvillalobos.gardenkit.Model.Device;
import com.desarollo.luisvillalobos.gardenkit.R;


public class ListDevices extends AppCompatActivity {

    //private ImageButton imgBtnAdd;
    private FloatingActionButton imgBtnAdd;
    private ListView lvDevice;

    private Context context;
    private DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_devices);
        context = getBaseContext();

        //Configuración de Activity
        SetUpActivity.hiderActionBar(this);
        SetUpActivity.hideStatusBar(this);
        SetUpActivity.hideSoftKeyboard(this);

        //Instanciando los Views
        imgBtnAdd = findViewById(R.id.btnAdd);
        lvDevice = findViewById(R.id.lvDevice);

        //Configurando el ListView
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        Cursor cursor = databaseAccess.getDevices();

        cursor.moveToFirst();
        do {
            Log.d("Dispositivos", cursor.getString(cursor.getColumnIndexOrThrow("_id")));
        } while (cursor.moveToNext());


        DeviceCursorAdapter adapter = new DeviceCursorAdapter(context, cursor);
        lvDevice.setAdapter(adapter);


        lvDevice.setOnItemClickListener(new lvDeviceClick());
        lvDevice.setOnItemLongClickListener(new lvDeviceLongClick());


        //Configurando el ImageButton
        imgBtnAdd.setOnClickListener(new AddImgBtn());
    }

    class lvDeviceLongClick implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView lblDescription = view.findViewById(R.id.lblDescription);
            TextView lblApikey = view.findViewById(R.id.lblApiKey);
            TextView lblDevice = view.findViewById(R.id.lblDevice);
            TextView lblUser = view.findViewById(R.id.lblUser);

            String _id = databaseAccess.getDevice(lblDescription.getText().toString(), lblApikey.getText().toString(), lblDevice.getText().toString(), lblUser.getText().toString());
            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();

            if (databaseAccess.deleteDevice(_id)) {

                Toast.makeText(context, "Se ha borrado satisfactoriamente", Toast.LENGTH_LONG).show();
                //recreate();
                finish();
                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);
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

            String _id = databaseAccess.getDevice(lblDescription.getText().toString(), lblApikey.getText().toString(), lblDevice.getText().toString(), lblUser.getText().toString());
            Intent intent = new Intent(context, graphs.class);
            intent.putExtra("_id", _id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }

    }

    class AddImgBtn implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, FormDevice.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Device device = data.getParcelableExtra("object");
                databaseAccess = DatabaseAccess.getInstance(this);
                databaseAccess.open();
                databaseAccess.setDevice(device.getDescripcion(), device.getApiKey(), device.getDevice(), device.getUser());

                Cursor cursor = databaseAccess.getDevices();
                DeviceCursorAdapter adapter = new DeviceCursorAdapter(context, cursor);
                lvDevice.setAdapter(adapter);
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
