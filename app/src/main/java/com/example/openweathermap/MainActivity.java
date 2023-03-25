package com.example.openweathermap;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "62f612d0399ba05729e890ef4394a359";
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static TextView locationText;
    private static TextView currentLocation;
    private static TextView timeStamp;
    private static TextView nyText, sgText, mumbaiText, delhiText, sydneyText, melbText;
    private static Button refreshButton;
    private static ArrayList<String> cities = new ArrayList<String>();
    String lastUpdate, lastWeather, lastLocation, lastNY, lastSG, lastMB, lastDH, lastSYD, lastMEL;
    LocationTrack locationTrack;
    DecimalFormat df = new DecimalFormat("#.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        String lastUpdate = pref.getString("timeStamp", "No prior update");
        String lastWeather = pref.getString("weatherStamp", "No Weather");
        String lastLocation = pref.getString("locationStamp", "Weather Forecast");
        String lastNY = pref.getString("ny", "New York");
        String lastSG = pref.getString("sg", "Singapore");
        String lastMB = pref.getString("mb", "Mumbai");
        String lastDH = pref.getString("dh", "Delhi");
        String lastSYD = pref.getString("sydney", "Sydney");
        String lastMEL = pref.getString("mel", "Melbourne");
        populateCities();
        connectViews();
        timeStamp.setText(lastUpdate);
        currentLocation.setText(lastLocation);
        locationText.setText(lastWeather);
        nyText.setText(lastNY);
        sgText.setText(lastSG);
        mumbaiText.setText(lastMB);
        delhiText.setText(lastDH);
        sydneyText.setText(lastSYD);
        melbText.setText(lastMEL);
    }

    /* WEIRD, DOESNT WORK WHEN ABSTRACTED OUT
    private void setHistory(View view) {
        timeStamp.setText(lastUpdate);
        currentLocation.setText(lastLocation);
        locationText.setText(lastWeather);
        nyText.setText(lastNY);
        sgText.setText(lastSG);
        mumbaiText.setText(lastMB);
        delhiText.setText(lastDH);
        sydneyText.setText(lastSYD);
        melbText.setText(lastMEL);
    }

    private void getHistory() {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        String lastUpdate = pref.getString("timeStamp", "No prior update");
        String lastWeather = pref.getString("weatherStamp", "No Weather");
        String lastLocation = pref.getString("locationStamp", "Weather Forecast");
        String lastNY = pref.getString("ny", "New York");
        String lastSG = pref.getString("sg", "Singapore");
        String lastMB = pref.getString("mb", "Mumbai");
        String lastDH = pref.getString("dh", "Delhi");
        String lastSYD = pref.getString("sydney", "Sydney");
        String lastMEL = pref.getString("mel", "Melbourne");
    }
    */


    private void connectViews() {
        locationText = findViewById(R.id.LocationText);
        refreshButton = findViewById(R.id.RefreshButton);
        currentLocation = findViewById(R.id.CurrentLocation);
        timeStamp = findViewById(R.id.TimeStamp);
        nyText =findViewById(R.id.nyText);
        sgText = findViewById(R.id.sgText);
        mumbaiText = findViewById(R.id.mumbaiText);
        delhiText = findViewById(R.id.delhiText);
        sydneyText = findViewById(R.id.sydneyText);
        melbText = findViewById(R.id.melbourneText);
    }
    private void populateCities() {
        cities.add("New York");
        cities.add("Singapore");
        cities.add("Mumbai");
        cities.add("Delhi");
        cities.add("Sydney");
        cities.add("Melbourne");
    }

    public void refreshWeatherDetails(View view) {
        locationTrack = new LocationTrack(MainActivity.this);
        if (locationTrack.canGetLocation() && isConnected()) {
            getOtherWeather(view);
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            String tempUrl = "";
            String longi = Double.toString(longitude);
            String lati = Double.toString(latitude);
            tempUrl = String.format("%s?lat=%s&lon=%s&appid=%s", url, lati, longi, appid);
            //locationText.setText(tempUrl);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Log.d("response", response);
                            String output = "";
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                                JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                                String description = jsonObjectWeather.getString("description");
                                JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                                double temp = jsonObjectMain.getDouble("temp") - 273.15;
                                double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                                float pressure = jsonObjectMain.getInt("pressure");
                                int humidity = jsonObjectMain.getInt("humidity");
                                JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                                String wind = jsonObjectWind.getString("speed");
                                JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                                String clouds = jsonObjectClouds.getString("all");
                                JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                                String countryName = jsonObjectSys.getString("country");
                                String cityName = jsonResponse.getString("name");
                                output += " Temp: " + df.format(temp) + " °C"
                                        + "\n Feels Like: " + df.format(feelsLike) + " °C"
                                        + "\n Humidity: " + humidity + "%"
                                        + "\n Description: " + description
                                        + "\n Wind Speed: " + wind + "m/s"
                                        + "\n Cloudiness: " + clouds + "%"
                                        + "\n Pressure: " + pressure + " hPa";
                                locationText.setText(output);
                                currentLocation.setText(cityName + " (" + countryName + ")");
                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a dd MM yyyy");
                                String currentDateandTime = sdf.format(new Date());
                                timeStamp.setText(String.format("Last updated at: %s", currentDateandTime));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
            //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "No Wifi Connection", Toast.LENGTH_SHORT).show();
        } else  {
            locationTrack.showSettingsAlert();
        }
    }

    public void getOtherWeather(View view) {
        Toast.makeText(getApplicationContext(),"HELLO THERE", Toast.LENGTH_LONG).show();
        for (String city : cities) {
            String tempUrl = "";
            tempUrl = String.format("%s?q=%s&appid=%s", url, city, appid);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Log.d("response", response);
                            String output = String.format("%s\n", city);
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                                JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                                String description = jsonObjectWeather.getString("description");
                                JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                                double temp = jsonObjectMain.getDouble("temp") - 273.15;
                                int humidity = jsonObjectMain.getInt("humidity");
                                JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                                String wind = jsonObjectWind.getString("speed");
                                JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                                String clouds = jsonObjectClouds.getString("all");
                                JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                                //String countryName = jsonObjectSys.getString("country");
                                //String cityName = jsonResponse.getString("name");
                                output += " Temp: " + df.format(temp) + " °C"
                                        + "\n Humidity: " + humidity + "%"
                                        + "\n Description: " + description
                                        + "\n Wind Speed: " + wind + "m/s"
                                        + "\n Cloudiness: " + clouds + "%";
                                switch (city) {
                                    case ("New York"):
                                        nyText.setText(output);
                                        break;
                                    case ("Singapore"):
                                        sgText.setText(output);
                                        break;
                                    case ("Mumbai"):
                                        mumbaiText.setText(output);
                                        break;
                                    case ("Delhi"):
                                        delhiText.setText(output);
                                        break;
                                    case ("Sydney"):
                                        sydneyText.setText(output);
                                        break;
                                    case ("Melbourne"):
                                        melbText.setText(output);
                                        break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }

    // Check for internet connection
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
         } catch (Exception e) {
            Log.e("Connectivty Exception", e.getMessage());
        }
        return connected;
    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String lastUpdated = timeStamp.getText().toString();
        String lastLocation = currentLocation.getText().toString();
        String lastWeather = locationText.getText().toString();
        String ny = nyText.getText().toString();
        String sg = sgText.getText().toString();
        String mb = mumbaiText.getText().toString();
        String dh = delhiText.getText().toString();
        String sydney = sydneyText.getText().toString();
        String mel = melbText.getText().toString();
        editor.putString("timeStamp", lastUpdated);
        editor.putString("locationStamp", lastLocation);
        editor.putString("weatherStamp", lastWeather);
        editor.putString("ny", ny);
        editor.putString("sg", sg);
        editor.putString("mb", mb);
        editor.putString("dh", dh);
        editor.putString("sydney", sydney);
        editor.putString("mel", mel);
        editor.apply();
    }
    /*
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("currentLocation", currentLocation.getText().toString());
        savedInstanceState.putString("currentWeather", locationText.getText().toString());
        savedInstanceState.putString("lastUpdated", timeStamp.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState){
    }*/
}