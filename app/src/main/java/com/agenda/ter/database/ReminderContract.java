package com.agenda.ter.database;

import android.provider.BaseColumns;

/**
 * Created by Giyomm on 26/04/2016.
 */
public final class ReminderContract {

    public ReminderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class ReminderEntry implements BaseColumns {
        public static final String TABLE_NAME = "reminder";
        public static final String COLUMN_NAME_REMINDER_ID =_ID;
        public static final String COLUMN_NAME_REMINDER_NAME = "reminder_name";
        public static final String COLUMN_NAME_REMINDER_DATE = "reminder_date";
        public static final String COLUMN_NAME_REMINDER_DISPLAY_MAP = "reminder_display_map";
        public static final String COLUMN_NAME_REMINDER_NOTIFICATION_ID = "reminder_notification_id";

        private static final String VARCHAR_TYPE = " VARCHAR";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String DATE_TYPE = " DATETIME";
        private static final String COMMA_SEP = ",";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + ReminderEntry.TABLE_NAME +
                        " (" +
                        ReminderEntry.COLUMN_NAME_REMINDER_ID + INTEGER_TYPE + " PRIMARY KEY NOT NULL," +
                        ReminderEntry.COLUMN_NAME_REMINDER_NAME + VARCHAR_TYPE + "(255)" + COMMA_SEP +
                        ReminderEntry.COLUMN_NAME_REMINDER_DATE + DATE_TYPE + COMMA_SEP +
                        ReminderEntry.COLUMN_NAME_REMINDER_DISPLAY_MAP + INTEGER_TYPE + " DEFAULT 0" +COMMA_SEP +
                        ReminderEntry.COLUMN_NAME_REMINDER_NOTIFICATION_ID + INTEGER_TYPE + COMMA_SEP +
                        "FOREIGN KEY ("+ ReminderEntry.COLUMN_NAME_REMINDER_NOTIFICATION_ID +
                        ") REFERENCES "+ NotificationContract.NotificationEntry.TABLE_NAME + "("+ NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_ID +")"+
                        " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + ReminderEntry.TABLE_NAME;

        public static String getSqlCreateEntries() {
            return SQL_CREATE_ENTRIES;
        }

        public static String getSqlDeleteEntries() {
            return SQL_DELETE_ENTRIES;
        }
    }
}
