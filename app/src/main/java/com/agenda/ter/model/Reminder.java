package com.agenda.ter.model;

import java.util.Date;

/**
 * Created by Giyomm on 25/04/2016.
 */
public class Reminder {

    private String mReminderName;
    private Date mReminderDate;
    private Boolean mReminderDisplayMap;

    public Reminder(String mReminderName, Date mReminderDate, Boolean mReminderDisplayMap) {
        this.mReminderName = mReminderName;
        this.mReminderDate = mReminderDate;
        this.mReminderDisplayMap = mReminderDisplayMap;
    }

    public String getmReminderName() {
        return mReminderName;
    }

    public void setmReminderName(String mReminderName) {
        this.mReminderName = mReminderName;
    }

    public Date getmReminderDate() {
        return mReminderDate;
    }

    public void setmReminderDate(Date mReminderDate) {
        this.mReminderDate = mReminderDate;
    }

    public Boolean getmReminderDisplayMap() {
        return mReminderDisplayMap;
    }

    public void setmReminderDisplayMap(Boolean mReminderDisplayMap) {
        this.mReminderDisplayMap = mReminderDisplayMap;
    }
}
