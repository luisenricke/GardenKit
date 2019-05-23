package com.desarollo.luisvillalobos.gardenkit.Controller;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;


public class HTTPGetRequest extends AsyncTask<String, Void, String> {

    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    @Override
    protected String doInBackground(String... strings) {
        String url_request = strings[0];
        String device_request = strings[1];
        String apiKey_request = strings[2];
        String now_request = strings[3];
        String past_resquest = strings[4];

        HttpURLConnection conn = null;
        InputStream responseBody = null;

        try {
            //Create a URL object holding our url
            URL url = new URL(url_request + device_request);
            //Create a connection
            conn = (HttpURLConnection) url.openConnection();
            //Set methods and timeouts
            conn.setRequestMethod(REQUEST_METHOD);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestProperty("carriots.apikey", apiKey_request);
            //Connect to our url
            conn.connect();
            if (conn.getResponseCode() == 200) {
                //Create a new InputStream
                responseBody = new BufferedInputStream(conn.getInputStream());
                //Create my string with contains my JSON and my object JSON
                String json = convertStreamToString(responseBody);
                JSONObject jsonObject = new JSONObject(json);
                JSONArray results = jsonObject.getJSONArray("result");
                //Create my StringBuilder for concatenate
                StringBuilder result = new StringBuilder();

                for (int i = 0; i < results.length(); i++) {
                    //Handles the part of the JSON result
                    JSONObject aux = results.getJSONObject(i);
                    //Extra Info
                    String id = aux.getString("_id");
                    String protocol = aux.getString("protocol");
                    String device = aux.getString("device");
                    String t = aux.getString("_t");
                    String developer = aux.getString("id_developer");
                    long created = aux.getLong("created_at");
                    String owner = aux.getString("owner");
                    //Important info
                    long at = aux.getLong("at");
                    Calendar calendar = Calendar.getInstance();
                    //A Unix timestamp is a number of seconds since 01-01-1970 00:00:00 GMT. Java measures time in milliseconds since 01-01-1970 00:00:00 GMT. You need to multiply the Unix timestamp by 1000:
                    calendar.setTimeInMillis(at * 1000L);
                    Date temp = calendar.getTime();
                    Date now = new Date(now_request);
                    Date past = new Date(past_resquest);

                    //Handles the part of the JSON data
                    JSONObject data = aux.getJSONObject("data");

                    if (temp.before(now) && temp.after(past)) {
                        result.append(temp.toString() + ",");
                        for (int j = 0; j < data.names().length(); j++) {
                            if (j != data.names().length() - 1)
                                result.append(String.valueOf(data.get(data.names().getString(j))) + ",");
                            else
                                result.append(String.valueOf(data.get(data.names().getString(j))));
                        }
                        result.append("#");
                    }
                }
                return result.toString();
            } else {
                return "Error";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error";
        } finally {
            //Close our InputStream
            if (responseBody != null && conn != null) {
                try {
                    responseBody.close();
                    conn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
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
                DeviceJ obj = databaseAccess.getDevice(_id);
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
