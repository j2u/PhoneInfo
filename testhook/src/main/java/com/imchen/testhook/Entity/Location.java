package com.imchen.testhook.Entity;

/**
 * Created by imchen on 2017/8/4.
 */

public class Location {

    private double initLatitude;
    private double initLongitude;
    private double altitude;
    private double speed;
    private float bearing;
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
                "\ninitLatitude=" + initLatitude +
                "\ninitLongitude=" + initLongitude +
                "\naltitude=" + altitude +
                "\nspeed=" + speed +
                "\nbearing=" + bearing +
                "\naccuracy=" + accuracy +
                '}';
    }
}
