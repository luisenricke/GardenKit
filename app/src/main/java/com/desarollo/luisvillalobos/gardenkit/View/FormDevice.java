package com.desarollo.luisvillalobos.gardenkit.View;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity;
import com.desarollo.luisvillalobos.gardenkit.Controller.DatabaseAccess;
import com.desarollo.luisvillalobos.gardenkit.R;

public class FormDevice extends AppCompatActivity {

    /*
    private ImageButton imgBtnBack;
    private EditText txtDescription;
    private EditText txtApiKey;
    private EditText txtDevice;
    private EditText txtUser;
    private Button btnAdd;
*/

    private EditText x;

    private Context context;
    private DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_device);
        context = getBaseContext();

        //Configuración de Activity
        SetUpActivity.hiderActionBar(this);
        SetUpActivity.hideStatusBar(this);
        SetUpActivity.hideSoftKeyboard(this);

        x = (EditText) findViewById(R.id.in_apikey);


/*
        //Instanciando los Views
        imgBtnBack = (ImageButton) findViewById(R.id.btnBack);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        txtApiKey = (EditText) findViewById(R.id.txtApiKey);
        txtDevice = (EditText) findViewById(R.id.txtDevice);
        txtUser = (EditText) findViewById(R.id.txtUser);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        //Configurando las views
        btnAdd.setOnClickListener(new AddBtnClick());
        imgBtnBack.setOnClickListener(new BackImgBtnClick());
        */
    }
/*
    class AddBtnClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent returnIntent = getIntent();
            btnAdd.setEnabled(true);
            btnAdd.setClickable(true);
            if (txtDescription.getText().toString().trim().length() != 0 && txtApiKey.getText().toString().trim().length() != 0 && txtDevice.getText().toString().trim().length() != 0 && txtUser.getText().toString().trim().length() != 0) {
                Device device = new Device(txtDescription.getText().toString().trim(), txtApiKey.getText().toString().trim(), txtDevice.getText().toString().trim(), txtUser.getText().toString().trim());
                returnIntent.putExtra("object", device);
                returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                returnIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                //overridePendingTransition( 0, 0);
                Toast.makeText(context, "No se rellenaron los campos correctamentre", Toast.LENGTH_SHORT).show();
            }
            btnAdd.setEnabled(true);
            btnAdd.setClickable(true);
        }
    }

    class BackImgBtnClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent returnIntent = getIntent();
            if (txtDescription.getText().toString().trim().length() != 0 && txtApiKey.getText().toString().trim().length() != 0 && txtDevice.getText().toString().trim().length() != 0 && txtUser.getText().toString().trim().length() != 0) {
                Device device = new Device(txtDescription.getText().toString(), txtApiKey.getText().toString(), txtDevice.getText().toString(), txtUser.getText().toString());
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
            //Intent intent = new Intent(context, ListDevices.class);
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
    */
}
