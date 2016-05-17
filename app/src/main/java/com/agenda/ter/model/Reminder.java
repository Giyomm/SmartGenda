package com.agenda.ter.model;

import java.util.Date;

/**
 * Created by Giyomm on 25/04/2016.
 */
public class Reminder {

    private int mReminderId;
    private int mReminderTime;
    private Boolean mReminderDisplayMap;
    private int mReminderNotificationId;

    public Reminder(int mReminderId, int mReminderTime, int mReminderNotificationId) {
        this.mReminderId = mReminderId;
        this.mReminderTime = mReminderTime;
        this.mReminderNotificationId = mReminderNotificationId;
    }

    public int getmReminderId() {
        return mReminderId;
    }

    public void setmReminderId(int mReminderId) {
        this.mReminderId = mReminderId;
    }

    public int getmReminderTime() {
        return mReminderTime;
    }

    public void setmReminderTime(int mReminderTime) {
        this.mReminderTime = mReminderTime;
    }

    public Boolean getmReminderDisplayMap() {
        return mReminderDisplayMap;
    }

    public void setmReminderDisplayMap(Boolean mReminderDisplayMap) {
        this.mReminderDisplayMap = mReminderDisplayMap;
    }

    public int getmReminderNotificationId() {
        return mReminderNotificationId;
    }

    public void setmReminderNotificationId(int mReminderNotificationId) {
        this.mReminderNotificationId = mReminderNotificationId;
    }
}
