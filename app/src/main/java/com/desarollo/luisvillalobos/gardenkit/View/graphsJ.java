package com.desarollo.luisvillalobos.gardenkit.View;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.desarollo.luisvillalobos.gardenkit.Controller.DateOperations;
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity;
import com.desarollo.luisvillalobos.gardenkit.Model.DataWithStrings;
import com.desarollo.luisvillalobos.gardenkit.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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

public class graphsJ extends AppCompatActivity {

    private LineChart graphWet,graphPh/*,graphH20,graphV*/;
    private EditText inChooseDate_to,inChooseDate_from;
    private FloatingActionButton btnHome;

    private Context context;

    private static final String JSON_URL = "http://api.carriots.com/streams/?";
    private static final String DEVICE = "device=";
    private static final String AT_FROM = "&at_from="; // sub second
    private static final String AT_TO = "&at_to="; // add second
    private static final String SORT = "&sort=at";
    private static final String ORDER = "&order=1";// -1 HighToLow  +1 LowToHigh
    private static final String API = "carriots.apikey";

    protected Calendar myCalendarFrom;
    protected Calendar myCalendarTo;
    protected String dateFormat;
    protected DatePickerDialog.OnDateSetListener date;
    protected SimpleDateFormat sdf;

    //public List<Data> dataList;
    public List<DataWithStrings> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphs);
        context = this;

        SetUpActivity.hiderActionBar(this);
        SetUpActivity.hideStatusBar(this);
        SetUpActivity.hideSoftKeyboard(this);

        inChooseDate_to = (EditText) findViewById(R.id.in_choosedate_to);
        inChooseDate_from = (EditText) findViewById(R.id.in_choosedate_from);
        btnHome = (FloatingActionButton) findViewById(R.id.btn_home);
        graphWet = (LineChart) findViewById(R.id.graphWet);
        graphPh = (LineChart) findViewById(R.id.graphPh);
        //graphH20 = (LineChart) findViewById(R.id.graphH20);
        //graphV = (LineChart) findViewById(R.id.graphV);

        graphWet.setNoDataTextColor(Color.RED);
        graphWet.setNoDataText("No hay datos para graficar");
        graphWet.setDoubleTapToZoomEnabled(false);
        graphWet.invalidate();

        graphPh.setNoDataTextColor(Color.RED);
        graphPh.setNoDataText("No hay datos para graficar");
        graphPh.setDoubleTapToZoomEnabled(false);
        graphPh.invalidate();

        /*graphH20.setNoDataTextColor(Color.RED);
        graphH20.setNoDataText("No hay datos para graficar");
        graphH20.setDoubleTapToZoomEnabled(false);
        graphH20.invalidate();

        graphV.setNoDataTextColor(Color.RED);
        graphV.setNoDataText("No hay datos para graficar");
        graphV.setDoubleTapToZoomEnabled(false);
        graphV.invalidate();*/

        //Configuración de la fecha
        myCalendarFrom = Calendar.getInstance();
        myCalendarFrom.setTimeZone(TimeZone.getTimeZone("GMT"));
        myCalendarTo = Calendar.getInstance();
        myCalendarTo.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateFormat = "dd-MM-yyyy";
        sdf = new SimpleDateFormat(dateFormat);
        String dateString = sdf.format(myCalendarTo.getTime());
        inChooseDate_from.setText(dateString);

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
        btnHome.setOnClickListener(new btnHomeClick());
        inChooseDate_to.setOnClickListener(new ChooseDateTxt());
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
        inChooseDate_to.setText(sdf.format(myCalendarFrom.getTime()));

        Date fromDate = DateOperations.clearTime(new Date(myCalendarFrom.getTimeInMillis()));
        Date toDate = DateOperations.getEnd(new Date(myCalendarTo.getTimeInMillis()));
        loadDataByDates(this, fromDate.getTime() / 1000L, toDate.getTime() / 1000L);
    }

    protected void loadDataByDates(final Context context, long from, long to) {

        final Date fromDate = new Date(from * 1000L);
        final Date toDate = new Date(to * 1000L);

        dataList = new ArrayList<>();

        //Agarro el dispositivo de la base de datos
        Bundle bundle = getIntent().getExtras();
        String _id = bundle.getString("_id");
        /*DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        DeviceJ obj = databaseAccess.getDevice(_id);
        databaseAccess.close();
*/
        //Preparo los datos del listview seleccionado
        String device = "";//obj.getDevice();
        final String apiKey ="";// obj.getApiKey();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                JSON_URL + DEVICE + device
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
                                /*
                                Data dataJSON = new Data(new Date(aux.getLong("at") * 1000L),
                                        data.getInt("H1"),
                                        data.getInt("H2"),
                                        data.getInt("H3"),
                                        data.getInt("H4"),
                                        data.getInt("H5"),
                                        data.getDouble("PH"),
                                        data.getDouble("H2O"),
                                        data.getInt("V"));*/
                                DataWithStrings dataJSON = new DataWithStrings(new Date(aux.getLong("at") * 1000L),
                                        data.getString("Humedad1: "),
                                        data.getString("Humedad2: "),
                                        data.getString("Humedad3: "),
                                        data.getString("Humedad4: "),
                                        data.getString("Humedad5: "),
                                        data.getDouble("PH: "));

                                dataList.add(dataJSON);
                            }

                            for (DataWithStrings i : dataList) {
                                Log.v("Sirve", String.valueOf(i.getWet1()));
                            }

                            List<Entry> entriesWet1 = new ArrayList<Entry>();
                            List<Entry> entriesWet2 = new ArrayList<Entry>();
                            List<Entry> entriesWet3 = new ArrayList<Entry>();
                            List<Entry> entriesWet4 = new ArrayList<Entry>();
                            List<Entry> entriesWet5 = new ArrayList<Entry>();

                            List<Entry> entriesPh = new ArrayList<Entry>();
                            /*List<Entry> entriesH20 = new ArrayList<Entry>();
                            List<Entry> entriesV = new ArrayList<Entry>();*/

                            int i = 0;
                            /*for (DataWithStrings data : dataList) {// turn your data into Entry objects
                                entriesWet1.add(new Entry(i, data.getWet1()));
                                entriesWet2.add(new Entry(i, data.getWet2()));
                                entriesWet3.add(new Entry(i, data.getWet3()));
                                entriesWet4.add(new Entry(i, data.getWet4()));
                                entriesWet5.add(new Entry(i, data.getWet5()));
                                entriesPh.add(new Entry(i, (float) data.getPh()));
                                *//*entriesH20.add(new Entry(i, (float) data.getH20()));
                                entriesV.add(new Entry(i, data.getV()));*//*
                                i++;
                            }*/
                            for (DataWithStrings data : dataList) {// turn your data into Entry objects
                                entriesWet1.add(new Entry(i, Integer.parseInt(data.getWet1())));
                                entriesWet2.add(new Entry(i, Integer.parseInt(data.getWet2())));
                                entriesWet3.add(new Entry(i, Integer.parseInt(data.getWet3())));
                                entriesWet4.add(new Entry(i, Integer.parseInt(data.getWet4())));
                                entriesWet5.add(new Entry(i, Integer.parseInt(data.getWet5())));
                                entriesPh.add(new Entry(i, (float) data.getPh()));
                                i++;
                            }

                            LineDataSet dataSetWet1 = new LineDataSet(entriesWet1, "H1");
                            dataSetWet1.setColor(Color.YELLOW);
                            dataSetWet1.setValueTextColor(Color.BLACK);
                            dataSetWet1.setLineWidth(2);
                            dataSetWet1.setCircleColor(Color.YELLOW);
                            dataSetWet1.setCircleRadius(4f);
                            dataSetWet1.setDrawCircleHole(false);
                            //dataSetWet1.setCircleHoleRadius(4f);
                            dataSetWet1.setHighLightColor(Color.YELLOW);
                            dataSetWet1.setValueTextSize(8);

                            LineDataSet dataSetWet2 = new LineDataSet(entriesWet2, "H2");
                            dataSetWet2.setColor(Color.BLUE);
                            dataSetWet2.setValueTextColor(Color.BLACK);
                            dataSetWet2.setLineWidth(2);
                            dataSetWet2.setCircleColor(Color.BLUE);
                            dataSetWet2.setCircleRadius(4f);
                            dataSetWet2.setDrawCircleHole(false);
                            dataSetWet2.setHighLightColor(Color.BLUE);
                            dataSetWet2.setValueTextSize(8);

                            LineDataSet dataSetWet3 = new LineDataSet(entriesWet3, "H3");
                            dataSetWet3.setColor(Color.CYAN);
                            dataSetWet3.setValueTextColor(Color.BLACK);
                            dataSetWet3.setLineWidth(2);
                            dataSetWet3.setCircleColor(Color.CYAN);
                            dataSetWet3.setCircleRadius(4f);
                            dataSetWet3.setDrawCircleHole(false);
                            dataSetWet3.setHighLightColor(Color.CYAN);
                            dataSetWet3.setValueTextSize(8);

                            LineDataSet dataSetWet4 = new LineDataSet(entriesWet4, "H4");
                            dataSetWet4.setColor(Color.GREEN);
                            dataSetWet4.setValueTextColor(Color.BLACK);
                            dataSetWet4.setLineWidth(2);
                            dataSetWet4.setCircleColor(Color.GREEN);
                            dataSetWet4.setCircleRadius(4f);
                            dataSetWet4.setDrawCircleHole(false);
                            dataSetWet4.setHighLightColor(Color.GREEN);
                            dataSetWet4.setValueTextSize(8);

                            LineDataSet dataSetWet5 = new LineDataSet(entriesWet5, "H5");
                            dataSetWet5.setColor(Color.MAGENTA);
                            dataSetWet5.setValueTextColor(Color.BLACK);
                            dataSetWet5.setLineWidth(2);
                            dataSetWet5.setCircleColor(Color.MAGENTA);
                            dataSetWet5.setCircleRadius(4f);
                            dataSetWet5.setDrawCircleHole(false);
                            dataSetWet5.setHighLightColor(Color.MAGENTA);
                            dataSetWet5.setValueTextSize(8);

                            LineDataSet dataSetPh = new LineDataSet(entriesPh, "Ph");
                            dataSetPh.setColor(Color.CYAN);
                            dataSetPh.setValueTextColor(Color.BLACK);
                            dataSetPh.setLineWidth(2);
                            dataSetPh.setCircleColor(Color.CYAN);
                            dataSetPh.setCircleRadius(4f);
                            dataSetPh.setDrawCircleHole(false);
                            dataSetPh.setHighLightColor(Color.CYAN);
                            dataSetPh.setValueTextSize(8);

                            /*LineDataSet dataSetH20 = new LineDataSet(entriesH20, "Nivel de agua");
                            dataSetH20.setColor(Color.GREEN);
                            dataSetH20.setValueTextColor(Color.BLACK);
                            dataSetH20.setLineWidth(2);
                            dataSetH20.setCircleColor(Color.GREEN);
                            dataSetH20.setCircleRadius(4f);
                            dataSetH20.setDrawCircleHole(false);
                            dataSetH20.setHighLightColor(Color.GREEN);
                            dataSetH20.setValueTextSize(8);

                            LineDataSet dataSetV = new LineDataSet(entriesV, "Energía electrica");
                            dataSetV.setColor(Color.MAGENTA);
                            dataSetV.setValueTextColor(Color.BLACK);
                            dataSetV.setLineWidth(2);
                            dataSetV.setCircleColor(Color.MAGENTA);
                            dataSetV.setCircleRadius(4f);
                            dataSetV.setDrawCircleHole(false);
                            dataSetV.setHighLightColor(Color.MAGENTA);
                            dataSetV.setValueTextSize(8);*/

                            /*
                            //Customizar el eje de las X
                            final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr"};
                            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {
                                    return months[(int) value];
                                }
                            };
                            graphWet.getXAxis().setValueFormatter(formatter);
                            */

                            graphWet.getDescription().setText("Gráfica de Humedad");
                            graphWet.getDescription().setTextSize(10f);
                            graphWet.setData(new LineData(dataSetWet1, dataSetWet2, dataSetWet3, dataSetWet4, dataSetWet5));

                            graphPh.getDescription().setText("Gráfica de Ph");
                            graphPh.getDescription().setTextSize(10f);
                            graphPh.setData(new LineData(dataSetPh));

                           /* graphH20.getDescription().setText("Gráfica de Nivel de Agua");
                            graphH20.getDescription().setTextSize(10f);
                            graphH20.setData(new LineData(dataSetH20));

                            graphV.getDescription().setText("Gráfica de Energía Electrica");
                            graphV.getDescription().setTextSize(10f);
                            graphV.setData(new LineData(dataSetV));*/

                            //Custom Graphics
                            graphWet.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);// Set the xAxis position to bottom. Default is top
                            graphWet.getXAxis().setGranularity(1f);// minimum axis-step (interval) is 1
                            graphWet.getAxisRight().setEnabled(false);// Controlling right side of y axis
                            graphWet.getAxisLeft().setGranularity(0.1f);// Controlling left side of y axis

                            graphPh.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);// Set the xAxis position to bottom. Default is top
                            graphPh.getXAxis().setGranularity(1f);// minimum axis-step (interval) is 1
                            graphPh.getAxisRight().setEnabled(false);// Controlling right side of y axis
                            graphPh.getAxisLeft().setGranularity(0.1f);// Controlling left side of y axis

                            /*graphH20.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);// Set the xAxis position to bottom. Default is top
                            graphH20.getXAxis().setGranularity(1f);// minimum axis-step (interval) is 1
                            graphH20.getAxisRight().setEnabled(false);// Controlling right side of y axis
                            graphH20.getAxisLeft().setGranularity(0.1f);// Controlling left side of y axis

                            graphV.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);// Set the xAxis position to bottom. Default is top
                            graphV.getXAxis().setGranularity(1f);// minimum axis-step (interval) is 1
                            graphV.getAxisRight().setEnabled(false);// Controlling right side of y axis
                            graphV.getAxisLeft().setGranularity(1f);// Controlling left side of y axis*/

                            //Bordes Opcional
                            graphWet.setBorderWidth(2f);
                            graphWet.setDrawBorders(true);

                            graphPh.setBorderWidth(2f);
                            graphPh.setDrawBorders(true);

                            /*graphH20.setBorderWidth(2f);
                            graphH20.setDrawBorders(true);

                            graphV.setBorderWidth(2f);
                            graphV.setDrawBorders(true);*/

                            //Limits
                            LimitLine upper_limit = new LimitLine(90, "Alto");
                            upper_limit.setLineWidth(1.5f);
                            upper_limit.enableDashedLine(15f, 15f, 0f);
                            upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                            upper_limit.setTextSize(8f);

                            LimitLine medius_limit = new LimitLine(60f, "Mediano");
                            medius_limit.setLineWidth(1.5f);
                            medius_limit.enableDashedLine(15f, 15f, 0f);
                            medius_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                            medius_limit.setTextSize(8f);

                            LimitLine lower_limit = new LimitLine(40f, "Bajo");
                            lower_limit.setLineWidth(1.5f);
                            lower_limit.enableDashedLine(15f, 15f, 0f);
                            lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                            lower_limit.setTextSize(8f);

                            graphWet.getAxisLeft().addLimitLine(upper_limit);
                            graphWet.getAxisLeft().addLimitLine(medius_limit);
                            graphWet.getAxisLeft().addLimitLine(lower_limit);
                            graphWet.getAxisLeft().setDrawLimitLinesBehindData(true);

                            //Animation
                            graphWet.animateX(2500, Easing.EaseOutSine);

                            graphPh.animateX(2500, Easing.EaseOutSine);
                            /*graphH20.animateX(2500, Easing.EasingOption.EaseOutSine);
                            graphV.animateX(2500, Easing.EasingOption.EaseOutSine);*/

                            graphWet.invalidate(); // refresh
                            graphPh.invalidate(); // refresh
                            /*graphH20.invalidate(); // refresh
                            graphV.invalidate(); // refresh*/


                            /*//Generar espacios en X de la tabla
                            if ((DateOperations.getDay(toDate) - DateOperations.getDay(fromDate) <= 5)) {
                                //Generar por 24 horas los registros 24*Dias
                            } else if ((DateOperations.getMonth(toDate) - DateOperations.getMonth(fromDate) < 2)) {
                                //Generar por dias
                            } else if ((DateOperations.getYear(toDate) - DateOperations.getYear(fromDate) < 2)) {
                                //Generar por meses
                            } else if ((DateOperations.getYear(toDate) - DateOperations.getYear(fromDate) >= 2)) {
                                //Generar por años
                            }
*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("No NetWork", error.getMessage());
                        Toast.makeText(context, "Sin conexión", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(API, apiKey);
                return headers;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    class btnHomeClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            /*Intent intent = new Intent(context, ListDevicesJ.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);*/
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /*Intent intent = new Intent(context, ListDevicesJ.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);*/
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
