package com.agenda.ter.database;

import android.provider.BaseColumns;

/**
 * Created by Giyomm on 26/04/2016.
 * Classe qui represente la table rappel dans la base de données
 */
public final class ReminderContract {

    public ReminderContract() {}

    /**
     * Classe qui définit la table Rappel dans la base de données
     */
    public static abstract class ReminderEntry implements BaseColumns {
        /** Le nom de a table Reminder */
        public static final String TABLE_NAME = "reminder";
        /**  ID de a table Reminder */
        public static final String COLUMN_NAME_REMINDER_ID = "reminder_id";
        /** Le temps du rappel*/
        public static final String COLUMN_NAME_REMINDER_TIME = "reminder_time";
        /** Pour afficher ou pas la carte Google Map*/
        public static final String COLUMN_NAME_REMINDER_DISPLAY_MAP = "reminder_display_map";
        /** Clé étrangére de la notification liée a ce rappel */
        public static final String COLUMN_NAME_REMINDER_NOTIFICATION_ID = "reminder_notification_id";

        private static final String INTEGER_TYPE = " INTEGER";
        private static final String COMMA_SEP = ",";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + ReminderEntry.TABLE_NAME +
                        " (" +
                        ReminderEntry.COLUMN_NAME_REMINDER_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                        ReminderEntry.COLUMN_NAME_REMINDER_TIME + INTEGER_TYPE + COMMA_SEP +
                        ReminderEntry.COLUMN_NAME_REMINDER_DISPLAY_MAP + INTEGER_TYPE + " DEFAULT 0" +COMMA_SEP +
                        ReminderEntry.COLUMN_NAME_REMINDER_NOTIFICATION_ID + INTEGER_TYPE + COMMA_SEP +
                        "FOREIGN KEY ("+ ReminderEntry.COLUMN_NAME_REMINDER_NOTIFICATION_ID +
                        ") REFERENCES "+ NotificationContract.NotificationEntry.TABLE_NAME + "("+ NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_ID +")"+
                        " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + ReminderEntry.TABLE_NAME;

        /**
         *
         * @return Touts les champs de la table crée
         */
        public static String getSqlCreateEntries() {
            return SQL_CREATE_ENTRIES;
        }

        /**
         *
         * @return Le nom de la table supprimée
         */
        public static String getSqlDeleteEntries() {
            return SQL_DELETE_ENTRIES;
        }
    }
}
