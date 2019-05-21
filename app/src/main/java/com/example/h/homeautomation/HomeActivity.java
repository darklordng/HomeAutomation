package com.example.h.homeautomation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class HomeActivity extends AppCompatActivity {

    Context context = this;
    private RelativeLayout relativeLayout;
    private AlertDialog.Builder builder;
    private Switch mSwitchLight;
    private SharedPreferences sharedPreferences;
    private String value, formattedDate, newTime;
    private Boolean switchStateLight;
    private JSONArray jsonArray;
    private JSONObject object;
    private String lightsAPI = "http://172.16.11.161:5000/togglelight";
    private TextView shared_prefs_name, light_status_text_view,
            current_time_text_view, current_time_text_view_fans, hours_on_text_view_lights;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Date currentTime, nTimeCal;
    private DateFormat dateFormat;
    private long fDate, fTime;

    //Bottom nav
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    break;
                case R.id.navigation_history:
                    break;
                case R.id.navigation_settings:
                    startActivity(new Intent(context, Settings.class));
                    break;
            }
            return false;
        }
    };

    public void showGraph(View v) {
        startActivity(new Intent(this, Graph.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        relativeLayout=findViewById(R.id.layout);

        jsonArray = new JSONArray();
        object = new JSONObject();
        dateFormat = new SimpleDateFormat("HH:MM:SS");
        currentTime = Calendar.getInstance().getTime();
        formattedDate = dateFormat.format(currentTime);

        nTimeCal = Calendar.getInstance().getTime();
        light_status_text_view = findViewById(R.id.light_status_text_view);
        current_time_text_view = findViewById(R.id.current_time_text_view);
        current_time_text_view_fans = findViewById(R.id.current_time_text_view_fans);

        hours_on_text_view_lights = findViewById(R.id.hours_on_text_view);

        BottomNavigationView navigation =findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mSwitchLight = findViewById(R.id.lights_toggle_switch);
        switchStateLight = mSwitchLight.isChecked();

        //current_time_text_view_fans.setText(formattedDate);

        mSwitchLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    Toast.makeText(context, "ON", Toast.LENGTH_SHORT).show();
                    try {
                        object.put("changePin", 23);
                        object.put("action", "on");
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.remove(0);
                    jsonArray.put(object);
                    Log.d("jsonArray", jsonArray.toString());
                    turnOnLights(lightsAPI, jsonArray, new VolleyCallback() {
                        @Override
                        public void onError(String message) {
                        }

                        @Override
                        public void onSuccess(String message) {
//                            light_status_text_view.setText(R.string.light_status_on);
//                            current_time_text_view.setText(formattedDate);
//                            newTime = dateFormat.format(nTimeCal);
//                            fDate=(long)Double.parseDouble(formattedDate);

//                            fTime=(long)Double.parseDouble(newTime);
//                            long c = changeInTime(fDate, fTime);
//                            hours_on_text_view_lights.setText(String.valueOf(c));
                            //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        }

                    });
                    light_status_text_view.setText(R.string.light_status_on);
                    current_time_text_view.setText(formattedDate);
                }else {
                    //Toast.makeText(context, "OFF", Toast.LENGTH_SHORT).show();
                    try {
                        object.put("changePin", 23);
                        object.put("action", "off");
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.remove(0);
                    jsonArray.put(object);
                    Log.d("offJsonArray", jsonArray.toString());
                    turnOffLights(lightsAPI, jsonArray, new VolleyCallback() {
                        @Override
                        public void onError(String message) {
                        }

                        @Override
                        public void onSuccess(String message) {
//                            light_status_text_view.setText(R.string.light_status_off);
//                            current_time_text_view.setText(formattedDate);
                            //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        }
                    });
                    nTimeCal = Calendar.getInstance().getTime();
                    newTime = dateFormat.format(nTimeCal);
                    long nT = (long) Double.parseDouble(newTime);
                    long fT = (long) Double.parseDouble(formattedDate);
                    fTime = changeInTime(nT, fT);
                    Log.d("nT", String.valueOf(nT));
                    Log.d("fT", String.valueOf(fT));
                    Log.d("fTime", String.valueOf(fTime));
                    light_status_text_view.setText(R.string.light_status_off);
                    current_time_text_view.setText(newTime);
                    hours_on_text_view_lights.setText(String.valueOf(fTime));
                }
            }
        });
        builder = new AlertDialog.Builder(context);

        WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            Toast.makeText(context, "Wifi Enabled", Toast.LENGTH_LONG).show();
        }

        if (!(wifi.isWifiEnabled())) {
            wifiDialog();
        }

        //get sharedPrefs from Preference Activity
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        value = sharedPreferences.getString("example_text", "");

        //set sharedPrefs in string value
        shared_prefs_name = findViewById(R.id.shared_prefs_text_view);
        shared_prefs_name.setText(value);

        //swipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sync) {
            //do something here
        }
        return super.onOptionsItemSelected(item);
    }

    public void refresh(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        value = sharedPreferences.getString("example_text", "");

        shared_prefs_name.setText(value + ",");

    }

    public void wifiDialog() {
        builder.setTitle("WIFI");
        builder.setMessage("Turn on Wifi");
        builder.setIcon(R.drawable.baseline_network_wifi_black_18dp);
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar snackbar = Snackbar.make(relativeLayout, "Turn on WIFI!!", Snackbar.LENGTH_LONG)
                        .setAction("TURN ON", new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                            }
                        }).setActionTextColor(Color.RED); //changing message text color

                View snackView = snackbar.getView();
                TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();

            }
        }) .setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
            }
        });
        //show alert dialog
        builder.show();
    }


    public void turnOnLights(String url, JSONArray array, final VolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(com.android.volley.Request.Method.POST, url,
                array, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Toast.makeText(context, "Lights On", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                Log.d("Err", error.toString());

            }
        });
        requestQueue.add(jsonArrayRequest);
    }



    public void turnOffLights(String url, JSONArray array, final VolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(com.android.volley.Request.Method.POST, url,
                array, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Toast.makeText(context, "Lights Off", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                Log.d("offErr", error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public long changeInTime(long cTime, long nTime) {
        cTime=(long) Double.parseDouble(formattedDate);

        Date date = Calendar.getInstance().getTime();
        DateFormat dFormat = new SimpleDateFormat("HH:MM:SS");
        String nString = dFormat.format(date);
        nTime=(long) Double.parseDouble(nString);

        long fTime = nTime-cTime;
        return fTime;

    }

}
