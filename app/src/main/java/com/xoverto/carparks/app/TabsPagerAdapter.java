package com.xoverto.carparks.app;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by andrew on 11/08/2014.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch(index) {
            case 0:
                CarParkFragment fragment1 = new CarParkFragment();
                return fragment1;

            case 1:
                CarParkFragment fragment2 = new CarParkFragment();
                return fragment2;

            case 2:
                MapFragment fragment3 = new MapFragment();
                return fragment3;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
