package com.desarollo.luisvillalobos.gardenkit.View;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.desarollo.luisvillalobos.gardenkit.Controller.DatabaseAccess;
import com.desarollo.luisvillalobos.gardenkit.Controller.DateOperations;
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity;
import com.desarollo.luisvillalobos.gardenkit.Model.Data;
import com.desarollo.luisvillalobos.gardenkit.Model.Device;
import com.desarollo.luisvillalobos.gardenkit.R;

import org.achartengine.chart.LineChart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Dispisitivo 1: test_prueba@spikedev.spikedev
 * ApiKey 1: cef8f456d2ec6bebd28021dc8b1bbcfc0330ad558a0c0b2e1b4b19f8bb514d51
 * <p>
 * Dispositivo 2: Humedad_PH@mariohlh.mariohlh
 * ApiKey: fddc93fc422beea85c5104f2d4342a48a3a007ac4e6e7a71ca85abacdf9baf95
 */

public class Main3Activity extends AppCompatActivity {

    protected ImageButton imgBtnBack;

    protected LinearLayout graphPh;
    protected LinearLayout graphWet;

    protected EditText txtChooseDate;
    protected EditText txtNowDate;

    protected Context context;

    private static final String JSON_URL = "http://api.carriots.com/streams/?";
    private static final String DEVICE = "device=";
    private static final String AT_FROM = "&at_from="; // sub second
    private static final String AT_TO = "&at_to="; // add second
    private static final String SORT = "&sort=at";
    private static final String ORDER = "&order=-1";// -1 HighToLow  +1 LowToHigh
    private static final String API = "carriots.apikey";


    protected Calendar myCalendarFrom;
    protected Calendar myCalendarTo;
    protected String dateFormat;
    protected DatePickerDialog.OnDateSetListener date;
    protected SimpleDateFormat sdf;

    public List<Data> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        context = this;

        //Configuración de Activity
        SetUpActivity.hiderActionBar(this);
        SetUpActivity.hideStatusBar(this);
        SetUpActivity.hideSoftKeyboard(this);

        //Instanciando los Views
        imgBtnBack = (ImageButton) findViewById(R.id.btnBack);
        txtChooseDate = (EditText) findViewById(R.id.txtChooseDate);
        txtNowDate = (EditText) findViewById(R.id.txtNowDate);

        graphPh = (LinearLayout) findViewById(R.id.graphPh);
        graphWet = (LinearLayout) findViewById(R.id.graphWet);

        //Configuración de la fecha
        myCalendarFrom = Calendar.getInstance();
        myCalendarFrom.setTimeZone(TimeZone.getTimeZone("GMT"));
        myCalendarTo = Calendar.getInstance();
        myCalendarTo.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateFormat = "dd-MM-yyyy";
        sdf = new SimpleDateFormat(dateFormat);
        String dateString = sdf.format(myCalendarTo.getTime());
        txtNowDate.setText(dateString);

        // set calendar date and update editDate
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendarFrom.set(Calendar.YEAR, year);
                myCalendarFrom.set(Calendar.MONTH, monthOfYear);
                myCalendarFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };

        //Configurando las views
        imgBtnBack.setOnClickListener(new BackImgBtnClick());
        txtChooseDate.setOnClickListener(new ChooseDateTxt());
    }

    class ChooseDateTxt implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            DatePickerDialog dpd = new DatePickerDialog(context, date,
                    myCalendarFrom.get(Calendar.YEAR), myCalendarFrom.get(Calendar.MONTH),
                    myCalendarFrom.get(Calendar.DAY_OF_MONTH));
            Calendar c = Calendar.getInstance();
            dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
            dpd.show();
        }
    }


    private void updateDate() {
        txtChooseDate.setText(sdf.format(myCalendarFrom.getTime()));

        Date fromDate = DateOperations.clearTime(new Date(myCalendarFrom.getTimeInMillis()));
        Date toDate = DateOperations.getEnd(new Date(myCalendarTo.getTimeInMillis()));
        /*Log.v("Test", "past " + fromDate.getTime() / 1000L + " Millies");
        Log.v("Test", "past " + fromDate.toString() + " Date");
        Log.v("Test", "past " + toDate.getTime() / 1000L + " Millies");
        Log.v("Test", "past " + toDate.toString() + " Date");
        */
        loadDataByDates(this, fromDate.getTime() / 1000L, toDate.getTime() / 1000L);
    }

    protected void loadDataByDates(final Context context, long from, long to) {

        final Date fromDate = new Date(from * 1000L);
        final Date toDate = new Date(to * 1000L);
        /*
        Log.v("Test", "next " + fromDate.getTime() / 1000L + " Millies");
        Log.v("Test", "next " + fromDate.toString() + " Date");
        Log.v("Test", "next " + toDate.getTime() / 1000L + " Millies");
        Log.v("Test", "next " + toDate.toString() + " Date");
        */
        dataList = new ArrayList<>();

        //Agarro el dispositivo de la base de datos
        Bundle bundle = getIntent().getExtras();
        String _id = bundle.getString("_id");
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        Device obj = databaseAccess.getDevice(_id);
        databaseAccess.close();

        //Preparo los datos del listview seleccionado
        String device = obj.getDevice();
        final String apiKey = obj.getApiKey();


        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                JSON_URL + DEVICE + "test_prueba@spikedev.spikedev" /*device*/
                        + AT_FROM + from + AT_TO + to + SORT + ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray results = json.getJSONArray("result");

                            for (int i = 0; i < results.length(); i++) {
                                JSONObject aux = results.getJSONObject(i);
                                JSONObject data = aux.getJSONObject("data");
                                long at = aux.getLong("at");

                                Data dataJSON = new Data(new Date(aux.getLong("at") * 1000L),
                                        data.getInt("H1"),
                                        data.getInt("H2"),
                                        data.getInt("H3"),
                                        data.getInt("H4"),
                                        data.getInt("H5"),
                                        data.getDouble("PH"));

                                dataList.add(dataJSON);
                            }

                            for (Data i : dataList) {
                                Log.v("Sirve", String.valueOf(i.getWet1()));
                            }

                            //Grafica
                            com.github.mikephil.charting.charts.LineChart x = new com.github.mikephil.charting.charts.LineChart(context);
                            graphWet.removeAllViews();


                            //Generar espacios en X de la tabla
                            if ((DateOperations.getDay(toDate) - DateOperations.getDay(fromDate) <= 5)) {
                                //Generar por 24 horas los registros 24*Dias
                            } else if ((DateOperations.getMonth(toDate) - DateOperations.getMonth(fromDate) < 2)) {
                                //Generar por dias
                            } else if ((DateOperations.getYear(toDate) - DateOperations.getYear(fromDate) < 2)) {
                                //Generar por meses
                            } else if ((DateOperations.getYear(toDate) - DateOperations.getYear(fromDate) >= 2)) {
                                //Generar por años
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(API, "cef8f456d2ec6bebd28021dc8b1bbcfc0330ad558a0c0b2e1b4b19f8bb514d51"/*apiKey*/);
                return headers;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    class BackImgBtnClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
