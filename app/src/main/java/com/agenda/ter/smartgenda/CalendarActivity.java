package com.agenda.ter.smartgenda;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agenda.ter.database.EventContract;
import com.agenda.ter.database.LocationContract;
import com.agenda.ter.database.SmartgendaDbHelper;
import com.agenda.ter.model.Event;
import com.agenda.ter.model.Location;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Smartgenda Team
 * Activité de la Page principale de l'application. Controller chargé de la gestion du Calendrier.
 */
public class CalendarActivity extends AppCompatActivity {

    /**Extra du jour sélectionné par l'utilisateur sur le calendrier */
    public final static String EXTRA_SELECTED_DATE = "com.agenda.ter.smartgenda.SELECTED_DATE";
    //false -> Mode ajout || true -> Mode edition

    /**Extra représentant si le mode dans lequel la Page EventActivty est appelée*/
    public final static String EXTRA_EVENT_MODE = "com.agenda.ter.smartgenda.EVENT_MODE";

    /**Extra de l'ID de l'évènement*/
    public final static String EXTRA_EVENT_ID = "com.agenda.ter.smartgenda.EVENT_ID";

    /**Extra de l'arlarm associé à l'évènement*/
    public static final String EXTRA_ALARM_RECEIVER_EVENT_ID = "com.agenda.ter.ALARM_EVENT_ID";

    /**L'élèment Calendrier à afficher*/
    private com.agenda.ter.smartgenda.CalendarView mCalendarView;

    /**Instance de l'helper de la base de données*/
    private SmartgendaDbHelper dbHelper;

    /**List des évènement prévu pour un jour*/
    private ArrayList<Event> eventDayList;

    /**Liste des prochains évènements du mois. Seul le plus récent est affiché*/
    private ArrayList<Event> nextEventList;

    /**TextView de l'encadré "Prochain évènement"*/
    private TextView nextEventName, nextEventDate, nextEventHour, nextEventLocation, nextEventTemperature;

    /**Bouton de l'encadré "Prochin évènement" permettant d'afficher le trajet le lieu d'un évènement*/
    private Button nextEventButtonMaps;

    /**ImageView de l'encadré "Prochain évènement" dans lequel sera afficher l'imcon de la météo*/
    private ImageView nextEventIcon;

    /**Prochain évènement à venir*/
    private Event minEvent;

    /**Coordonnées GPS (Latitude, Longitude) du prochain évènement à venir*/
    private double nextEventLatitude, nextEventLongitude;

    /**Objet Location d'Android pour le calcul du Trajet à partir de la position de l'utilisateur*/
    private android.location.Location location_user;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        nextEventName = (TextView) findViewById(R.id.calendar_next_name_id);
        nextEventName.setText(R.string.calendar_next_event_no_event);
        nextEventDate = (TextView) findViewById(R.id.calendar_next_date_id);
        nextEventHour = (TextView) findViewById(R.id.calendar_next_hour_id);
        nextEventLocation = (TextView) findViewById(R.id.calendar_next_location_id);
        nextEventTemperature = (TextView) findViewById(R.id.calendar_next_temperature_id);
        nextEventButtonMaps = (Button) findViewById(R.id.calendar_next_button_maps_id);
        nextEventIcon = (ImageView) findViewById(R.id.calendar_next_icon_id);

        dbHelper = new SmartgendaDbHelper(getApplicationContext());
        eventDayList = new ArrayList<>();
        nextEventList = new ArrayList<>();

        mCalendarView = ((com.agenda.ter.smartgenda.CalendarView) findViewById(R.id.calendar_view));
        new GetAllEventTask(this).execute();

        // assign event handler
        mCalendarView.setEventHandler(new com.agenda.ter.smartgenda.CalendarView.EventHandler() {
            @Override
            public void onDayLongPress(Date date) {
                SimpleDateFormat sdf_DATE = new SimpleDateFormat("dd/MM/yyyy");
                Date dateParse = null;
                try {
                    dateParse = sdf_DATE.parse(new SimpleDateFormat("dd/MM/yyyy").format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                new GetEventTask(CalendarActivity.this).execute("" + dateParse.getTime());
            }
        });

        nextEventButtonMaps.setVisibility(View.GONE);
        new GetNextEventTask(CalendarActivity.this).execute();

        if (!checkNetworkConnection()) {
            //Toast.makeText(this,"Veuillez activer votre connection internet !",Toast.LENGTH_SHORT).show();
            final WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
            new AlertDialog.Builder(this)
                    .setTitle("Internet")
                    .setMessage("Voulez vous activer votre wifi ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            wifiManager.setWifiEnabled(true);
                            Intent in = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(in);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        //CHECK GPS POUR LE TRAJET
        if(!checkGPSConnection()){
            Toast.makeText(this,"GPS non activé",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetAllEventTask(this).execute();
        new GetNextEventTask(this).execute();
    }

    /**
     * Vérifie que le GPS de l'appareil est en marche
     * @return True si le GPS fonctionne, False sinon
     */
    public Boolean checkGPSConnection(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //Do what you need if enabled...
            gps = true;
        }else{
            //Do what you need if not enabled...
            gps = false;
        }
        return gps;
    }

    /**
     * Vérifie que la connection au réseau est disponible
     * @return True si la connection est disponible, False sinon
     */
    public Boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Calcul le trajet de la position de l'utilsateur jusqu'au lieu de l'évènement
     * @param v Le bouton qui appelle cette méthode
     */
    public void findPath(View v) {

        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location_user = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            Intent navigation = new Intent(Intent.ACTION_VIEW, Uri
                    .parse("http://maps.google.com/maps?saddr="
                            + location_user.getLatitude() + ","
                            + location_user.getLongitude() + "&daddr="
                            + nextEventLatitude + "," + nextEventLongitude));
            navigation.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(navigation);
        }catch (Exception e){
            Toast.makeText(this,"GPS non activé",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Affiche une AlerteDialog après un appuie long sur une case du calendrier. Celle-ci affiche la date du jour sélectionné ainsi qu'une liste des évènements prévu pour ce jour.
     * @param selectedDate La date du jour sélectionné
     */
    private void showDayDialog(final Date selectedDate) {
        AlertDialog.Builder eventDialogBuilder = new AlertDialog.Builder(CalendarActivity.this);
        eventDialogBuilder.setTitle(new SimpleDateFormat("dd/MM/yyyy").format(selectedDate));
        eventDialogBuilder.setMessage("Créez, modifiez ou supprimez une tâche.");
        eventDialogBuilder.setIcon(R.mipmap.ic_app);

        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.calendar_event_list, null);
        eventDialogBuilder.setView(convertView);

        ListView events_list_view = (ListView) convertView.findViewById(R.id.event_list_day_events);
        EventListAdapter customAdapter = new EventListAdapter(this,R.layout.calendar_list_item_row,eventDayList);
        events_list_view.setAdapter(customAdapter);

        eventDialogBuilder.setNegativeButton(
                "Retour",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        eventDialogBuilder.setPositiveButton(
                "Ajoutez une tâche",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CalendarActivity.this, EventActivity.class);
                        intent.putExtra(EXTRA_SELECTED_DATE,selectedDate.getTime());
                        intent.putExtra(EXTRA_EVENT_MODE,false);
                        startActivity(intent);
                    }
                });

        eventDialogBuilder.show();
    }

    /**
     * Permet d'afficher la Page EventActivity après avoir appuyé sur le bouton d'ajout de tâche en haut à droite de la page
     * @param view Le bouton appellant cette méthode
     */
    public void goToEventActivity(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra(EXTRA_EVENT_MODE,false);
        startActivity(intent);
    }

    /**
     * Ajoute un évènement dans la liste d'évènements de l'objet Calendrier
     * @param e L'évènement à ajouter
     */
    public void insertEventInListSet(Event e){
        ArrayList<Event> tmpHash = mCalendarView.getEventListSet();
        tmpHash.add(e);
        mCalendarView.setEventListSet(tmpHash);
    }

    /**
     * Remplis les champs textes de l'encadré "Prochain évènement" avec les informations du prochain évènement à venir
     * @param eventList La liste des évènements à venir
     */
    public void DisplayNextEvent(ArrayList<Event> eventList){
        long minDate = Long.MAX_VALUE;
        minEvent = new Event();
        for (Event e : eventList) {
            if (e.getmEventDate().getTime() < minDate){
                minDate = e.getmEventDate().getTime();
                minEvent = e;
            }else if(e.getmEventDate().getTime()==minDate){
                String[] hour1 = e.getmEventTime().split(":");
                String[] hour2 = minEvent.getmEventTime().split(":");

                if(Integer.valueOf(hour1[0]) < Integer.valueOf(hour2[0]) ){
                    minEvent = e;
                }else if (Integer.valueOf(hour1[0]) == Integer.valueOf(hour2[0])){
                    if(Integer.valueOf(hour1[1]) < Integer.valueOf(hour2[1]) )
                        minEvent = e;
                }
            }

        }
        nextEventName.setText(minEvent.getmEventName().toString());
        nextEventHour.setText(minEvent.getmEventTime().toString());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(minEvent.getmEventDate());
        nextEventDate.setText(dateString);
        nextEventButtonMaps.setVisibility(View.VISIBLE);
        new GetLocationTask(this).execute(minEvent.getmEventLocationId()+"");
    }

    /**
     * Vide les champs texte de l'encadré "Prochan évènement" et affiche "Aucun évènement de prévu"
     */
    public void resetNextEventView(){
        nextEventName.setText(R.string.calendar_next_event_no_event);
        nextEventHour.setText("");
        nextEventDate.setText("");
        nextEventButtonMaps.setVisibility(View.GONE);
        nextEventLocation.setText("");
        nextEventTemperature.setText("");
        nextEventIcon.setImageBitmap(null);
    }

    /**
     * Remplis les champs textes de l'encadré "Prochain évènement" avec les informations du lieu du prochain évènement à venir
     * @param location Le lieu de prochain évènement
     */
    public void fillNextEventLocation(Location location){
        nextEventLocation.setText(location.getmLocationName());
        nextEventTemperature.setText(location.getmMeteoTemperature()+" °C");
        new ImageLoadTask(location.getmMeteoIcon(),nextEventIcon).execute();
        nextEventLatitude = location.getmLocationLatitude();
        nextEventLongitude = location.getmLocationLongitude();
    }

    /*LIST ADAPTER*/

    /**
     * Adapter personnalisé pour remplir la liste des évènements d'un jour
     */
    public class EventListAdapter extends ArrayAdapter<Event>{

        public EventListAdapter(Context context, int resource, List<Event> items) {
            super(context, resource, items);
        }

        public View getView(final int position, View convertView, final ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.calendar_list_item_row, null);
            }

            final Event e = getItem(position);

            TextView event_name = (TextView) v.findViewById(R.id.calendar_event_list_name_text_View_id);
            TextView event_desc = (TextView) v.findViewById(R.id.calendar_event_list_desc_text_View_id);
            TextView event_time = (TextView) v.findViewById(R.id.calendar_event_list_desc_time_View_id);

            Button edit_button = (Button) v.findViewById(R.id.calendar_event_list_modify_button_id);
            Button delete_button = (Button) v.findViewById(R.id.calendar_event_list_delete_button_id);

            event_name.setText(e.getmEventName());

            if(!e.getmEventDescription().equals(""))
                event_desc.setText(e.getmEventDescription());

            event_time.setText(e.getmEventTime());

            delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = e.getmEventId();
                    int loc_id = e.getmEventLocationId();
                    String [] _idStr = {""+id,""+loc_id};

                    //remove from database (and the hash set)
                    new DeleteEventTask(CalendarActivity.this).execute(_idStr);

                    eventDayList.remove(e);
                    notifyDataSetChanged();
                    new GetNextEventTask(CalendarActivity.this).execute();
                }
            });

            edit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = e.getmEventId();
                    Intent intent = new Intent(CalendarActivity.this, EventActivity.class);
                    intent.putExtra(EXTRA_EVENT_MODE,true);
                    intent.putExtra(EXTRA_EVENT_ID,id);
                    startActivity(intent);
                }
            });

            return v;
        }
    }

    /*CLASSES AYNCHRONES POUR REQUÊTES HTTP et SQL*/

    /**
     * Classe de la tâche asynchrone chargée d'exécuter la requête SQL permettant de récupérer un évènement à partir de son ID
     */
    public class GetEventTask extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        public GetEventTask(CalendarActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            eventDayList.clear();
            dialog.setMessage("Récupération des données");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            //SELECT * FROM event
            String[] projection = {
                    EventContract.EventEntry.COLUMN_NAME_EVENT_ID,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_NAME,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_DESCRIPTION,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_DATE,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_TIME,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_LOCATION_ID,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_NOTIFICATION_ID,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_EMAIL
            };

            /*(WHERE Date = date_of_the_day)*/
            //WHERE clause column
             String selection = EventContract.EventEntry.COLUMN_NAME_EVENT_DATE + "=?";

            //WHERE clause values
            String[] selectionArgs = {params[0]};


            Cursor queryResult = db.query(
                    EventContract.EventEntry.TABLE_NAME,      // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // The sort order
            );

            while (queryResult.moveToNext()) {
                int _id = queryResult.getInt(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_ID));
                String _name = queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_NAME));
                String _email = queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_EMAIL));
                Date _date = new Date(Long.valueOf(queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_DATE))));
                String _time = queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_TIME));
                String _desc = queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_DESCRIPTION));
                int _location_id = queryResult.getInt(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_LOCATION_ID));
                int _notification_id = queryResult.getInt(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_NOTIFICATION_ID));

                Event e = new Event(_id,_name,_date,_time,_desc,_location_id,_notification_id,_email);
                eventDayList.add(e);
            }

            queryResult.close();
            return params[0];
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            showDayDialog(new Date(Long.valueOf(aVoid)));
            dbHelper.close();
        }
    }

    /**
     * Classe de la tâche asynchrone chargée d'exécuter la requête SQL permettant de supprimer un évènement à partir de son ID
     */
    public class DeleteEventTask extends AsyncTask<String, Void, Void>{

        private ProgressDialog dialog;
        private CalendarActivity context;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        public DeleteEventTask(CalendarActivity activity) {
            this.context = activity;
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Suppression en cours...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            // DELETE Event WHERE Event.id = params[0]
            String selection = EventContract.EventEntry.COLUMN_NAME_EVENT_ID + " LIKE ?";
            String[] selectionArgs = {params[0]};
            db.delete(EventContract.EventEntry.TABLE_NAME, selection, selectionArgs);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dbHelper.close();
            dialog.dismiss();
            new GetAllEventTask(context).execute();
        }
    }

    /**
     * Classe de la tâche asynchrone chargée d'exécuter la requête SQL permettant de récupérer tous les évènements
     */
    public class GetAllEventTask extends AsyncTask<String, Void, Void>{

        private ProgressDialog dialog;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        public GetAllEventTask(CalendarActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Récupération des données");
            dialog.show();
            mCalendarView.getEventListSet().clear();
        }

        @Override
        protected Void doInBackground(String... params) {
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            //SELECT * FROM event
            String[] projection = {
                    EventContract.EventEntry.COLUMN_NAME_EVENT_ID,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_NAME,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_DESCRIPTION,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_DATE,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_TIME,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_LOCATION_ID,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_NOTIFICATION_ID,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_EMAIL
            };

            Cursor queryResult = db.query(
                    EventContract.EventEntry.TABLE_NAME,      // The table to query
                    projection,                               // The columns to return
                    null,                                     // The columns for the WHERE clause
                    null,                                     // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // The sort order
            );

            while (queryResult.moveToNext()) {
                int _id = queryResult.getInt(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_ID));
                String _name = queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_NAME));
                String _email = queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_EMAIL));
                Date _date = new Date(Long.valueOf(queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_DATE))));
                String _time = queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_TIME));
                String _desc = queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_DESCRIPTION));
                int _location_id = queryResult.getInt(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_LOCATION_ID));
                int _notification_id = queryResult.getInt(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_NOTIFICATION_ID));

                Event e = new Event(_id,_name,_date,_time,_desc,_location_id,_notification_id,_email);
                insertEventInListSet(e);
            }
            queryResult.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            dbHelper.close();
            mCalendarView.updateCalendar();
        }
    }

    /**
     * Classe de la tâche asynchrone chargée d'exécuter la requête SQL permettant de récupérer le prochain évènement à venir
     */
    public class GetNextEventTask extends AsyncTask<String, Void, Void>{

        private ProgressDialog dialog;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        public GetNextEventTask(CalendarActivity activity) {
            dialog = new ProgressDialog(activity);
            nextEventList.clear();
        }

        @Override
        protected Void doInBackground(String... params) {
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            //SELECT * FROM event
            String[] projection = {
                    EventContract.EventEntry.COLUMN_NAME_EVENT_ID,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_NAME,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_DESCRIPTION,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_DATE,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_TIME,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_LOCATION_ID,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_NOTIFICATION_ID,
                    EventContract.EventEntry.COLUMN_NAME_EVENT_EMAIL
            };


            Cursor queryResult = db.query(
                    EventContract.EventEntry.TABLE_NAME,      // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                                     // The values for the WHERE clause
                    null,                                       // group the rows
                    null,                                     // don't filter by row groups
                    null                                      // The sort order
            );

            while (queryResult.moveToNext()) {
                int _id = queryResult.getInt(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_ID));
                String _name = queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_NAME));
                String _email = queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_EMAIL));
                Date _date = new Date(Long.valueOf(queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_DATE))));
                String _time = queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_TIME));
                String _desc = queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_DESCRIPTION));
                int _location_id = queryResult.getInt(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_LOCATION_ID));
                int _notification_id = queryResult.getInt(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_NOTIFICATION_ID));

                Event e = new Event(_id,_name,_date,_time,_desc,_location_id,_notification_id,_email);
                nextEventList.add(e);
            }
            queryResult.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(nextEventList.size()>0){
                DisplayNextEvent(nextEventList);
            }
            else{
                resetNextEventView();
            }
        }


    }

    /**
     * Classe de la tâche asynchrone chargée d'exécuter la requête SQL permettant de récupérer un objet Location à partir de son ID
     */
    public class GetLocationTask extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;
        private Location location;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        public GetLocationTask(CalendarActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            dialog.setMessage("Récupération des données");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            //SELECT * FROM Location
            String[] projection = {
                    LocationContract.LocationEntry.COLUMN_NAME_LOCATION_ID,
                    LocationContract.LocationEntry.COLUMN_NAME_LOCATION_NAME,
                    LocationContract.LocationEntry.COLUMN_NAME_METEO_TEMPERATURE,
                    LocationContract.LocationEntry.COLUMN_NAME_LOCATION_LONGITUDE,
                    LocationContract.LocationEntry.COLUMN_NAME_LOCATION_LATITUDE,
                    LocationContract.LocationEntry.COLUMN_NAME_METEO_ICON
            };

            /*(WHERE id = _id_)*/
            //WHERE clause column
            String selection = LocationContract.LocationEntry.COLUMN_NAME_LOCATION_ID+ "=?";

            //WHERE clause values
            String[] selectionArgs = {params[0]};


            Cursor queryResult = db.query(
                    LocationContract.LocationEntry.TABLE_NAME,      // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // The sort order
            );

            while (queryResult.moveToNext()) {
                int _id = queryResult.getInt(queryResult.getColumnIndex(LocationContract.LocationEntry.COLUMN_NAME_LOCATION_ID));
                String _name = queryResult.getString(queryResult.getColumnIndex(LocationContract.LocationEntry.COLUMN_NAME_LOCATION_NAME));
                float _temperature = queryResult.getFloat(queryResult.getColumnIndex(LocationContract.LocationEntry.COLUMN_NAME_METEO_TEMPERATURE));
                double _latitude = queryResult.getDouble(queryResult.getColumnIndex(LocationContract.LocationEntry.COLUMN_NAME_LOCATION_LATITUDE));
                double _longitude = queryResult.getDouble(queryResult.getColumnIndex(LocationContract.LocationEntry.COLUMN_NAME_LOCATION_LONGITUDE));
                String _icon = queryResult.getString(queryResult.getColumnIndex(LocationContract.LocationEntry.COLUMN_NAME_METEO_ICON));


                location = new Location(_id,_name,_temperature,_latitude,_longitude, _icon);
            }

            queryResult.close();
            return location.getmLocationName();
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            dbHelper.close();
            fillNextEventLocation(location);
        }
    }

    /**
     * Classe de la tâche asynchrone chargée d'exécuter la requête HTTP permettant de remplir l'ImageView avec l'url d'une image passée en paramêtre
     */
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

}
