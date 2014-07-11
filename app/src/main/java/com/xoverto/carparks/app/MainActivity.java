package com.xoverto.carparks.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends ActionBarActivity implements CarParkFragment.OnFragmentInteractionListener {

    public final static String EXTRA_CARPARK_LOCATION = "com.xoverto.carparks.app.CARPARK_LOCATION";
    public final static String EXTRA_CARPARK_NAME = "com.xoverto.carparks.app.CARPARK_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new CarParkFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_map) {

            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);

            return true;
        }
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentInteraction(LatLng latLng) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(EXTRA_CARPARK_LOCATION, latLng);
        startActivity(intent);
    }
}
