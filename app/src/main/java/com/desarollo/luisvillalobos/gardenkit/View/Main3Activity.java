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

                        Date [] arrayDates = new Date[table.length];
                        for (int i = 0; i <table.length ; i++) {
                            arrayDates[i] = new Date(table[i][0]);
                        }

                        int [] arrayTemp = new int[table.length];
                        for (int i = 0; i < table.length; i++) {
                            arrayTemp[i] = Integer.parseInt(table[i][1]);
                        }

                        //--------------------------
                        String[] mMonth = new String[] {
                                "Jan", "Feb" , "Mar", "Apr", "May", "Jun",
                                "Jul", "Aug" , "Sep", "Oct", "Nov", "Dec"
                        };
                        int[] x_values = { 1,2,3,4,5,6,7,8 };
                        int[] y_values = { 1000,1500,1700,2000,2500,3000,3500,3600};
                        XYSeries expenseSeries = new XYSeries("Expense");
                        for(int i=0;i<x_values.length;i++){
                            expenseSeries.add(x_values[i], y_values[i]);
                        }
                        XYMultipleSeriesDataset xyMultipleSeriesDataset = new XYMultipleSeriesDataset();
                        xyMultipleSeriesDataset.addSeries(expenseSeries);

                        XYSeriesRenderer renderer = new XYSeriesRenderer();
                        renderer.setColor(Color.GREEN);
                        renderer.setPointStyle(PointStyle.CIRCLE);
                        renderer.setFillPoints(true);
                        renderer.setLineWidth(3);
                        renderer.setDisplayChartValues(true);

                        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
                        multiRenderer.setXLabels(0);
                        multiRenderer.setChartTitle("Expense Chart");
                        multiRenderer.setXTitle("Year 2016");
                        multiRenderer.setYTitle("Amount in Dollars");
                        multiRenderer.setZoomButtonsVisible(true);
                        for(int i=0;i<x_values.length;i++){
                            multiRenderer.addXTextLabel(i+1, mMonth[i]);
                        }

                        multiRenderer.addSeriesRenderer(renderer);
                        View chart = ChartFactory.getLineChartView(context, xyMultipleSeriesDataset, multiRenderer);
                        graphTemp.addView(chart);
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
