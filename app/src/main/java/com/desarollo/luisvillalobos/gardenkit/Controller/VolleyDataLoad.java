package com.desarollo.luisvillalobos.gardenkit.Controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.desarollo.luisvillalobos.gardenkit.Model.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolleyDataLoad {

    private static final String JSON_URL = "http://api.carriots.com/streams/?";
    private static final String DEVICE = "device=";
    private static final String AT_FROM = "&at_from="; // sub second
    private static final String AT_TO = "&at_to="; // add second
    private static final String SORT = "&at_to=";
    private static final String ORDER = "&order=";// -1 HighToLow  +1 LowToHigh
    private static final String API = "carriots.apikey";
    private static final String API_KEY = "cef8f456d2ec6bebd28021dc8b1bbcfc0330ad558a0c0b2e1b4b19f8bb514d51";

    public static List<Data> dataList;

    public static void loadByPastDate(final Context context, final Date pastDate) {
        dataList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL + DEVICE,
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
                                Calendar calendar = Calendar.getInstance();
                                //A Unix timestamp is a number of seconds since 01-01-1970 00:00:00 GMT. Java measures time in milliseconds since 01-01-1970 00:00:00 GMT. You need to multiply the Unix timestamp by 1000:
                                calendar.setTimeInMillis(at * 1000L);
                                Date atDate = calendar.getTime();
                                Date now = atDate;


                                if (atDate.before(now) && atDate.after(pastDate)) {
                                    //Log.v("Fecha", "Ahora: "+now_past[0].toString());
                                    //Log.v("Fecha", "Pasado: "+now_past[1].toString());
                                    //Log.v("Fecha", "At: "+atDate.toString());
                                    Data dataJSON = new Data(data.getInt("H1"),
                                            data.getInt("H2"),
                                            data.getInt("H3"),
                                            data.getInt("H4"),
                                            data.getInt("H5"),
                                            data.getDouble("PH"));
                                    dataList.add(dataJSON);
                                }
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
                headers.put(API, API_KEY);
                return headers;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    protected void loadByDatesOption(final Context context, final int optionTime, final int backwardTimeCount) {
        dataList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL + DEVICE,
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
                                Calendar calendar = Calendar.getInstance();
                                //A Unix timestamp is a number of seconds since 01-01-1970 00:00:00 GMT. Java measures time in milliseconds since 01-01-1970 00:00:00 GMT. You need to multiply the Unix timestamp by 1000:
                                calendar.setTimeInMillis(at * 1000L);
                                Date atDate = calendar.getTime();
                                Date[] now_past = DateOperations.getNowAndPast((byte) optionTime, backwardTimeCount);

                                if (atDate.before(now_past[0]) && atDate.after(now_past[1])) {
                                    //Log.v("Fecha", "Ahora: "+now_past[0].toString());
                                    //Log.v("Fecha", "Pasado: "+now_past[1].toString());
                                    //Log.v("Fecha", "At: "+atDate.toString());
                                    Data dataJSON = new Data(data.getInt("H1"),
                                            data.getInt("H2"),
                                            data.getInt("H3"),
                                            data.getInt("H4"),
                                            data.getInt("H5"),
                                            data.getDouble("PH"));
                                    dataList.add(dataJSON);
                                }
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
                headers.put(API, API_KEY);
                return headers;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public static void loadAllData(final Context context) {
        dataList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL + DEVICE,
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
                headers.put(API, API_KEY);
                return headers;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
