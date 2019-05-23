package com.desarollo.luisvillalobos.gardenkit.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity;
import com.desarollo.luisvillalobos.gardenkit.Controller.DatabaseAccess;
import com.desarollo.luisvillalobos.gardenkit.Model.DeviceJ;
import com.desarollo.luisvillalobos.gardenkit.R;

public class FormDeviceJ extends AppCompatActivity {

    private EditText inName, inDescription, inDevice, inApiKey;
    private Button btnAction;
    private FloatingActionButton btnHome;

    private Context context;
    private DatabaseAccess databaseAccess;
    public static final String PREFS_NAME = "SGKLog";
    private int fk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_device);
        context = getBaseContext();
        fk = Integer.parseInt(getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString("_id", null));

        SetUpActivity.hiderActionBar(this);
        SetUpActivity.hideStatusBar(this);
        SetUpActivity.hideSoftKeyboard(this);

        inName = (EditText) findViewById(R.id.in_name);
        inDescription = (EditText) findViewById(R.id.in_description);
        inDevice = (EditText) findViewById(R.id.in_device);
        inApiKey = (EditText) findViewById(R.id.in_apikey);
        btnAction = (Button) findViewById(R.id.btn_action);
        btnHome = (FloatingActionButton) findViewById(R.id.btn_home);

        btnAction.setOnClickListener(new btnAddDeviceClick());
        btnHome.setOnClickListener(new btnHomeClick());
    }

    class btnAddDeviceClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent returnIntent = getIntent();
            if (inDescription.getText().toString().trim().length() != 0 && inApiKey.getText().toString().trim().length() != 0 && inDevice.getText().toString().trim().length() != 0 && inName.getText().toString().trim().length() != 0) {
                DeviceJ device = new DeviceJ(inDescription.getText().toString().trim(), inApiKey.getText().toString().trim(), inDevice.getText().toString().trim(), inName.getText().toString().trim(), fk);
                returnIntent.putExtra("object", device);
                returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                returnIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                //overridePendingTransition( 0, 0);
                Toast.makeText(context, "No se rellenaron los campos correctamentre", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class btnHomeClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent returnIntent = getIntent();
            if (inDescription.getText().toString().trim().length() != 0 && inApiKey.getText().toString().trim().length() != 0 && inDevice.getText().toString().trim().length() != 0 && inName.getText().toString().trim().length() != 0) {
                DeviceJ device = new DeviceJ(inDescription.getText().toString(), inApiKey.getText().toString(), inDevice.getText().toString(), inName.getText().toString(), fk);
                returnIntent.putExtra("object", device);
                returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                returnIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                returnIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                setResult(Activity.RESULT_CANCELED, returnIntent);

                finishFromChild(getParent());
                //finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Intent intent = new Intent(context, ListDevicesJ.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            //startActivity(intent);
            Intent returnIntent = getIntent();
            returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            returnIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            setResult(Activity.RESULT_CANCELED, returnIntent);
            //finishFromChild(getParent());
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
