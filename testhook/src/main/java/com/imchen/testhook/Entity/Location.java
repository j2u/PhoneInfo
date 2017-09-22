package com.imchen.testhook.Entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by imchen on 2017/8/4.
 */

public class Location {

    @SerializedName("Location.initLatitude")
    private double initLatitude;
    @SerializedName("Location.initLongitude")
    private double initLongitude;
    @SerializedName("Location.altitude")
    private double altitude;
    @SerializedName("Location.speed")
    private double speed;
    @SerializedName("Location.bearing")
    private float bearing;
    @SerializedName("Location.accuracy")
    private float accuracy;

    public Location() {
    }

    public double getInitLatitude() {
        return initLatitude;
    }

    public void setInitLatitude(double initLatitude) {
        this.initLatitude = initLatitude;
    }

    public double getInitLongitude() {
        return initLongitude;
    }

    public void setInitLongitude(double initLongitude) {
        this.initLongitude = initLongitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        return "Location{" +
                "\n initLatitude=" + initLatitude +
                "\n initLongitude=" + initLongitude +
                "\n altitude=" + altitude +
                "\n speed=" + speed +
                "\n bearing=" + bearing +
                "\n accuracy=" + accuracy +
                "\n}";
    }
}
