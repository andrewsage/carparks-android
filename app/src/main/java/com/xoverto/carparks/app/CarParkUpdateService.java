package com.xoverto.carparks.app;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class CarParkUpdateService extends IntentService {

    public static String TAG = "CARPARK_UPDATE_SERVICE";

    public CarParkUpdateService() {
        super("CarParkUpdateService");
    }

    public CarParkUpdateService(String name) {
        super(name);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int updateFrequency = 1; //Integer.parseInt(prefs.getString("refresh_frequency", 5));


        if(updateFrequency > 0) {
            int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            long timeToRefresh = SystemClock.elapsedRealtime() + updateFrequency*60*1000;
            alarmManager.setInexactRepeating(alarmType, timeToRefresh, updateFrequency*60*1000, alarmIntent);

        } else {
            alarmManager.cancel(alarmIntent);
        }

        refreshCarParks();
    }

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        String ALARM_ACTION = CarParkAlarmReceiver.ACTION_REFRESH_CARPARK_ALARM;
        Intent intentToFire = new Intent(ALARM_ACTION);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void refreshCarParks() {
        // Get the JSON
        URL url;
        try {
            String carParksFeed = getString(R.string.carparks_feed);
            url = new URL(carParksFeed);

            URLConnection connection;
            connection = url.openConnection();

            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                JSONArray carParks = new JSONArray(responseStrBuilder.toString());
                for(int i = 0; i < carParks.length(); i++) {
                    JSONObject carParkJSON = carParks.getJSONObject(i);
                    String id = carParkJSON.getString("id");
                    String name = carParkJSON.getString("facility_name");
                    String latitude = carParkJSON.getString("lat");
                    String longitude = carParkJSON.getString("long");

                    Double latPosition = 0.0;
                    Double longPosition = 0.0;
                    Integer occupancy = 0;
                    Double occupancyPercentage = 0.0;

                    try {
                        latPosition = Double.parseDouble(latitude);
                        longPosition = Double.parseDouble(longitude);
                    } catch (NullPointerException e) {
                        Log.d(TAG, "Location parsing exception for " + name, e);
                    }

                    Location location = new Location("dummyGPS");
                    location.setLatitude(latPosition);
                    location.setLongitude(longPosition);

                    final CarPark carPark = new CarPark(name, location, id, occupancy, occupancyPercentage);
                    addNewCarPark(carPark);
                }
            }

        } catch (MalformedURLException e) {
            Log.d(TAG, "MalformedURLException");
        } catch (IOException e) {
            Log.d(TAG, "IOException");
        } catch (JSONException e) {
            Log.d(TAG, "JSONException");
        } finally {
        }
    }

    private void addNewCarPark(CarPark carPark) {
        ContentResolver cr = getContentResolver();

        // Construct a where clause to make sure we don't already have this carpark in the provider
        String w = CarParkProvider.KEY_NAME + " = '" + carPark.getName() + "'";

        // If the carpark is new, insert it into the provider
        Cursor query = cr.query(CarParkProvider.CONTENT_URI, null, w, null, null);
        if(query.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(CarParkProvider.KEY_CAR_PARK_ID, carPark.getCarParkID());
            values.put(CarParkProvider.KEY_NAME, carPark.getName());

            double lat = carPark.getLocation().getLatitude();
            double lng = carPark.getLocation().getLongitude();
            values.put(CarParkProvider.KEY_LOCATION_LAT, lat);
            values.put(CarParkProvider.KEY_LOCATION_LNG, lng);
            values.put(CarParkProvider.KEY_OCCUPANCY, carPark.getOccupancy());
            values.put(CarParkProvider.KEY_OCCUPANCY_PERCENTAGE, carPark.getOccupancyPercentage());
            values.put(CarParkProvider.KEY_SPACES, carPark.getSpaces());

            cr.insert(CarParkProvider.CONTENT_URI, values);
        } else {
            ContentValues values = new ContentValues();
            values.put(CarParkProvider.KEY_CAR_PARK_ID, carPark.getCarParkID());
            values.put(CarParkProvider.KEY_NAME, carPark.getName());

            double lat = carPark.getLocation().getLatitude();
            double lng = carPark.getLocation().getLongitude();
            values.put(CarParkProvider.KEY_LOCATION_LAT, lat);
            values.put(CarParkProvider.KEY_LOCATION_LNG, lng);
            values.put(CarParkProvider.KEY_UPDATED, java.lang.System.currentTimeMillis());
            values.put(CarParkProvider.KEY_OCCUPANCY, carPark.getOccupancy());
            values.put(CarParkProvider.KEY_OCCUPANCY_PERCENTAGE, carPark.getOccupancyPercentage());
            values.put(CarParkProvider.KEY_SPACES, carPark.getSpaces());

            cr.update(CarParkProvider.CONTENT_URI, values, w, null);
        }
        query.close();
    }
}
