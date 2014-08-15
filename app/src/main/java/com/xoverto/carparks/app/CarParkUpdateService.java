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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
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

        // TODO: reinstate refreshCarParks
/*
        // Get the XML
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
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();

                NodeList nl = docEle.getElementsByTagName("facility");

                if(nl != null && nl.getLength() > 0) {
                    for(int i = 0; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        Element facilityName = (Element)entry.getElementsByTagName("facility_name").item(0);
                        Element carParkID = (Element)entry.getElementsByTagName("id").item(0);
                        Element latPositionElement = (Element)entry.getElementsByTagName("lat").item(0);
                        Element longPositionElement = (Element)entry.getElementsByTagName("long").item(0);
                        Element featuresElement = (Element)entry.getElementsByTagName("features").item(0);
                        Element occupancyElement = (Element)featuresElement.getElementsByTagName("occupancy").item(0);
                        Element occupancyPercentageElement = (Element)featuresElement.getElementsByTagName("occupancypercentage").item(0);


                        String name = facilityName.getFirstChild().getNodeValue();
                        String id = carParkID.getFirstChild().getNodeValue();
                        Double latPosition = 0.0;
                        Double longPosition = 0.0;
                        Integer occupancy = Integer.parseInt(occupancyElement.getFirstChild().getNodeValue());
                        Double occupancyPercentage = Double.parseDouble(occupancyPercentageElement.getFirstChild().getNodeValue());


                        try {
                            latPosition = Double.parseDouble(latPositionElement.getFirstChild().getNodeValue());
                            longPosition = Double.parseDouble(longPositionElement.getFirstChild().getNodeValue());
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

            }

        } catch (MalformedURLException e) {
            Log.d(TAG, "MalformedURLException");
        } catch (IOException e) {
            Log.d(TAG, "IOException");
        } catch (ParserConfigurationException e) {
            Log.d(TAG, "Parser Configuration Exception");
        } catch (SAXException e) {
            Log.d(TAG, "SAX Exception");
        } finally {
        }
        */
    }

    private void addNewCarPark(CarPark carPark) {

        //mCarParkList.add(carPark);
        //mAdapter.notifyDataSetChanged();


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
