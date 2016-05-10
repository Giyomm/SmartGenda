package com.agenda.ter.model;

/**
 * Created by Giyomm on 25/04/2016.
 */
public class Location {
    private int mLocationId;
    private String mLocationName;
    private float mMeteoTemperature;
    private Double mLocationLatitude;
    private Double mLocationLongitude;
    private String mMeteoIcon;

    public Location(int mLocationId, String mLocationName, float mMeteoTemperature, Double mLocationLatitude, Double mLocationLongitude, String mMeteoIcon) {
        this.mLocationId = mLocationId;
        this.mLocationName = mLocationName;
        this.mMeteoTemperature = mMeteoTemperature;
        this.mLocationLatitude = mLocationLatitude;
        this.mLocationLongitude = mLocationLongitude;
        this.mMeteoIcon = mMeteoIcon;
    }

    public Location(String locationName, double latitudeEvent, double longitudeEvent) {
        this.mLocationName = mLocationName;
        this.mLocationLatitude = mLocationLatitude;
        this.mLocationLongitude = mLocationLongitude;
    }

    public int getmLocationId() {
        return mLocationId;
    }

    public void setmLocationId(int mLocationId) {
        this.mLocationId = mLocationId;
    }

    public String getmLocationName() {
        return mLocationName;
    }

    public void setmLocationName(String mLocationName) {
        this.mLocationName = mLocationName;
    }

    public float getmMeteoTemperature() {
        return mMeteoTemperature;
    }

    public void setmMeteoTemperature(float mMeteoTemperature) {
        this.mMeteoTemperature = mMeteoTemperature;
    }

    public Double getmLocationLatitude() {
        return mLocationLatitude;
    }

    public void setmLocationLatitude(Double mLocationLatitude) {
        this.mLocationLatitude = mLocationLatitude;
    }

    public Double getmLocationLongitude() {
        return mLocationLongitude;
    }

    public void setmLocationLongitude(Double mLocationLongitude) {
        this.mLocationLongitude = mLocationLongitude;
    }

    public String getmMeteoIcon() {
        return mMeteoIcon;
    }

    public void setmMeteoIcon(String mMeteoIcon) {
        this.mMeteoIcon = mMeteoIcon;
    }
}
