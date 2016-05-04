package com.agenda.ter.database;

import android.provider.BaseColumns;

/**
 * Created by Giyomm on 26/04/2016.
 */
public final class EventContract {

    public EventContract() {}

    /* Inner class that defines the table contents */
    public static abstract class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "event";
        public static final String COLUMN_NAME_EVENT_ID = "event_id";
        public static final String COLUMN_NAME_EVENT_NAME = "event_name";
        public static final String COLUMN_NAME_EVENT_DATE = "event_date";
        public static final String COLUMN_NAME_EVENT_TIME = "event_time";
        public static final String COLUMN_NAME_EVENT_DESCRIPTION = "event_desc";
        public static final String COLUMN_NAME_EVENT_LOCATION_ID = "event_meteo_id";
        public static final String COLUMN_NAME_EVENT_NOTIFICATION_ID = "event_notification_id";

        private static final String VARCHAR_TYPE = " VARCHAR";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String COMMA_SEP = ",";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + EventEntry.TABLE_NAME +
                        " (" +
                            EventEntry.COLUMN_NAME_EVENT_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                            EventEntry.COLUMN_NAME_EVENT_NAME + VARCHAR_TYPE + "(255)" + COMMA_SEP +
                            EventEntry.COLUMN_NAME_EVENT_DATE + INTEGER_TYPE + COMMA_SEP +
                            EventEntry.COLUMN_NAME_EVENT_TIME + VARCHAR_TYPE + "(255)" + COMMA_SEP +
                            EventEntry.COLUMN_NAME_EVENT_DESCRIPTION + VARCHAR_TYPE + "(255)" + COMMA_SEP +
                            EventEntry.COLUMN_NAME_EVENT_LOCATION_ID + INTEGER_TYPE + COMMA_SEP +
                            EventEntry.COLUMN_NAME_EVENT_NOTIFICATION_ID + INTEGER_TYPE + COMMA_SEP +
                            "FOREIGN KEY ("+ EventEntry.COLUMN_NAME_EVENT_LOCATION_ID +
                                ") REFERENCES "+ LocationContract.LocationEntry.TABLE_NAME + "("+ LocationContract.LocationEntry.COLUMN_NAME_LOCATION_ID +")"+
                            "FOREIGN KEY ("+ EventEntry.COLUMN_NAME_EVENT_NOTIFICATION_ID +
                                ") REFERENCES "+ NotificationContract.NotificationEntry.TABLE_NAME + "("+ NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_ID +")"+
                        " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;

        public static String getSqlCreateEntries() {
            return SQL_CREATE_ENTRIES;
        }

        public static String getSqlDeleteEntries() {
            return SQL_DELETE_ENTRIES;
        }
    }

}
