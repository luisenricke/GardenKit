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
     */

}
