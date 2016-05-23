package com.agenda.ter.model;

/**
 * @author Smartgenda Team
 * CLasse définissant un objet Lieu d'un évènement
 */
public class Location {
    /**ID du lieu*/
    private int mLocationId;

    /**Nom du lieu*/
    private String mLocationName;

    /**Température de la météo du lieu*/
    private float mMeteoTemperature;

    /**Latitude du lieu*/
    private Double mLocationLatitude;

    /**Longitude du lieu*/
    private Double mLocationLongitude;

    /**Icon de la météo du lieu*/
    private String mMeteoIcon;

    /**
     * Constructeur
     * @param mLocationId
     * @param mLocationName
     * @param mMeteoTemperature
     * @param mLocationLatitude
     * @param mLocationLongitude
     * @param mMeteoIcon
     */
    public Location(int mLocationId, String mLocationName, float mMeteoTemperature, Double mLocationLatitude, Double mLocationLongitude, String mMeteoIcon) {
        this.mLocationId = mLocationId;
        this.mLocationName = mLocationName;
        this.mMeteoTemperature = mMeteoTemperature;
        this.mLocationLatitude = mLocationLatitude;
        this.mLocationLongitude = mLocationLongitude;
        this.mMeteoIcon = mMeteoIcon;
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
