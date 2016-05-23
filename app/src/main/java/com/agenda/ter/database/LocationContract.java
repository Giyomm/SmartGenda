package com.agenda.ter.database;

import android.provider.BaseColumns;

/**
 * Created by Giyomm on 26/04/2016.
 */

/**
 * Represente la table d'un lieu dans la base de données
 */
public final class LocationContract {

    public LocationContract() {}

    /* Inner class that defines the table contents */
    public static abstract class LocationEntry implements BaseColumns {
        /**
         * TABLE_NAME                           Le nom de la table
         * COLUMN_NAME_LOCATION_ID              L'identifiant d'un lieu
         * COLUMN_NAME_LOCATION_NAME            Le nom d'un lieu
         * COLUMN_NAME_METEO_TEMPERATURE        La météo d'un lieu
         * COLUMN_NAME_LOCATION_LONGITUDE       La longitude d'un lieu
         * COLUMN_NAME_LOCATION_LATITUDE        La latitude d'un lieu
         * COLUMN_NAME_METEO_ICON               L'icone d'un lieu
         */
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_NAME_LOCATION_ID = "location_id";
        public static final String COLUMN_NAME_LOCATION_NAME = "location_name";
        public static final String COLUMN_NAME_METEO_TEMPERATURE = "meteo_temperature";
        public static final String COLUMN_NAME_LOCATION_LONGITUDE = "location_longitude";
        public static final String COLUMN_NAME_LOCATION_LATITUDE = "location_latitude";
        public static final String COLUMN_NAME_METEO_ICON = "meteo_icon";

        /**
         * VARCHAR_TYPE         Represente le string varchar
         * INTEGER_TYPE         Represente le string integer
         * COMMA_SEP            Represente une virgule
         */
        private static final String VARCHAR_TYPE = " VARCHAR";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String REAL_TYPE = " REAL";
        private static final String COMMA_SEP = ",";

        /**
         * Un champ qui contient une requete pour la creation de la table location
         */
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + LocationEntry.TABLE_NAME +
                        " (" +
                        LocationEntry.COLUMN_NAME_LOCATION_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                        LocationEntry.COLUMN_NAME_LOCATION_NAME + VARCHAR_TYPE + "(255)" + COMMA_SEP +
                        LocationEntry.COLUMN_NAME_METEO_TEMPERATURE + REAL_TYPE + COMMA_SEP +
                        LocationEntry.COLUMN_NAME_LOCATION_LONGITUDE + REAL_TYPE + COMMA_SEP +
                        LocationEntry.COLUMN_NAME_LOCATION_LATITUDE + REAL_TYPE + COMMA_SEP +
                        LocationEntry.COLUMN_NAME_METEO_ICON + VARCHAR_TYPE + "(255)" +
                        " )";

        /**
         * Un champ qui contient une requete pour la suppression de la table location
         */
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME;

        public static String getSqlCreateEntries() {
            return SQL_CREATE_ENTRIES;
        }

        public static String getSqlDeleteEntries() {
            return SQL_DELETE_ENTRIES;
        }
    }
}
