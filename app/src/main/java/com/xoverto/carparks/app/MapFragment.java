package com.xoverto.carparks.app;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;


public class MapFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static View view;

    private static GoogleMap mMap;
    public LatLng mLatLong;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        view = (RelativeLayout) inflater.inflate(R.layout.fragment_map, container, false);

        Bundle bundle = this.getArguments();
        if(bundle != null){
            double latitude = bundle.getDouble(MainActivity.EXTRA_CARPARK_LOCATION_LAT);
            double longitude = bundle.getDouble(MainActivity.EXTRA_CARPARK_LOCATION_LONG);
            mLatLong = new LatLng(latitude, longitude);
        }

        setUpMapIfNeeded(); // For setting up the MapFragment

        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    /***** Sets up the map if it is possible to do so *****/
    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) MainActivity.fragmentManager
                    .findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the
     * camera.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap}
     * is not null.
     */
    private void setUpMap() {


        mMap.setMyLocationEnabled(true);

        if(mLatLong == null) {
            Criteria criteria = new Criteria();
            LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            double lat =  location.getLatitude();
            double lng = location.getLongitude();
            mLatLong = new LatLng(lat, lng);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLong, 16));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle != null){
            double latitude = bundle.getDouble(MainActivity.EXTRA_CARPARK_LOCATION_LAT);
            double longitude = bundle.getDouble(MainActivity.EXTRA_CARPARK_LOCATION_LONG);
            mLatLong = new LatLng(latitude, longitude);
        }

        if (mMap != null)
            setUpMap();

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) MainActivity.fragmentManager
                    .findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }

    /**** The mapfragment's id must be removed from the FragmentManager
     **** or else if the same it is passed on the next time then
     **** app will crash ****/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
            MainActivity.fragmentManager.beginTransaction()
                    .remove(MainActivity.fragmentManager.findFragmentById(R.id.map)).commit();
            mMap = null;
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
                CarParkProvider.KEY_UPDATED,
                CarParkProvider.KEY_SPACES
        };
        CursorLoader loader = new CursorLoader(getActivity(),
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
        int spaces = 0;
        String name = "";

        locationCount = cursor.getCount();
        cursor.moveToFirst();

        mMap.clear();

        for(int i = 0; i < locationCount; i++) {
            lat = cursor.getDouble(cursor.getColumnIndex(CarParkProvider.KEY_LOCATION_LAT));
            lng = cursor.getDouble(cursor.getColumnIndex(CarParkProvider.KEY_LOCATION_LNG));
            name = cursor.getString(cursor.getColumnIndex(CarParkProvider.KEY_NAME));
            updated = cursor.getLong(cursor.getColumnIndex(CarParkProvider.KEY_UPDATED));
            spaces = cursor.getInt(cursor.getColumnIndex(CarParkProvider.KEY_SPACES));

            LatLng location = new LatLng(lat, lng);

            DateFormat dateF = DateFormat.getDateTimeInstance();
            String text = spaces + " spaces as of " + dateF.format(new Date(updated));

            drawMarker(location, name, text);

            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
