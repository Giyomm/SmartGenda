package com.agenda.ter.model;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by Giyomm on 25/04/2016.
 */
public class SmartNotification {

    private String mSmartNotificationName;
    private Color mSmartNotificationColor;
    private String mSmartNotificationSound;
    private ArrayList<Reminder> mReminderList;

    public SmartNotification(String mSmartNotificationName, Color mSmartNotificationColor, String mSmartNotificationSound) {
        this.mSmartNotificationName = mSmartNotificationName;
        this.mSmartNotificationColor = mSmartNotificationColor;
        this.mSmartNotificationSound = mSmartNotificationSound;
        this.mReminderList = new ArrayList<>();
    }

    public String getmSmartNotificationName() {
        return mSmartNotificationName;
    }

    public void setmSmartNotificationName(String mSmartNotificationName) {
        this.mSmartNotificationName = mSmartNotificationName;
    }

    public Color getmSmartNotificationColor() {
        return mSmartNotificationColor;
    }

    public void setmSmartNotificationColor(Color mSmartNotificationColor) {
        this.mSmartNotificationColor = mSmartNotificationColor;
    }

    public String getmSmartNotificationSound() {
        return mSmartNotificationSound;
    }

    public void setmSmartNotificationSound(String mSmartNotificationSound) {
        this.mSmartNotificationSound = mSmartNotificationSound;
    }

    public ArrayList<Reminder> getmReminderList() {
        return mReminderList;
    }

    public void setmReminderList(ArrayList<Reminder> mReminderList) {
        this.mReminderList = mReminderList;
    }
}
