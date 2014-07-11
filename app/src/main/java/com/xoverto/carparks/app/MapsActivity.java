package com.xoverto.carparks.app;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        getLoaderManager().initLoader(0, null, this);

        RadioGroup rgViews = (RadioGroup) findViewById(R.id.rg_views);

        rgViews.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_normal) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }else if(checkedId == R.id.rb_hybrid) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }else if(checkedId == R.id.rb_satellite){
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }else if(checkedId == R.id.rb_terrain){
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        //mMap.addMarker(new MarkerOptions().position(new LatLng(57.148624, -2.1006504)).title("Car Park"));

        Intent intent = getIntent();
        LatLng latLng = intent.getParcelableExtra(MainActivity.EXTRA_CARPARK_LOCATION);

        if(latLng != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                    16));
        }
    }


    private void drawMarker(LatLng latLng, String name, String updated){
        // add a marker to the map indicating our current position
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .snippet(updated)
                .title(name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.carpark)));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                CarParkProvider.KEY_ID,
                CarParkProvider.KEY_NAME,
                CarParkProvider.KEY_CAR_PARK_ID,
                CarParkProvider.KEY_LOCATION_LAT,
                CarParkProvider.KEY_LOCATION_LNG,
                CarParkProvider.KEY_UPDATED
        };
        CursorLoader loader = new CursorLoader(this,
                CarParkProvider.CONTENT_URI,
                projection, null, null, null);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        int locationCount = 0;
        double lat = 0;
        double lng = 0;
        long updated = 0;
        String name = "";

        locationCount = cursor.getCount();
        cursor.moveToFirst();

        mMap.clear();


        for(int i = 0; i < locationCount; i++) {
            lat = cursor.getDouble(cursor.getColumnIndex(CarParkProvider.KEY_LOCATION_LAT));
            lng = cursor.getDouble(cursor.getColumnIndex(CarParkProvider.KEY_LOCATION_LNG));
            name = cursor.getString(cursor.getColumnIndex(CarParkProvider.KEY_NAME));
            updated = cursor.getLong(cursor.getColumnIndex(CarParkProvider.KEY_UPDATED));

            LatLng location = new LatLng(lat, lng);

            DateFormat dateF = DateFormat.getDateTimeInstance();
            String text = "Last Updated: " + dateF.format(new Date(updated));

            drawMarker(location, name, text);

            cursor.moveToNext();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
