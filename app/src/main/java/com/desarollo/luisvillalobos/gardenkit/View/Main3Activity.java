package com.desarollo.luisvillalobos.gardenkit.View;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.desarollo.luisvillalobos.gardenkit.Controller.DateOperations;
import com.desarollo.luisvillalobos.gardenkit.Controller.HTTPGetRequest;
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity;
import com.desarollo.luisvillalobos.gardenkit.Controller.DatabaseAccess;
import com.desarollo.luisvillalobos.gardenkit.Model.Device;
import com.desarollo.luisvillalobos.gardenkit.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main3Activity extends AppCompatActivity {

    private ImageView imgLogo;
    private ImageButton imgBtnBack;
    private Spinner spnUpdateTime;
    private Context context;
    private String _id;

    private String TAG = MainActivity.class.getSimpleName();

    private String[][] table;

    private LinearLayout graphTemp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        context = getBaseContext();
        SetUpActivity.hiderActionBar(this);
        SetUpActivity.hideStatusBar(this);
        SetUpActivity.hideSoftKeyboard(this);

        imgLogo = (ImageView) findViewById(R.id.logo);
        imgBtnBack = (ImageButton) findViewById(R.id.btnBack);
        spnUpdateTime = (Spinner) findViewById(R.id.spnUpdateTime);
        graphTemp = (LinearLayout) findViewById(R.id.graphTemp);

        String[] itemChoose = {"12 horas", "24 horas", "7 días", "14 días", "1 mes"};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemChoose) {
/*
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView lbl = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    lbl.setTextColor(Color.GRAY);
                } else {
                    lbl.setTextColor(Color.BLACK);
                }
                return view;
            }*/
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUpdateTime.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        _id = bundle.getString("_id");

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        Device device = databaseAccess.getDevice(_id);
        databaseAccess.close();


        spnUpdateTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                int number = -1;
                String postFix = "";
                String item = adapterView.getItemAtPosition(position).toString();
                String myUrl = "https://api.carriots.com/streams/?device=";
                String d = "test_prueba@spikedev.spikedev";
                String a = "cef8f456d2ec6bebd28021dc8b1bbcfc0330ad558a0c0b2e1b4b19f8bb514d51";
                //String d = "Humedad_PH@mariohlh.mariohlh";
                //String a ="fddc93fc422beea85c5104f2d4342a48a3a007ac4e6e7a71ca85abacdf9baf95";
                Date currentTime = Calendar.getInstance().getTime();
                Date pastDate = currentTime;

                //Busca un numero u palabra, ignorando caracteres especiales <---Checar para ponerlo en libreria
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

                if ((postFix.equals("horas") || postFix.equals("hora")) && number != -1) {
                    pastDate = DateOperations.subHour(currentTime, number);
                } else if (postFix.equals("días") && number != -1) {
                    pastDate = DateOperations.subDay(currentTime, number);
                } else if ((postFix.equals("mes") || postFix.equals("meses") && number != -1)) {
                    pastDate = DateOperations.subMonth(currentTime, number);
                } else {
                    Toast.makeText(context, "Selección no valida", Toast.LENGTH_LONG).show();
                }

                try {
                    String json = new HTTPGetRequest().execute(myUrl, d, a, currentTime.toString(), pastDate.toString()).get();
                    if (!json.equals("Error") && !json.equals("")) {
                        String colum = ",";
                        String row = "#";

                        String[] rowTokens = json.split(row);
                        for (int i = 0; i < rowTokens.length; i++) {
                            Log.d("column", rowTokens[i]);
                        }

                        table = new String[rowTokens.length][rowTokens[0].split(colum).length];
                        for (int i = 0; i < rowTokens.length; i++) {
                            String[] columTokens = rowTokens[i].split(colum);
                            for (int j = 0; j < columTokens.length; j++) {
                                table[i][j] = columTokens[j];
                                Log.d("column", columTokens[j] + " i=" + i + "j=" + j);
                            }
                        }

                        /*
                        Date [] arrayDates = new Date[table.length];
                        for (int i = 0; i <table.length ; i++) {
                            arrayDates[i] = new Date(table[i][0]);
                            Log.d("fecha",arrayDates[i].getMonth()+"."+String.format("%02d", arrayDates[i].getDay())+String.format("%02d", arrayDates[i].getHours())+String.format("%02d", arrayDates[i].getMinutes())+String.format("%02d", arrayDates[i].getSeconds()));
                        }
                        */
                        //Date [] arrayDates = new Date[table.length];
                        /*double[] arrayDates = new double[table.length];
                        for (int i = 0; i < table.length; i++) {
                            Date tempDate = new Date(table[i][0]);
                            arrayDates[i] = Double.parseDouble(tempDate.getMonth() + 1 + "." + String.format("%02d", tempDate.getDay()) + String.format("%02d", tempDate.getHours()) + String.format("%02d", tempDate.getMinutes()) + String.format("%02d", tempDate.getSeconds()));
                            Log.d("fecha", String.format("%02d", tempDate.getMonth()) + "." + String.format("%02d", tempDate.getDay()) + String.format("%02d", tempDate.getHours()) + String.format("%02d", tempDate.getMinutes()) + String.format("%02d", tempDate.getSeconds()));
                        }
                        */

                        String [] arrayDate = new String[table.length];
                        for (int i = 0; i < table.length; i++) {
                            Date tempDate = new Date(table[i][0]);
                            arrayDate[i] = String.format("%02d", tempDate.getMonth())  + "." + String.format("%02d", tempDate.getDay()) + String.format("%02d", tempDate.getHours()) + String.format("%02d", tempDate.getMinutes()) + String.format("%02d", tempDate.getSeconds());
                            Log.d("fecha", String.format("%02d", tempDate.getMonth()) + "." + String.format("%02d", tempDate.getDay()) + String.format("%02d", tempDate.getHours()) + String.format("%02d", tempDate.getMinutes()) + String.format("%02d", tempDate.getSeconds()));
                        }

                        int[] arrayTemp = new int[table.length];
                        for (int i = 0; i < table.length; i++) {
                            arrayTemp[i] = Integer.parseInt(table[i][1]);
                            Log.d("temperatura", arrayTemp[i] + " ");
                        }

                        //--------------------------
                        graphTemp.removeAllViews();
                        String[] mMonth;
                        double arrayDates[] = new  double[table.length];

                        if (postFix.equals("horas")){
                            mMonth = new String[]{
                                    "00:00","01:00", "02:00", "03:00", "04:00", "05:00",
                                    "06:00", "07:00", "08:00", "09:00", "10:00",
                                    "11:00", "12:00",
                                    "13:00", "14:00", "15:00", "16:00", "17:00",
                                    "18:00", "19:00", "20:00", "21:00", "22:00",
                                    "23:00"
                            };


                            for (int i = 0; i < table.length; i++) {
                                String temp="";
                                //temp = (String.valueOf(arrayDates[i]).substring(2).equals("10") || String.valueOf(arrayDates[i]).substring(2).equals("11") || String.valueOf(arrayDates[i]).substring(2).equals("11")) ?"":"2";
                                arrayDates[i] = Double.parseDouble(arrayDate[i].substring(5,7)+"."+arrayDate[i].substring(7,9));
                                Log.d("fecha", arrayDates[i]+" x");
                            }

                        }else{
                            mMonth = new String[]{
                                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                            };
                            for (int i = 0; i < table.length; i++) {
                                arrayDates[i]= Double.parseDouble(arrayDate[i]);
                            }
                        }


                        //int[] x_values = {1, 2, 3, 4, 5, 6, 7};
                        double[] x_values = arrayDates;
                        //int[] y_values = {1000, 1500, 1700, 2000, 2500, 3000, 3500, 3600};
                        int[] y_values = arrayTemp;

                        XYSeries expenseSeries = new XYSeries("Temperatura");
                        for (int i = 0; i < x_values.length; i++) {
                            expenseSeries.add(x_values[i], y_values[i]);
                        }

                        XYMultipleSeriesDataset xyMultipleSeriesDataset = new XYMultipleSeriesDataset();
                        xyMultipleSeriesDataset.addSeries(expenseSeries);

                        XYSeriesRenderer renderer = new XYSeriesRenderer();
                        renderer.setColor(Color.RED);
                        renderer.setPointStyle(PointStyle.CIRCLE);
                        renderer.setFillPoints(true);
                        renderer.setLineWidth(3);
                        renderer.setDisplayChartValues(true);

                        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
                        multiRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                        multiRenderer.setYLabelsColor(0, Color.RED);
                        multiRenderer.setLabelsColor(Color.BLACK);
                        multiRenderer.setXLabelsColor(Color.RED);
                        multiRenderer.setXLabels(0);
                        multiRenderer.setChartTitle("Grafica de temperatura 1");
                        multiRenderer.setXTitle("Tiempo");
                        multiRenderer.setYTitle("Grados °C");
                        multiRenderer.setShowGrid(true); // we show the grid
                        //multiRenderer.setYAxisMax(3600);
                        //multiRenderer.setYAxisMin(0);


                        multiRenderer.setShowGrid(true); // we show the grid
                        for (int i = 0; i < x_values.length; i++) {
                            multiRenderer.addXTextLabel(i + 1, mMonth[i]);
                        }

                        multiRenderer.addSeriesRenderer(renderer);

                        GraphicalView chart = ChartFactory.getLineChartView(getBaseContext(), xyMultipleSeriesDataset, multiRenderer);

                        graphTemp.addView(chart, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 500));

                        //---------------------------

                    } else {
                        Log.d("resulta", " Error");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Checar que hacer con esto
            }
        });

        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
