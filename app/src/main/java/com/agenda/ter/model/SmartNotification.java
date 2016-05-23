package com.agenda.ter.model;

/**
 * @author Smartgenda Team
 * Classe définissant un objet Notification
 */
public class SmartNotification {

    /**
     * ID de la notification
     */
    private int mSmartNotificationId;

    /**
     * Nom de la notification
     */
    private String mSmartNotificationName;

    /**
     * Niveau de rouge de la notification
     */
    private int  mSmartNotificationRed;

    /**
     * Niveau de vert de la notification
     */
    private int  mSmartNotificationGreen;

    /**
     * Niveau de bleu de la notification
     */
    private int  mSmartNotificationBlue;

    /**
     * Son délenché par la notification
     */
    private String mSmartNotificationSound;

    /**
     * Constructeur
     * @param mSmartNotificationId
     * @param mSmartNotificationName
     * @param mSmartNotificationRed
     * @param mSmartNotificationGreen
     * @param mSmartNotificationBlue
     */
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
