package com.agenda.ter.model;

/**
 * Created by Giyomm on 25/04/2016.
 */
public class Meteo {
    private float mMeteoTemperature;
    private Double mMeteoLatitude;
    private Double mMeteoLongitude;
    private String mMeteoIcon;

    public Meteo(float mMeteoTemperature, Double mMeteoLatitude, Double mMeteoLongitude, String mMeteoIcon) {
        this.mMeteoTemperature = mMeteoTemperature;
        this.mMeteoLatitude = mMeteoLatitude;
        this.mMeteoLongitude = mMeteoLongitude;
        this.mMeteoIcon = mMeteoIcon;
    }

    public Meteo(float mMeteoTemperature, Double mMeteoLatitude, Double mMeteoLongitude) {
        this.mMeteoTemperature = mMeteoTemperature;
        this.mMeteoLatitude = mMeteoLatitude;
        this.mMeteoLongitude = mMeteoLongitude;
    }

    public float getmMeteoTemperature() {
        return mMeteoTemperature;
    }

    public void setmMeteoTemperature(float mMeteoTemperature) {
        this.mMeteoTemperature = mMeteoTemperature;
    }

    public Double getmMeteoLatitude() {
        return mMeteoLatitude;
    }

    public void setmMeteoLatitude(Double mMeteoLatitude) {
        this.mMeteoLatitude = mMeteoLatitude;
    }

    public Double getmMeteoLongitude() {
        return mMeteoLongitude;
    }

    public void setmMeteoLongitude(Double mMeteoLongitude) {
        this.mMeteoLongitude = mMeteoLongitude;
    }

    public String getmMeteoIcon() {
        return mMeteoIcon;
    }

    public void setmMeteoIcon(String mMeteoIcon) {
        this.mMeteoIcon = mMeteoIcon;
    }
}
