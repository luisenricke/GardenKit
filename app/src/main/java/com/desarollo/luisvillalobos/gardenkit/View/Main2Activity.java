package com.desarollo.luisvillalobos.gardenkit.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity;
import com.desarollo.luisvillalobos.gardenkit.Controller.DatabaseAccess;
import com.desarollo.luisvillalobos.gardenkit.Model.Device;
import com.desarollo.luisvillalobos.gardenkit.R;

public class Main2Activity extends AppCompatActivity {

    private ImageView imgLogo;
    private ImageButton imgBtnBack;
    private EditText txtDescription;
    private EditText txtApiKey;
    private EditText txtDevice;
    private EditText txtUser;
    private Button btnAdd;
    private Context context;
    private DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        context = getBaseContext();
        SetUpActivity.hiderActionBar(this);
        SetUpActivity.hideStatusBar(this);
        SetUpActivity.hideSoftKeyboard(this);

        imgLogo = (ImageView) findViewById(R.id.logo);
        imgBtnBack = (ImageButton) findViewById(R.id.btnBack);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        txtApiKey = (EditText) findViewById(R.id.txtApiKey);
        txtDevice = (EditText) findViewById(R.id.txtDevice);
        txtUser = (EditText) findViewById(R.id.txtUser);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = getIntent();


                if (txtDescription.getText().toString().trim().length() != 0 && txtApiKey.getText().toString().trim().length() != 0 && txtDevice.getText().toString().trim().length() != 0 && txtUser.getText().toString().trim().length() != 0) {
                    Device device = new Device(txtDescription.getText().toString(), txtApiKey.getText().toString(), txtDevice.getText().toString(), txtUser.getText().toString());
                    returnIntent.putExtra("object", device);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(context, "No se rellenaron los campos correctamentre", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = getIntent();

                if (txtDescription.getText().toString().trim().length() != 0 && txtApiKey.getText().toString().trim().length() != 0 && txtDevice.getText().toString().trim().length() != 0 && txtUser.getText().toString().trim().length() != 0) {
                    Device device = new Device(txtDescription.getText().toString(), txtApiKey.getText().toString(), txtDevice.getText().toString(), txtUser.getText().toString());
                    returnIntent.putExtra("object", device);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }
            }
        });

    }
}
