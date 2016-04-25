package com.agenda.ter.model;

import java.util.Date;

/**
 * Created by Giyomm on 25/04/2016.
 */
public class Event {

    private String mEventName;
    private Date mEventDate;
    private String mEventDescription;
    private Meteo mEventMeteo;
    private SmartNotification mEventNotification;

    public Event(String mEventName, Date mEventDate, String mEventDescription, Meteo mEventMeteo, SmartNotification mEventNotification) {
        this.mEventName = mEventName;
        this.mEventDate = mEventDate;
        this.mEventDescription = mEventDescription;
        this.mEventMeteo = mEventMeteo;
        this.mEventNotification = mEventNotification;
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

    public String getmEventDescription() {
        return mEventDescription;
    }

    public void setmEventDescription(String mEventDescription) {
        this.mEventDescription = mEventDescription;
    }

    public Meteo getmEventMeteo() {
        return mEventMeteo;
    }

    public void setmEventMeteo(Meteo mEventMeteo) {
        this.mEventMeteo = mEventMeteo;
    }

    public SmartNotification getmEventNotification() {
        return mEventNotification;
    }

    public void setmEventNotification(SmartNotification mEventNotification) {
        this.mEventNotification = mEventNotification;
    }
}
