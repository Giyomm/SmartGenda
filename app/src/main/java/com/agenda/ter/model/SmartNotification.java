package com.agenda.ter.model;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by Giyomm on 25/04/2016.
 */
public class SmartNotification {

    private int mSmartNotificationId;
    private String mSmartNotificationName;
    private int  mSmartNotificationRed;
    private int  mSmartNotificationGreen;
    private int  mSmartNotificationBlue;
    private String mSmartNotificationSound;

    public SmartNotification(int mSmartNotificationId, String mSmartNotificationName, int mSmartNotificationRed, int mSmartNotificationGreen, int mSmartNotificationBlue) {
        this.mSmartNotificationId = mSmartNotificationId;
        this.mSmartNotificationName = mSmartNotificationName;
        this.mSmartNotificationRed = mSmartNotificationRed;
        this.mSmartNotificationGreen = mSmartNotificationGreen;
        this.mSmartNotificationBlue = mSmartNotificationBlue;
    }

    public String getmSmartNotificationName() {
        return mSmartNotificationName;
    }

    public void setmSmartNotificationName(String mSmartNotificationName) {
        this.mSmartNotificationName = mSmartNotificationName;
    }

    public int getmSmartNotificationRed() {
        return mSmartNotificationRed;
    }

    public void setmSmartNotificationRed(int mSmartNotificationRed) {
        this.mSmartNotificationRed = mSmartNotificationRed;
    }

    public int getmSmartNotificationGreen() {
        return mSmartNotificationGreen;
    }

    public void setmSmartNotificationGreen(int mSmartNotificationGreen) {
        this.mSmartNotificationGreen = mSmartNotificationGreen;
    }

    public int getmSmartNotificationBlue() {
        return mSmartNotificationBlue;
    }

    public void setmSmartNotificationBlue(int mSmartNotificationBlue) {
        this.mSmartNotificationBlue = mSmartNotificationBlue;
    }

    public String getmSmartNotificationSound() {
        return mSmartNotificationSound;
    }

    public void setmSmartNotificationSound(String mSmartNotificationSound) {
        this.mSmartNotificationSound = mSmartNotificationSound;
    }

    public int getmSmartNotificationId() {
        return mSmartNotificationId;
    }

    public void setmSmartNotificationId(int mSmartNotificationId) {
        this.mSmartNotificationId = mSmartNotificationId;
    }

    @Override
    public String toString() {
        return mSmartNotificationName;
    }
}
