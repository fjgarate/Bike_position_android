package com.example.fgarate.bike_position_android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GPS_Service extends Service {
    private LocationListener listener;
    private LocationManager locationManager;
    private static final String LOGTAG = "Bike_position";
    long minTime;
    long minDistance;
    private boolean currentlyProcessingLocation = false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOGTAG," oncreate LocationService");
      //  defaultUploadWebsite = getString(R.string.default_upload_website);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        minTime = intent.getExtras().getLong("minTime");
        minDistance = intent.getExtras().getLong("minDistance");
        Log.i(LOGTAG,"minTime  onStartCommand"+ minTime);

        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOGTAG,"onDestroy ");
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }

    public void startTracking() {


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String url = "http://192.168.0.193:3000/point";
                String geoJson = createJson(location);
                new SendParams().execute(url, geoJson);
                Intent i = new Intent("location_update");
                Log.i(LOGTAG,"coordinates "+ location.getLongitude()+" "+location.getLatitude());
                i.putExtra("coordinates",location.getLongitude()+" "+location.getLatitude());
                sendBroadcast(i);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Log.i(LOGTAG,"minTime onCreate "+ minTime);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime,minDistance,listener);

    }
public String createJson(Location location){


        JSONObject manJson = new JSONObject();
    try {
        manJson.put("type", "Point");
        manJson.put("name", "point");
        manJson.put("email", "fjgbarreiro@gmail.com");
        //    manJson.put("color", "#0000ff");
        JSONObject style = new JSONObject();
        style.put("radius", 8);
        style.put("fillColor", "#00ce00");
        style.put("color", "#008c00");
        style.put("weight", 2);
        style.put("opacity", 1);
        style.put("fillOpacity", 1);
        //   manJson.put("style", style);
        JSONObject properties = new JSONObject();
        // properties.put("marker-color", "#7e7e7e");
        // properties.put("marker-size", "medium");
        // properties.put("marker-symbol", "circle");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        properties.put("date", new Date());
        manJson.put("properties", properties);
        JSONArray JSONArrayCoord = new JSONArray();
        JSONArrayCoord.put(location.getLongitude());
        JSONArrayCoord.put(location.getLatitude());

        manJson.put("coordinates", JSONArrayCoord);

    } catch (JSONException e) {
        e.printStackTrace();
        return null;
    }
    return manJson.toString();

}
}
