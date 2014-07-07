package com.xoverto.carparks.app;

import android.location.Location;

/**
 * Created by andrew on 05/07/2014.
 */
public class CarPark {

    private String carParkID;
    private String name;
    private Location location;

    public String getCarParkID() { return carParkID; }
    public void setCarParkID(String carParkID) { this.carParkID = carParkID; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public CarPark(String name, Location location, String id) {

        this.name = name;
        this.location = location;
        this.carParkID = id;
    }

    @Override
    public String toString() {
        return name + ":" + location.getLatitude() + ", " + location.getLongitude();
    }
}
