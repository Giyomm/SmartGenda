package com.agenda.ter.model;

import java.util.Date;

/**
 * Created by Giyomm on 25/04/2016.
 */
public class Event {

    private int mEventId;
    private String mEventName;
    private String mEventEmail;
    private Date mEventDate;
    private String mEventTime;
    private String mEventDescription;
    private int mEventLocationId;
    private int mEventNotificationId;

    public Event() {
    }

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
