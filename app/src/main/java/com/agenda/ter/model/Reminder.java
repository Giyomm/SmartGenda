package com.agenda.ter.model;

/**
 * @author Smartgenda Team
 * Classe définissant un Objet Rappel
 */
public class Reminder {

    /**ID du Rappel*/
    private int mReminderId;

    /**Heure du Rappel*/
    private int mReminderTime;

    /**Le rappel affiche t'il la carte ou non*/
    private Boolean mReminderDisplayMap;

    /**Notification à laquelle est associé le Rappel*/
    private int mReminderNotificationId;

    /**
     * Constructeur
     * @param mReminderId
     * @param mReminderTime
     * @param mReminderNotificationId
     */
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
