package com.agenda.ter.model;

import java.util.Date;

/**
 * @author Smartgenda Team
 * Classe définissant un Objet Evènement
 */
public class Event {

    /**ID de l'évènement*/
    private int mEventId;

    /**Nom de l'évènement*/
    private String mEventName;

    /**Email associé à l'évènement*/
    private String mEventEmail;

    /**Date de l'évènement*/
    private Date mEventDate;

    /**Heure de l'évènement*/
    private String mEventTime;

    /**Description de l'évènement*/
    private String mEventDescription;

    /**ID de l'objet Location de l'évènement*/
    private int mEventLocationId;

    /**ID de l'objet Notification de l'évènement*/
    private int mEventNotificationId;

    /**
     * Constructeur par défaut
     */
    public Event(){}

    /**
     * Constructeur
     * @param id
     * @param mEventName
     * @param mEventDate
     * @param mEventTime
     * @param mEventDescription
     * @param mEventLocation
     * @param mEventNotification
     * @param mEventEmail
     */
    public Event(int id, String mEventName, Date mEventDate, String mEventTime, String mEventDescription, int mEventLocation, int mEventNotification, String mEventEmail) {
        this.mEventId = id;
        this.mEventName = mEventName;
        this.mEventEmail = mEventEmail;
        this.mEventDate = mEventDate;
        this.mEventTime = mEventTime;
        this.mEventDescription = mEventDescription;
        this.mEventLocationId = mEventLocation;
        this.mEventNotificationId = mEventNotification;
    }

    public int getmEventId() {
        return mEventId;
    }

    public void setmEventId(int mEventId) {
        this.mEventId = mEventId;
    }

    public String getmEventName() {
        return mEventName;
    }

    public void setmEventName(String mEventName) {
        this.mEventName = mEventName;
    }

    public Date getmEventDate() {
        return mEventDate;
    }

    public void setmEventDate(Date mEventDate) {
        this.mEventDate = mEventDate;
    }

    public String getmEventTime() {
        return mEventTime;
    }

    public void setmEventTime(String mEventTime) {
        this.mEventTime = mEventTime;
    }

    public int getmEventNotificationId() {
        return mEventNotificationId;
    }

    public void setmEventNotificationId(int mEventNotificationId) {
        this.mEventNotificationId = mEventNotificationId;
    }

    public String getmEventDescription() {
        return mEventDescription;
    }

    public void setmEventDescription(String mEventDescription) {
        this.mEventDescription = mEventDescription;
    }

    public int getmEventLocationId() {
        return mEventLocationId;
    }

    public void setmEventLocationId(int mEventLocationId) {
        this.mEventLocationId = mEventLocationId;
    }

    public String getmEventEmail() {
        return mEventEmail;
    }

    public void setmEventEmail(String mEventEmail) {
        this.mEventEmail = mEventEmail;
    }
}
