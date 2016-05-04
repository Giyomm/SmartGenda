package com.agenda.ter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Giyomm on 26/04/2016.
 */
public class SmartgendaDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Smartgenda.db";

    public SmartgendaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LocationContract.LocationEntry.getSqlCreateEntries());
        db.execSQL(NotificationContract.NotificationEntry.getSqlCreateEntries());
        db.execSQL(ReminderContract.ReminderEntry.getSqlCreateEntries());
        db.execSQL(EventContract.EventEntry.getSqlCreateEntries());
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for offline data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(LocationContract.LocationEntry.getSqlDeleteEntries());
        db.execSQL(NotificationContract.NotificationEntry.getSqlDeleteEntries());
        db.execSQL(ReminderContract.ReminderEntry.getSqlDeleteEntries());
        db.execSQL(EventContract.EventEntry.getSqlDeleteEntries());
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
