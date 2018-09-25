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
import com.desarollo.luisvillalobos.gardenkit.Controller.DateOperations;
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity;
import com.desarollo.luisvillalobos.gardenkit.Model.Data;
import com.desarollo.luisvillalobos.gardenkit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
        myCalendarTo = Calendar.getInstance();
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

    protected void loadDataByDates(final Context context, long from, long to) {
        //Log.v("Fechas Funcion", new Date(from).toString());
        //Log.v("Fechas Funcion", new Date(to).toString());
        //Log.v("Fechas Funcion", from+ " from");
        //Log.v("Fechas Funcion", to+ " to");
        dataList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                JSON_URL + DEVICE + "test_prueba@spikedev.spikedev"
                        + AT_FROM + 1537005600 + AT_TO + 1537704000 + SORT + ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray results = json.getJSONArray("result");

                            for (int i = 0; i < results.length(); i++) {
                                JSONObject aux = results.getJSONObject(i);
                                JSONObject data = aux.getJSONObject("data");

                                Data dataJSON = new Data(data.getInt("H1"),
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
                headers.put(API, "cef8f456d2ec6bebd28021dc8b1bbcfc0330ad558a0c0b2e1b4b19f8bb514d51");
                return headers;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    protected void loadAllData(final Context context) {
        dataList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                JSON_URL + DEVICE + "test_prueba@spikedev.spikedev",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray results = json.getJSONArray("result");

                            for (int i = 0; i < results.length(); i++) {
                                JSONObject aux = results.getJSONObject(i);
                                JSONObject data = aux.getJSONObject("data");

                                Data dataJSON = new Data(data.getInt("H1"),
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
                headers.put(API, "cef8f456d2ec6bebd28021dc8b1bbcfc0330ad558a0c0b2e1b4b19f8bb514d51");
                return headers;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
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

        try {
            Date from = sdf.parse(String.valueOf(txtChooseDate.getText()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Date from = sdf.parse(DateOperations.clearTime(myCalendarFrom.getTime()));
        Date to = DateOperations.getEnd(myCalendarTo.getTime());
        Log.v("Fechas Millies", myCalendarFrom.getTimeInMillis()+" Millies");
        Log.v("Fechas Millies", myCalendarFrom.getTime()+" Millies");
        Log.v("Fechas Millies", myCalendarTo.getTimeInMillis()+ " Millies");
        loadDataByDates(this, myCalendarFrom.getTimeInMillis(), to.getTime());
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

    /*
    class UpdateTimeSpn implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            if (position != spnUpdateTime.getCount()) {
                int number = -1;
                String postFix = "";

                //Agarro el dispositivo de la base de datos
                Bundle bundle = getIntent().getExtras();
                String _id = bundle.getString("_id");
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                Device obj = databaseAccess.getDevice(_id);
                databaseAccess.close();

                //Preparo los datos del listview seleccionado
                String myUrl = "https://api.carriots.com/streams/?device=";
                String device = obj.getDevice();
                String apiKey = obj.getApiKey();

                //Busca un numero u palabra, ignorando caracteres especiales <---Checar para ponerlo en libreria
                String item = adapterView.getItemAtPosition(position).toString();
                Scanner sca = new Scanner(item);
                sca.useDelimiter("[^\\p{Alnum},\\.-]");
                while (true) {
                    if (sca.hasNextInt())
                        number = sca.nextInt();
                    else if (sca.hasNext())
                        postFix = sca.next();
                    else
                        break;
                }

                //Preparo las fechas para graficar
                Date currentTime = Calendar.getInstance().getTime();
                Date pastDate = currentTime;
                if ((postFix.equals("minutos") || postFix.equals("hora")) && number != -1)
                    pastDate = DateOperations.subMinute(currentTime, number);
                else if ((postFix.equals("horas") || postFix.equals("hora")) && number != -1)
                    pastDate = DateOperations.subHour(currentTime, number);
                else if (postFix.equals("días") && number != -1)
                    pastDate = DateOperations.subDay(currentTime, number);
                else if ((postFix.equals("mes") || postFix.equals("meses") && number != -1))
                    pastDate = DateOperations.subMonth(currentTime, number);

                try {
                    String json = new HTTPGetRequest().execute(myUrl, device, apiKey, currentTime.toString(), pastDate.toString()).get();
                    if (!json.equals("Error") && !json.equals("")) {
                        String colum = ",";
                        String row = "#";

                        //Preparo los datos en columnas y filas, para asi obtener de las columnas los datos correspondientes de cada campo del json.
                        String[] rowTokens = json.split(row);
                        String[][] table = new String[rowTokens.length][rowTokens[0].split(colum).length];
                        for (int i = 0; i < rowTokens.length; i++) {
                            String[] columTokens = rowTokens[i].split(colum);
                            for (int j = 0; j < columTokens.length; j++) {
                                table[i][j] = columTokens[j];
                                Log.v("Tabla", table[i][j] + " i=" + i + " j=" + j);
                            }
                        }

                        //Formateo la fecha para poder utilizarlo en dado caso sea horas, dias o meses
                        String[] arrayDate = new String[table.length];
                        for (int i = 0; i < table.length; i++) {
                            Date tempDate = new Date(table[i][0]);
                            arrayDate[i] = String.format("%02d", DateOperations.getMonth(tempDate)) + "." + String.format("%02d", DateOperations.getDay(tempDate)) + String.format("%02d", DateOperations.getHour(tempDate)) + String.format("%02d", DateOperations.getMinute(tempDate)) + String.format("%02d", DateOperations.getSecond(tempDate));
                        }

                        //Aqui se crearan las cadenas por separadas obtenidas de la tabla del json
                        int[] arrayWet1 = new int[table.length];
                        int[] arrayWet2 = new int[table.length];
                        int[] arrayWet3 = new int[table.length];
                        int[] arrayWet4 = new int[table.length];
                        int[] arrayWet5 = new int[table.length];
                        double[] arrayPh = new double[table.length];
                        double[] arrayH2o = new double[table.length];
                        int[] arrayV = new int[table.length];

                        for (int i = 0; i < table.length; i++) {
                            arrayWet1[i] = Integer.parseInt(table[i][1]);
                            arrayWet2[i] = Integer.parseInt(table[i][2]);
                            arrayWet3[i] = Integer.parseInt(table[i][3]);
                            arrayWet4[i] = Integer.parseInt(table[i][4]);
                            arrayWet5[i] = Integer.parseInt(table[i][5]);
                            arrayPh[i] = Double.parseDouble(table[i][6]);
                            arrayH2o[i] = Double.parseDouble(table[i][7]);
                            arrayV[i] = Integer.parseInt(table[i][8]);
                        }

                        graphWet1.removeAllViews();


                        BigDecimal bd[] = new BigDecimal[table.length];
                        for (int i = 0; i < table.length; i++) {
                            bd[i] = new BigDecimal(arrayDate[i]);
                        }

                        XYSeries expenseSeriesWet1 = new XYSeries("Humedad 1");
                        XYSeries expenseSeriesWet2 = new XYSeries("Humedad 2");
                        XYSeries expenseSeriesWet3 = new XYSeries("Humedad 3");
                        XYSeries expenseSeriesWet4 = new XYSeries("Humedad 4");
                        XYSeries expenseSeriesWet5 = new XYSeries("Humedad 5");


                        for (int i = 0; i < bd.length; i++) {
                            expenseSeriesWet1.add(bd[i].doubleValue(), arrayWet1[i]);
                            expenseSeriesWet2.add(bd[i].doubleValue(), arrayWet2[i]);
                            expenseSeriesWet3.add(bd[i].doubleValue(), arrayWet3[i]);
                            expenseSeriesWet4.add(bd[i].doubleValue(), arrayWet4[i]);
                            expenseSeriesWet5.add(bd[i].doubleValue(), arrayWet5[i]);
                        }

                        XYMultipleSeriesDataset xyMultipleSeriesDatasetWet1 = new XYMultipleSeriesDataset();
                        xyMultipleSeriesDatasetWet1.addSeries(expenseSeriesWet1);
                        xyMultipleSeriesDatasetWet1.addSeries(expenseSeriesWet2);
                        xyMultipleSeriesDatasetWet1.addSeries(expenseSeriesWet3);
                        xyMultipleSeriesDatasetWet1.addSeries(expenseSeriesWet4);
                        xyMultipleSeriesDatasetWet1.addSeries(expenseSeriesWet5);

                        XYSeriesRenderer renderer1 = new XYSeriesRenderer();
                        renderer1.setColor(Color.RED);
                        renderer1.setPointStyle(PointStyle.CIRCLE);
                        renderer1.setFillPoints(true);
                        renderer1.setLineWidth(3);
                        renderer1.setDisplayChartValues(true);

                        XYSeriesRenderer renderer2 = new XYSeriesRenderer();
                        renderer2.setColor(Color.BLUE);
                        renderer2.setPointStyle(PointStyle.CIRCLE);
                        renderer2.setFillPoints(true);
                        renderer2.setLineWidth(3);
                        renderer2.setDisplayChartValues(true);

                        XYSeriesRenderer renderer3 = new XYSeriesRenderer();
                        renderer3.setColor(Color.GREEN);
                        renderer3.setPointStyle(PointStyle.CIRCLE);
                        renderer3.setFillPoints(true);
                        renderer3.setLineWidth(3);
                        renderer3.setDisplayChartValues(true);

                        XYSeriesRenderer renderer4 = new XYSeriesRenderer();
                        renderer4.setColor(Color.DKGRAY);
                        renderer4.setPointStyle(PointStyle.CIRCLE);
                        renderer4.setFillPoints(true);
                        renderer4.setLineWidth(3);
                        renderer4.setDisplayChartValues(true);

                        XYSeriesRenderer renderer5 = new XYSeriesRenderer();
                        renderer5.setColor(Color.MAGENTA);
                        renderer5.setPointStyle(PointStyle.CIRCLE);
                        renderer5.setFillPoints(true);
                        renderer5.setLineWidth(3);
                        renderer5.setDisplayChartValues(true);

                        XYMultipleSeriesRenderer multiRendererWet1 = new XYMultipleSeriesRenderer();
                        multiRendererWet1.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                        multiRendererWet1.setYLabelsColor(0, Color.RED);
                        multiRendererWet1.setLabelsColor(Color.BLACK);
                        multiRendererWet1.setXLabelsColor(Color.RED);
                        multiRendererWet1.setXLabels(0);
                        multiRendererWet1.setChartTitle("Grafica de Humedad");
                        multiRendererWet1.setXTitle("Tiempo");
                        multiRendererWet1.setYTitle("°C");
                        multiRendererWet1.setShowGrid(true); // we show the grid
                        multiRendererWet1.setShowGrid(true); // we show the grid
                        multiRendererWet1.addSeriesRenderer(renderer1);
                        multiRendererWet1.addSeriesRenderer(renderer2);
                        multiRendererWet1.addSeriesRenderer(renderer3);
                        multiRendererWet1.addSeriesRenderer(renderer4);
                        multiRendererWet1.addSeriesRenderer(renderer5);
                        multiRendererWet1.setZoomButtonsVisible(true);

                        ArrayList<String> tempString = new ArrayList<String>();
                        ArrayList<BigDecimal> tempDouble = new ArrayList<BigDecimal>();
                        Date dateIterator = Calendar.getInstance().getTime();

                        if (postFix.equals("minutos")) {
                            dateIterator = DateOperations.subMinute(dateIterator, number - 1);
                            for (int i = 0; i < number - 1; i++) {
                                tempString.add(DateOperations.getMinute(dateIterator) + "");
                                tempDouble.add(new BigDecimal(String.format("%02d", DateOperations.getMonth(dateIterator)) + "." + String.format("%02d", DateOperations.getDay(dateIterator)) + String.format("%02d", DateOperations.getHour(dateIterator)) + String.format("%02d", DateOperations.getMinute(dateIterator))));
                                dateIterator = DateOperations.addMinute(dateIterator, 1);
                            }
                        } else if (postFix.equals("horas")) {
                            dateIterator = DateOperations.subHour(dateIterator, number - 1);
                            for (int i = 0; i < number - 1; i++) {
                                tempString.add(DateOperations.getHour(dateIterator) + ":00");
                                tempDouble.add(new BigDecimal(String.format("%02d", DateOperations.getMonth(dateIterator)) + "." + String.format("%02d", DateOperations.getDay(dateIterator)) + String.format("%02d", DateOperations.getHour(dateIterator))));
                                dateIterator = DateOperations.addHour(dateIterator, 1);
                            }
                        } else if (postFix.equals("días")) {
                            dateIterator = DateOperations.subDay(dateIterator, number - 1);
                            for (int i = 0; i < number - 1; i++) {
                                tempString.add(DateOperations.getDay(dateIterator) + " día");
                                tempDouble.add(new BigDecimal(String.format("%02d", DateOperations.getMonth(dateIterator)) + "." + String.format("%02d", DateOperations.getDay(dateIterator))));
                                dateIterator = DateOperations.addDay(dateIterator, 1);
                            }
                        } else if (postFix.equals("mes")) {//Checar caso especial

                        } else if (postFix.equals("meses")) {
                            dateIterator = DateOperations.subMonth(dateIterator, number - 1);
                            for (int i = 0; i < number - 1; i++) {
                                tempString.add(DateOperations.getMonth(dateIterator) + " Mes");
                                tempDouble.add(new BigDecimal(String.format("%02d", DateOperations.getMonth(dateIterator))));
                                dateIterator = DateOperations.addMonth(dateIterator, 1);
                            }
                        }

                        for (int i = 0; i < number -1; i++) {
                            multiRendererWet1.addXTextLabel(i, tempString.get(i));

                        }

                        GraphicalView chartWet1 = ChartFactory.getLineChartView(getBaseContext(), xyMultipleSeriesDatasetWet1, multiRendererWet1);
                        graphWet1.addView(chartWet1, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 500));


                    } else {
                        if (json.equals("Error"))
                            Toast.makeText(context, "Hubo algun problema con la petición al servidor", Toast.LENGTH_SHORT).show();
                        if (json.equals(""))
                            Toast.makeText(context, "Hubo algun problema con los datos", Toast.LENGTH_SHORT).show();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                //Cuando se abre por primera vez
                //Toast.makeText(context, "Hubo algun error con el spinner", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //Checar que hacer con esto
        }

    }
     */
}
