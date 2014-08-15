package com.xoverto.carparks.app;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends ActionBarActivity implements CarParkFragment.OnFragmentInteractionListener {

    public final static String EXTRA_CARPARK_LOCATION = "com.xoverto.carparks.app.CARPARK_LOCATION";
    public final static String EXTRA_CARPARK_LOCATION_LAT = "com.xoverto.carparks.app.CARPARK_LOCATION_LAT";
    public final static String EXTRA_CARPARK_LOCATION_LONG = "com.xoverto.carparks.app.CARPARK_LOCATION_LONG";

    public final static String EXTRA_CARPARK_NAME = "com.xoverto.carparks.app.CARPARK_NAME";

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    public static FragmentManager fragmentManager;

    // Tab titles
    private String[] tabs = { "A-Z", "by Distance", "Map" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        viewPager = (ViewPager)findViewById(R.id.pager);
        actionBar = getSupportActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        /** Defining tab listener */
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            }

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
            }
        };

        for(String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
            .setTabListener(tabListener));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        if (savedInstanceState == null) {
            /*
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new CarParkFragment())
                    .commit();
                    */
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
        MapFragment mapFragment =  (MapFragment)mAdapter.getItem(2);
        mapFragment.mLatLong = latLng;
        Bundle bundle = new Bundle();
        bundle.putDouble(EXTRA_CARPARK_LOCATION_LAT, latLng.latitude);
        bundle.putDouble(EXTRA_CARPARK_LOCATION_LONG, latLng.longitude);
        mapFragment.setArguments(bundle);

        viewPager.setCurrentItem(2);

        /*
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(EXTRA_CARPARK_LOCATION, latLng);
        startActivity(intent);
        */
    }
}
