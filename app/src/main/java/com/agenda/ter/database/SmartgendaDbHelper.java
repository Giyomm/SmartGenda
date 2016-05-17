package com.agenda.ter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.agenda.ter.smartgenda.NotificationActivity;

/**
 * Created by Giyomm on 26/04/2016.
 */
public class SmartgendaDbHelper extends SQLiteOpenHelper {
    private String rappelTime;
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
        // Insertion des deux types par defaut de notification
       /* String notifNormale = "insert into notification values ('Normale',58,157,35,'')";
        String notifFrequante = "insert into notification values ('Trés fréquante',255,0,0,'')";
        db.execSQL(notifNormale);
        db.execSQL(notifFrequante);*/

        ContentValues values1 = new ContentValues();
        values1.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_NAME,"Normale");
        values1.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_RED,58);
        values1.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_GREEN,157);
        values1.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_BLUE,35);
        values1.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_SOUND,"");
        long newRowId1;
        newRowId1 = db.insert(NotificationContract.NotificationEntry.TABLE_NAME, null, values1);
        // insertion du rappel

        String rappel1_1 = "insert into reminder values (0,21600000,0, "+newRowId1+")" ; //6heures
        String rappel1_2 = "insert into reminder values (1,86400000,0, "+newRowId1+")" ; //24heures
        String rappel1_21 = "insert into reminder values (2,60000,0, "+newRowId1+")" ; //1 min
        String rappel1_22 = "insert into reminder values (3,120000,0, "+newRowId1+")" ; //2 min
        db.execSQL(rappel1_1); db.execSQL(rappel1_2);

        ContentValues values2 = new ContentValues();
        values2.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_NAME,"Trés fréquente");
        values2.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_RED,255);
        values2.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_GREEN,0);
        values2.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_BLUE,0);
        values2.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_SOUND,"");
        long newRowId2;
        newRowId2 = db.insert(NotificationContract.NotificationEntry.TABLE_NAME, null, values2);
        // insertion du rappel
        String rappel2 = "insert into reminder values (4,10800000,0, "+newRowId2+")" ;   // 3heure
        String rappel3 = "insert into reminder values (5,21600000,0, "+newRowId2+")" ;   // 6heure
        String rappel4 = "insert into reminder values (6,43200000 ,0, "+newRowId2+")" ; // 12heure
        String rappel5 = "insert into reminder values (7,86400000 ,0, "+newRowId2+")" ; // 24heure
        String rappel6 = "insert into reminder values (8,3600000 ,0, "+newRowId2+")" ; // 1heure

        db.execSQL(rappel2);db.execSQL(rappel3);db.execSQL(rappel4);db.execSQL(rappel5);db.execSQL(rappel6);

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
