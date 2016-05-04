package com.agenda.ter.database;

import android.provider.BaseColumns;

/**
 * Created by Giyomm on 26/04/2016.
 */
public final class NotificationContract {

    public NotificationContract() {}

    /* Inner class that defines the table contents */
    public static abstract class NotificationEntry implements BaseColumns {
        public static final String TABLE_NAME = "notification";
        public static final String COLUMN_NAME_NOTIFICATION_ID = "notification_id";
        public static final String COLUMN_NAME_NOTIFICATION_NAME = "notification_name";
        public static final String COLUMN_NAME_NOTIFICATION_COLOR_RED = "notification_color_red";
        public static final String COLUMN_NAME_NOTIFICATION_COLOR_GREEN = "notification_color_green";
        public static final String COLUMN_NAME_NOTIFICATION_COLOR_BLUE = "notification_color_blue";
        public static final String COLUMN_NAME_NOTIFICATION_SOUND = "notification_sound";

        private static final String VARCHAR_TYPE = " VARCHAR";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String COMMA_SEP = ",";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + NotificationEntry.TABLE_NAME +
                        " (" +
                        NotificationEntry.COLUMN_NAME_NOTIFICATION_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                        NotificationEntry.COLUMN_NAME_NOTIFICATION_NAME + VARCHAR_TYPE + "(255)" + COMMA_SEP +
                        NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_RED + INTEGER_TYPE + COMMA_SEP +
                        NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_GREEN + INTEGER_TYPE + COMMA_SEP +
                        NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_BLUE + INTEGER_TYPE + COMMA_SEP +
                        NotificationEntry.COLUMN_NAME_NOTIFICATION_SOUND + VARCHAR_TYPE + "(255)" +
                        " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + NotificationEntry.TABLE_NAME;

        public static String getSqlCreateEntries() {
            return SQL_CREATE_ENTRIES;
        }

        public static String getSqlDeleteEntries() {
            return SQL_DELETE_ENTRIES;
        }
    }
}
