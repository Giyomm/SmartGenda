package com.agenda.ter.database;

import android.provider.BaseColumns;

/**
 * Created by Giyomm on 26/04/2016.
 * Classe qui représente la table Notification dans la base de données
 */
public final class NotificationContract {

    public NotificationContract() {}

    /**
     * Classe qui définit la table Notification dans la base de données
     */
    public static abstract class NotificationEntry implements BaseColumns {
        /** Le nom de a table Notification */
        public static final String TABLE_NAME = "notification";
        /** La colone ID de la table Notification */
        public static final String COLUMN_NAME_NOTIFICATION_ID = "notification_id";
        /** La colone Nom de la table Notification */
        public static final String COLUMN_NAME_NOTIFICATION_NAME = "notification_name";
        /** La colone qui correspond au code du couleur_rouge de la Notification */
        public static final String COLUMN_NAME_NOTIFICATION_COLOR_RED = "notification_color_red";
        /** La colone qui correspond au code du couleur_Verte de la Notification */
        public static final String COLUMN_NAME_NOTIFICATION_COLOR_GREEN = "notification_color_green";
        /** La colone qui correspond au code du couleur_bleu de la Notification */
        public static final String COLUMN_NAME_NOTIFICATION_COLOR_BLUE = "notification_color_blue";
        /** La colone qui correspond au chemin de la sonnerie de la notification */
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
