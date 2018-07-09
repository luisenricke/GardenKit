package com.desarollo.luisvillalobos.gardenkit.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.luisvillalobos.gardenkit.Controller.DatabaseAccess;
import com.desarollo.luisvillalobos.gardenkit.Controller.DateOperations;
import com.desarollo.luisvillalobos.gardenkit.Controller.HTTPGetRequest;
import com.desarollo.luisvillalobos.gardenkit.Controller.SetUpActivity;
import com.desarollo.luisvillalobos.gardenkit.Model.Device;
import com.desarollo.luisvillalobos.gardenkit.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Dispisitivo 1: test_prueba@spikedev.spikedev;
 * ApiKey 1: cef8f456d2ec6bebd28021dc8b1bbcfc0330ad558a0c0b2e1b4b19f8bb514d51
 * <p>
 * Dispositivo 2: Humedad_PH@mariohlh.mariohlh
 * ApiKey: fddc93fc422beea85c5104f2d4342a48a3a007ac4e6e7a71ca85abacdf9baf95
 */

public class Main3Activity extends AppCompatActivity {

    protected ImageButton imgBtnBack;
    protected Spinner spnUpdateTime;
    protected LinearLayout graphPh;
    protected LinearLayout graphWet1;
    protected LinearLayout graphWet2;
    protected LinearLayout graphWet3;

    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        context = getBaseContext();

        //Configuración de Activity
        SetUpActivity.hiderActionBar(this);
        SetUpActivity.hideStatusBar(this);
        SetUpActivity.hideSoftKeyboard(this);

        //Instanciando los Views
        imgBtnBack = (ImageButton) findViewById(R.id.btnBack);
        spnUpdateTime = (Spinner) findViewById(R.id.spnUpdateTime);
        graphPh = (LinearLayout) findViewById(R.id.graphPh);
        graphWet1 = (LinearLayout) findViewById(R.id.graphWet1);
        graphWet2 = (LinearLayout) findViewById(R.id.graphWet2);
        graphWet3 = (LinearLayout) findViewById(R.id.graphWet3);

        //Configurando el spinner
        String[] itemChoose = {"15 minutos", "30 minutos", "45 minutos", "2 horas", "5 horas", "12 horas", "24 horas", "3 días", "7 días",/* "14 días", "30 días","3 meses", "14 días", "4 mes",*/ "Selecciona un periodo"};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemChoose) {
            @Override
            public int getCount() {
                // don't display last item. It is used as hint.
                int count = super.getCount();
                return count > 0 ? count - 1 : count;
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView lbl = (TextView) view;
                lbl.setBackgroundColor(Color.WHITE);
                lbl.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                lbl.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                lbl.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                lbl.setGravity(Gravity.CENTER);
                return view;
            }

        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUpdateTime.setAdapter(adapter);
        spnUpdateTime.setSelection(adapter.getCount());
        spnUpdateTime.setOnItemSelectedListener(new UpdateTimeSpn());

        //Configurando las views
        imgBtnBack.setOnClickListener(new BackImgBtnClick());
    }

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
                                //Log.v("Tabla", table[i][j] + " i=" + i + " j=" + j);
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
                        double[] arrayPh = new double[table.length];
                        for (int i = 0; i < table.length; i++) {
                            arrayWet1[i] = Integer.parseInt(table[i][1]);
                            arrayWet2[i] = Integer.parseInt(table[i][2]);
                            arrayWet3[i] = Integer.parseInt(table[i][3]);
                            arrayPh[i] = Double.parseDouble(table[i][4]);
                        }

                        graphWet1.removeAllViews();
                        graphWet2.removeAllViews();
                        graphWet3.removeAllViews();
                        graphPh.removeAllViews();

                        BigDecimal bd[] = new BigDecimal[table.length];
                        for (int i = 0; i < table.length; i++) {
                            bd[i] = new BigDecimal(arrayDate[i]);
                        }

                        XYSeries expenseSeriesWet1 = new XYSeries("Humedad 1");
                        XYSeries expenseSeriesWet2 = new XYSeries("Humedad 2");
                        XYSeries expenseSeriesWet3 = new XYSeries("Humedad 3");
                        XYSeries expenseSeriesPh = new XYSeries("PH");

                        for (int i = 0; i < bd.length; i++) {
                            expenseSeriesWet1.add(bd[i].doubleValue(), arrayWet1[i]);
                            expenseSeriesWet2.add(bd[i].doubleValue(), arrayWet2[i]);
                            expenseSeriesWet3.add(bd[i].doubleValue(), arrayWet3[i]);
                            expenseSeriesPh.add(bd[i].doubleValue(), arrayPh[i]);
                        }

                        XYMultipleSeriesDataset xyMultipleSeriesDatasetWet1 = new XYMultipleSeriesDataset();
                        xyMultipleSeriesDatasetWet1.addSeries(expenseSeriesWet1);
                        XYMultipleSeriesDataset xyMultipleSeriesDatasetWet2 = new XYMultipleSeriesDataset();
                        xyMultipleSeriesDatasetWet2.addSeries(expenseSeriesWet2);
                        XYMultipleSeriesDataset xyMultipleSeriesDatasetWet3 = new XYMultipleSeriesDataset();
                        xyMultipleSeriesDatasetWet3.addSeries(expenseSeriesWet3);
                        XYMultipleSeriesDataset xyMultipleSeriesDatasetPh = new XYMultipleSeriesDataset();
                        xyMultipleSeriesDatasetPh.addSeries(expenseSeriesPh);

                        XYSeriesRenderer renderer = new XYSeriesRenderer();
                        renderer.setColor(Color.RED);
                        renderer.setPointStyle(PointStyle.CIRCLE);
                        renderer.setFillPoints(true);
                        renderer.setLineWidth(3);
                        renderer.setDisplayChartValues(true);

                        XYMultipleSeriesRenderer multiRendererWet1 = new XYMultipleSeriesRenderer();
                        multiRendererWet1.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                        multiRendererWet1.setYLabelsColor(0, Color.RED);
                        multiRendererWet1.setLabelsColor(Color.BLACK);
                        multiRendererWet1.setXLabelsColor(Color.RED);
                        multiRendererWet1.setXLabels(0);
                        multiRendererWet1.setChartTitle("Grafica de Humedad 1");
                        multiRendererWet1.setXTitle("Tiempo");
                        multiRendererWet1.setYTitle("°C");
                        multiRendererWet1.setShowGrid(true); // we show the grid
                        multiRendererWet1.setShowGrid(true); // we show the grid
                        multiRendererWet1.addSeriesRenderer(renderer);
                        multiRendererWet1.setZoomButtonsVisible(true);

                        XYMultipleSeriesRenderer multiRendererWet2 = new XYMultipleSeriesRenderer();
                        multiRendererWet2.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                        multiRendererWet2.setYLabelsColor(0, Color.RED);
                        multiRendererWet2.setLabelsColor(Color.BLACK);
                        multiRendererWet2.setXLabelsColor(Color.RED);
                        multiRendererWet2.setXLabels(0);
                        multiRendererWet2.setChartTitle("Grafica de Humedad 2");
                        multiRendererWet2.setXTitle("Tiempo");
                        multiRendererWet2.setYTitle("°C");
                        multiRendererWet2.setShowGrid(true); // we show the grid
                        //multiRendererWet2.setPanEnabled(false, false);
                        //multiRendererWet2.setZoomEnabled(false, false);
                        multiRendererWet2.setShowGrid(true); // we show the grid
                        multiRendererWet2.addSeriesRenderer(renderer);
                        multiRendererWet2.setZoomButtonsVisible(true);

                        XYMultipleSeriesRenderer multiRendererWet3 = new XYMultipleSeriesRenderer();
                        multiRendererWet3.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                        multiRendererWet3.setYLabelsColor(0, Color.RED);
                        multiRendererWet3.setLabelsColor(Color.BLACK);
                        multiRendererWet3.setXLabelsColor(Color.RED);
                        multiRendererWet3.setXLabels(0);
                        multiRendererWet3.setChartTitle("Grafica de Humedad 3");
                        multiRendererWet3.setXTitle("Tiempo");
                        multiRendererWet3.setYTitle("°C");
                        multiRendererWet3.setShowGrid(true); // we show the grid
                        //multiRendererWet3.setPanEnabled(false, false);
                        //multiRendererWet3.setZoomEnabled(false, false);
                        multiRendererWet3.setShowGrid(true); // we show the grid
                        multiRendererWet3.addSeriesRenderer(renderer);
                        multiRendererWet3.setZoomButtonsVisible(true);

                        XYMultipleSeriesRenderer multiRendererPh = new XYMultipleSeriesRenderer();
                        multiRendererPh.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                        multiRendererPh.setYLabelsColor(0, Color.RED);
                        multiRendererPh.setLabelsColor(Color.BLACK);
                        multiRendererPh.setXLabelsColor(Color.RED);
                        multiRendererPh.setXLabels(0);
                        multiRendererPh.setChartTitle("Grafica de PH");
                        multiRendererPh.setXTitle("Tiempo");
                        multiRendererPh.setYTitle("% de PH");
                        multiRendererPh.setShowGrid(true); // we show the grid
                        //multiRendererPh.setPanEnabled(false, false);
                        //multiRendererPh.setZoomEnabled(false, false);
                        multiRendererPh.setShowGrid(true); // we show the grid
                        multiRendererPh.addSeriesRenderer(renderer);
                        multiRendererPh.setZoomButtonsVisible(true);

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

                        for (int i = 0; i < number - 1; i++) {
                            multiRendererWet1.addXTextLabel(tempDouble.get(i).doubleValue(), tempString.get(i));
                            multiRendererWet2.addXTextLabel(tempDouble.get(i).doubleValue(), tempString.get(i));
                            multiRendererWet3.addXTextLabel(tempDouble.get(i).doubleValue(), tempString.get(i));
                            multiRendererPh.addXTextLabel(tempDouble.get(i).doubleValue(), tempString.get(i));
                        }

                        GraphicalView chartWet1 = ChartFactory.getLineChartView(getBaseContext(), xyMultipleSeriesDatasetWet1, multiRendererWet1);
                        graphWet1.addView(chartWet1, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 500));
                        GraphicalView chartWet2 = ChartFactory.getLineChartView(getBaseContext(), xyMultipleSeriesDatasetWet2, multiRendererWet2);
                        graphWet2.addView(chartWet2, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 500));
                        GraphicalView chartWet3 = ChartFactory.getLineChartView(getBaseContext(), xyMultipleSeriesDatasetWet3, multiRendererWet3);
                        graphWet3.addView(chartWet3, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 500));
                        GraphicalView chartTemp = ChartFactory.getLineChartView(getBaseContext(), xyMultipleSeriesDatasetPh, multiRendererPh);
                        graphPh.addView(chartTemp, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 500));

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
