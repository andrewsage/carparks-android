package com.xoverto.carparks.app;

import android.location.Location;

/**
 * Created by andrew on 05/07/2014.
 */
public class CarPark {

    private String carParkID;
    private String name;
    private Location location;
    private Integer occupancy;
    private Double occupancyPercentage;

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

    public Integer getOccupancy() { return occupancy; }
    public void setOccupancy(Integer occupancy) { this.occupancy = occupancy; }

    public Double getOccupancyPercentage() { return occupancyPercentage; }
    public void setOccupancyPercentage(Double occupancyPercentage) { this.occupancyPercentage = occupancyPercentage; }

    public Integer getSpaces() {
        return (int)Math.round(occupancy - ((occupancy / 100) * occupancyPercentage));
    }

    public CarPark(String name, Location location, String id, Integer occupancy, Double occupancyPercentage) {

        this.name = name;
        this.location = location;
        this.carParkID = id;
        this.occupancy = occupancy;
        this.occupancyPercentage = occupancyPercentage;
    }

    @Override
    public String toString() {
        return name + ":" + location.getLatitude() + ", " + location.getLongitude();
    }
}
