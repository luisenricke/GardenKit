package com.desarollo.luisvillalobos.gardenkit.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.desarollo.luisvillalobos.gardenkit.Controller.DatabaseAccess;
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity;
import com.desarollo.luisvillalobos.gardenkit.R;

public class Login extends AppCompatActivity {

    private EditText inName, inPassword;
    private Button btnAction, btnLogin, btnSignUp;
    private Resources resources;
    private Context context;
    private DatabaseAccess databaseAccess;
    private boolean actionSelectedOption; //true if select login and false if select signup
    public static final String PREFS_NAME = "SGKLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        resources = getResources();
        context = getApplicationContext();//context = getBaseContext();
        actionSelectedOption = true;

        SetUpActivity.hiderActionBar(this);
        SetUpActivity.hideStatusBar(this);
        SetUpActivity.hideSoftKeyboard(this);
        SetUpActivity.setWindowPortrait(this);

        inName = (EditText) findViewById(R.id.in_name);
        inPassword = (EditText) findViewById(R.id.in_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnAction = (Button) findViewById(R.id.btn_action);

        btnLogin.setOnClickListener(new loginBtnClick());
        btnSignUp.setOnClickListener(new signUpnBtnClick());
        btnAction.setOnClickListener(new actionBtnClick());

        /*SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("logged", false);
        editor.remove("_id");
        editor.commit();*/
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (settings.getBoolean("logged", true)) {
            Intent intent = new Intent(context, ListDevices.class);
            Log.d("Prueba", "paso");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
    }

    class actionBtnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (actionSelectedOption) {
                if (inName.getText().toString().trim().length() != 0 &&
                        inPassword.getText().toString().trim().length() != 0) {

                    databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    String _id = databaseAccess.getUser(inName.getText().toString().trim(), inPassword.getText().toString().trim());
                    databaseAccess.close();

                    if (_id != null) {
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("logged", true);
                        editor.putString("_id", _id);
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "Ha iniciado correctamente sesión", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, ListDevices.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Los datos son incorrectos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Falta ingresar el usuario y/o contraseña", Toast.LENGTH_LONG).show();
                }

            } else {
                if (inName.getText().toString().trim().length() > 3 &&
                        inPassword.getText().toString().trim().length() > 3) {

                    databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    databaseAccess.setUser(inName.getText().toString(), inPassword.getText().toString());
                    Toast.makeText(context, "Se ha registrado con exito, codigo:" + databaseAccess.getUser(inName.getText().toString(), inPassword.getText().toString()), Toast.LENGTH_LONG).show();
                    databaseAccess.close();

                    btnLogin.performClick();
                } else if (inName.getText().toString().trim().length() == 0 &&
                        inPassword.getText().toString().trim().length() == 0) {
                    Toast.makeText(context, "No ha ingresado el usuario y/o contraseña", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "El usuario y/o la contraseña son muy cortos", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    class loginBtnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (!actionSelectedOption) {
                inName.setText("");
                inPassword.setText("");
                btnLogin.setTextColor(resources.getColor(R.color.colorPrimary));
                btnSignUp.setTextColor(resources.getColor(R.color.black));
                btnAction.setText("Iniciar");
                actionSelectedOption = true;
            }
        }
    }

    class signUpnBtnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (actionSelectedOption) {
                inName.setText("");
                inPassword.setText("");
                btnSignUp.setTextColor(resources.getColor(R.color.colorPrimary));
                btnLogin.setTextColor(resources.getColor(R.color.black));
                btnAction.setText("Registrar");
                actionSelectedOption = false;
            }
        }
    }
}
