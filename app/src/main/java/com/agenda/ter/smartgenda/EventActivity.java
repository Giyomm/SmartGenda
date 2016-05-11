package com.agenda.ter.smartgenda;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.DialogFragment;
import android.widget.TimePicker;
import android.widget.Toast;

import com.agenda.ter.database.EventContract;
import com.agenda.ter.database.LocationContract;
import com.agenda.ter.database.NotificationContract;
import com.agenda.ter.database.SmartgendaDbHelper;
import com.agenda.ter.model.Event;
import com.agenda.ter.model.Location;
import com.agenda.ter.model.SmartNotification;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventActivity extends AppCompatActivity {
    //MES***
    private String idLocation;
    private String iconPath;

    public DatePickerDialog dpd;
    private EditText nameEvent;
    private TextView locationLatLngTextView;
    private EditText desc_even;
    private static TextView hour_picked_text_view;
    public static TextView datepickertxtview;
    private TextView meteoPicked;
    private ImageView iconMeteo;
    private TextView locationName;
    private EditText emailEditText;
    private Spinner notificationSpinner;
    private Spinner spinnerEmail;

    private ProgressBar meteoPbar;

    Intent intentFromCalendar;
    private Boolean eventMode;
    private int editEventId;
    private Location mEditLocation;

    private Location location;

    //LAT ET LON DE L EVENEMENT
    double latitudeEvent;
    double longitudeEvent;
    String latitudeCity, longitudeCity;
    public long dateSelected;

    // HEURE ET MINUTE DU SYS
    private int hourSys, minuteSys;

    //LES EXTRAS
    public static final String EXTRA_LONGITUDE = "com.agenda.ter.LONG";
    public static final String EXTRA_LATITUDE = "com.agenda.ter.LAT";
    public static final String EXTRA_LOCALISATION_NAME = "com.agenda.ter.LOCATION_NAME";

    //DATABASE
    private SmartgendaDbHelper dbHelper;

    ArrayList<SmartNotification> listNotif = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // enlever le focus ***MES
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        dbHelper = new SmartgendaDbHelper(getApplicationContext());

        Log.d("NOTIF","ON CREATE");
        new GetSmartNotifTask(this).execute();

        desc_even = (EditText) findViewById(R.id.desc_even_edittextt_id);
        nameEvent = (EditText) findViewById(R.id.nom_even_edittext_id);
        locationLatLngTextView = (TextView) findViewById(R.id.event_location_texteview_id);
        datepickertxtview = (TextView) findViewById(R.id.datepicker_textview_id);
        hour_picked_text_view = (TextView) findViewById(R.id.event_hourpicker_textview_id);
        notificationSpinner = (Spinner) findViewById(R.id.spinner);
        meteoPbar = (ProgressBar)findViewById(R.id.event_weather_progressbar_id);
        meteoPicked = (TextView)findViewById(R.id.event_weatherpicked_textview_id);
        iconMeteo = (ImageView)findViewById(R.id.event_weather_icon_id);
        locationName = (TextView) findViewById(R.id.lieu_event_textview_id);
        spinnerEmail = (Spinner)findViewById(R.id.event_spinner_email);

        //CACHER PROGRESSBAR
        meteoPbar.setVisibility(View.GONE);

        Calendar calendar = Calendar.getInstance();
        dpd = new DatePickerDialog(EventActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if(dayOfMonth<=9 && (monthOfYear+1)<=9){
                    datepickertxtview.setText("0"+dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year);
                }else if(dayOfMonth<=9 && (monthOfYear+1)>9){
                    datepickertxtview.setText("0"+dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                }else if(dayOfMonth>9 && (monthOfYear+1)<=9){
                    datepickertxtview.setText(dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year);
                }else {
                    datepickertxtview.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                }


            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        DatePicker dp = dpd.getDatePicker();
        dp.setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH,Integer.MAX_VALUE);

        // RECUPERER LA DATE DEPUIS CALENDAR ACTIVITY
        intentFromCalendar = getIntent();
        eventMode = intentFromCalendar.getBooleanExtra(CalendarActivity.EXTRA_EVENT_MODE,false);

        //Mode Edition
        if(eventMode){
            editEventId = intentFromCalendar.getIntExtra(CalendarActivity.EXTRA_EVENT_ID,0);
            new GetEventTask(EventActivity.this).execute(""+editEventId);
        }
        //Mode Ajout
        else{
            dateSelected = intentFromCalendar.getLongExtra(CalendarActivity.EXTRA_SELECTED_DATE, 0);
            if(dateSelected!= 0){
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = formatter.format(new Date(dateSelected));
                datepickertxtview.setText(dateString);
            }
        }
        SpinnerEmail();
    }


    //Recuperer les emails du terminal et remplir le spinner avec les emails trouvés
    public void SpinnerEmail(){
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(this).getAccounts();
        ArrayList<String> emailList = new ArrayList<>();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                emailList.add(possibleEmail);
            }
        }
        //Remplir le spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, emailList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEmail.setAdapter(arrayAdapter);
    }

    public String Stream2String(InputStream inputStream) {
        BufferedReader bureader=new BufferedReader( new InputStreamReader(inputStream));
        String line ;
        String Text="";
        try{
            while((line=bureader.readLine())!=null) {
                Text+=line;
            }
            inputStream.close();
        }catch (Exception ex){}
        return Text;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            latitudeEvent = data.getDoubleExtra(EXTRA_LATITUDE,0);
            longitudeEvent = data.getDoubleExtra(EXTRA_LONGITUDE,0);
            String locationName = data.getStringExtra(EXTRA_LOCALISATION_NAME);
            location = new Location(locationName,latitudeEvent,longitudeEvent);
            Log.d("LAT ET LONG","LAT: "+latitudeEvent+" LONG: "+longitudeEvent+ " NAME / "+locationName);
            try
            {

                longitudeCity=String.valueOf(longitudeEvent);
                latitudeCity=String.valueOf(latitudeEvent);
                Log.d("LAT ET LON STRING", "LAT / "+latitudeCity+ " LONG / "+longitudeCity);
                Log.d("LAT ET LON DOUBLE", "LAT / "+latitudeEvent+ " LONG / "+longitudeEvent);
                String myurl = "http://www.prevision-meteo.ch/services/json/lat="+latitudeCity+"lng="+longitudeCity;
                changeProgressBarVisibility();
                new  MyAsyncTaskgetNews().execute(myurl);

            }catch (Exception ex){}

            locationLatLngTextView.setText(locationName+"");
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Modifications non sauvegardées")
                .setMessage("Voulez-vous quitter sans sauvegarder?")
                .setNegativeButton("Non", null)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();
    }

    private void fillEventActivityFields(Event event) {
        nameEvent.setText(event.getmEventName());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(event.getmEventDate());
        datepickertxtview.setText(dateString);
        hour_picked_text_view.setText(event.getmEventTime());
        desc_even.setText(event.getmEventDescription());
        new GetLocationTask(this).execute(""+event.getmEventLocationId());
        //TODO Get SmartNotification by ID (AsyncTask)
    }

    private void fillLocationNameInEditMode(Location loc){
        locationLatLngTextView.setText(loc.getmLocationName());
        meteoPicked.setText(loc.getmMeteoTemperature()+"° C");
        changeProgressBarVisibility();
        new ImageLoadTask(loc.getmMeteoIcon(), iconMeteo).execute();
    }

    public void showDatePicker(View view) {
        dpd.show();
    }

    public void goToMapsActivity(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent,1);
    }

    public void goToNotificationPage(View view) {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    public void changeProgressBarVisibility(){
        if(meteoPbar.getVisibility() == View.VISIBLE){
            meteoPbar.setVisibility(View.GONE);
        }
        else
            meteoPbar.setVisibility(View.VISIBLE);
    }

    public void populateSpinner(ArrayList<SmartNotification> list){
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<SmartNotification> adapter = new ArrayAdapter<SmartNotification>(this,R.layout.support_simple_spinner_dropdown_item,list);
        // Apply the adapter to the spinner
        notificationSpinner.setAdapter(adapter);
    }

    public String getSelectedSmartNotifId(){
        int id = ((SmartNotification)notificationSpinner.getSelectedItem()).getmSmartNotificationId();
        return String.valueOf(id);
    }

    //AFFICHER LE TIME PICKER
    public void showTimePicker(View v){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(),"TimePicker");
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        private int hourSys,minuteSys;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            //Use the current time as the default values for the time picker
            final Calendar c = Calendar.getInstance();
            hourSys = c.get(Calendar.HOUR_OF_DAY);
            minuteSys = c.get(Calendar.MINUTE);

            //Create and return a new instance of TimePickerDialog
            return new TimePickerDialog(getActivity(),this, hourSys, minuteSys, DateFormat.is24HourFormat(getActivity()));

        }

        //onTimeSet() callback method
        public void onTimeSet(TimePicker view, int hourOfDay, int minute){
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = formatter.format(new Date(System.currentTimeMillis()));

            if(hourOfDay<hourSys && EventActivity.datepickertxtview.getText().toString().equals(dateString)) {
                Toast.makeText(getActivity(), "Veuillez choisir une heure convenable !", Toast.LENGTH_SHORT).show();
            }else{
                if (hourOfDay <= 9 && minute <= 9) {
                    hour_picked_text_view.setText("0" + String.valueOf(hourOfDay) + ":0" + String.valueOf(minute));
                } else if (hourOfDay <= 9 && minute > 9) {
                    hour_picked_text_view.setText("0" + String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
                } else if (hourOfDay > 9 && minute <= 9) {
                    hour_picked_text_view.setText(String.valueOf(hourOfDay) + ":0" + String.valueOf(minute));
                } else {
                    hour_picked_text_view.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
                }
            }
        }
    }

    public void saveEvent(View v) throws ParseException {
        if (nameEvent.getText().toString().equals("")) {
            Toast.makeText(this, "Entrez le nom de l'événement !", Toast.LENGTH_SHORT).show();
            return;
        }
        if (datepickertxtview.getText().toString().equals("")) {
            Toast.makeText(this, "Entrez une date !", Toast.LENGTH_SHORT).show();
            return;
        }
        if (hour_picked_text_view.getText().toString().equals("")) {
            Toast.makeText(this, "Entrez l'heure !", Toast.LENGTH_SHORT).show();
            return;
        }

        if(eventMode){
            new UpdateLocationTask(EventActivity.this).execute(
                    ""+mEditLocation.getmLocationId(),
                    locationLatLngTextView.getText().toString(),
                    meteoPicked.getText().toString(),
                    longitudeCity,
                    latitudeCity,
                    iconPath
            );
        }
        else{
            // Add Location
            new InsertLocationTask(this).execute(
                    locationLatLngTextView.getText().toString(),
                    meteoPicked.getText().toString(),
                    longitudeCity,
                    latitudeCity,
                    iconPath
            );
        }
    }

    /*CLASSES ASYNCHRONES POUR REQUÊTES HTTP et SQL*/

    /*REQUÊTES GET*/
    public class MyAsyncTaskgetNews extends AsyncTask<String, String, String> {

        private float temp;
        private Boolean findPrevison = false;

        @Override
        protected String  doInBackground(String... params) {
            String NewsData="";
            try
            {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                NewsData=Stream2String(in);
                in.close();

                publishProgress(NewsData);

            } catch (Exception e) {
                // TODO Auto-generated catch block

            }
            return null;
        }
        protected void onProgressUpdate(String... progress) {

            try {

                JSONObject root = new JSONObject(progress[0]);
                //SimpleDateFormat sdf_JSON = new SimpleDateFormat("dd.MM.yyyy");
                //String dateSelectedString = sdf_JSON.format(dateSelected);
                String[] tab = datepickertxtview.getText().toString().split("/");
                String dateSelectedString = tab[0]+"."+tab[1]+"."+tab[2];

                JSONObject day_0 = root.getJSONObject("fcst_day_0");

                JSONObject day_1 = root.getJSONObject("fcst_day_1");

                JSONObject day_2 = root.getJSONObject("fcst_day_2");

                JSONObject day_3 = root.getJSONObject("fcst_day_3");

                JSONObject day_4 = root.getJSONObject("fcst_day_4");

                ArrayList<JSONObject> dayList = new ArrayList<>();
                dayList.add(day_0);dayList.add(day_1);dayList.add(day_2);dayList.add(day_3);dayList.add(day_4);


                for (JSONObject d : dayList) {
                    String date = d.getString("date");
                    if(date.equals(dateSelectedString)) {
                        findPrevison = true;
                        temp = (d.getInt("tmin") + d.getInt("tmax")) / 2;
                        iconPath = d.getString("icon");
                        new ImageLoadTask(iconPath, iconMeteo).execute();
                        return;
                    }
                }

                changeProgressBarVisibility();
                Toast.makeText(getApplicationContext(), "Prévision indisponible !", Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        protected void onPostExecute(String  result2){
            if(findPrevison)
                meteoPicked.setText(temp+" °C");
        }
    }

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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
            changeProgressBarVisibility();
        }

    }

    public class GetEventTask extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;
        private Event event;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        public GetEventTask(EventActivity activity) {
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
            String selection = EventContract.EventEntry.COLUMN_NAME_EVENT_ID+ "=?";

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

                event = new Event(_id,_name,_date,_time,_desc,_location_id,_notification_id,_email);
            }

            queryResult.close();
            return params[0];
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            dbHelper.close();
            fillEventActivityFields(event);
        }
    }

    public class GetLocationTask extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;
        private Location location;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        public GetLocationTask(EventActivity activity) {
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

                location = new Location(_id,_name,_temperature,_latitude,_longitude,_icon);
            }

            queryResult.close();
            return location.getmLocationName();
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            dbHelper.close();
            mEditLocation = location;
            fillLocationNameInEditMode(location);
        }
    }

    public class GetSmartNotifTask extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        public GetSmartNotifTask(EventActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            dialog.setMessage("Récupération des données");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("NOTIF","DO IN BACKGROUND");
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            //SELECT * FROM event
            String[] projection = {
                    NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_ID,
                    NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_NAME,
                    NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_RED,
                    NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_GREEN,
                    NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_BLUE
            };

            Cursor queryResult = db.query(
                    NotificationContract.NotificationEntry.TABLE_NAME,      // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // The sort order
            );

            while (queryResult.moveToNext()) {

                String id = queryResult.getString(queryResult.getColumnIndex(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_ID));
                String nom = queryResult.getString(queryResult.getColumnIndex(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_NAME));
                String r = queryResult.getString(queryResult.getColumnIndex(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_RED));
                String g = queryResult.getString(queryResult.getColumnIndex(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_GREEN));
                String b = queryResult.getString(queryResult.getColumnIndex(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_BLUE));

                SmartNotification mynotif = new SmartNotification (Integer.valueOf(id),nom,Integer.valueOf(r),Integer.valueOf(g),Integer.valueOf(b));
                listNotif.add(mynotif);
            }

            queryResult.close();
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            dbHelper.close();
            populateSpinner(listNotif);
        }
    }

    /*REQUÊTES INSERT*/

    public class InsertEventTask extends  AsyncTask<String, Void, Void>{

        /** progress dialog to show user that the backup is processing. */
        private ProgressDialog dialog;
        /** application context. */
        private EventActivity activity;

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        public InsertEventTask(EventActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            dialog.setMessage("Progress start");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            ContentValues values = new ContentValues();

            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_NAME,params[0]);

            SimpleDateFormat sdf_DATE = new SimpleDateFormat("dd/MM/yyyy");
            Date dateParse = null;
            try {
                dateParse = sdf_DATE.parse(params[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_DATE,dateParse.getTime());
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_TIME,params[2]);
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_DESCRIPTION,params[3]);
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_LOCATION_ID,params[4]);
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_NOTIFICATION_ID,getSelectedSmartNotifId());
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_EMAIL,params[5]);

            db.insert(
                    EventContract.EventEntry.TABLE_NAME,
                    null,
                    values);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            dbHelper.close();
            Toast.makeText(activity,"Événement ajouté avec succés !",Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }

    public class InsertLocationTask extends  AsyncTask<String, Void, Void>{

        /** progress dialog to show user that the backup is processing. */
        private ProgressDialog dialog;
        /** application context. */
        private EventActivity activity;

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        public InsertLocationTask(EventActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            dialog.setMessage("Progress start");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            ContentValues values = new ContentValues();

            values.put(LocationContract.LocationEntry.COLUMN_NAME_LOCATION_NAME,params[0]);
            values.put(LocationContract.LocationEntry.COLUMN_NAME_METEO_TEMPERATURE,params[1]);
            values.put(LocationContract.LocationEntry.COLUMN_NAME_LOCATION_LONGITUDE,params[2]);
            values.put(LocationContract.LocationEntry.COLUMN_NAME_LOCATION_LATITUDE,params[3]);
            values.put(LocationContract.LocationEntry.COLUMN_NAME_METEO_ICON,params[4]);


            long newRowId;
            newRowId = db.insert(
                    LocationContract.LocationEntry.TABLE_NAME,
                    null,
                    values);
            idLocation =  String.valueOf(newRowId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            dbHelper.close();
            new InsertEventTask(EventActivity.this).execute(
                    nameEvent.getText().toString(),
                    datepickertxtview.getText().toString(),
                    hour_picked_text_view.getText().toString(),
                    desc_even.getText().toString(),
                    idLocation,
                    spinnerEmail.getSelectedItem().toString()
            );
        }
    }

    /*REQUÊTES UPDATE*/

    public class UpdateEventTask extends  AsyncTask<String, Void, Void>{

        /** progress dialog to show user that the backup is processing. */
        private ProgressDialog dialog;
        private EventActivity activity;

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        public UpdateEventTask(EventActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            dialog.setMessage("Mise à jour...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            ContentValues values = new ContentValues();

            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_NAME,params[1]);


            SimpleDateFormat sdf_DATE = new SimpleDateFormat("dd/MM/yyyy");
            Date dateParse = null;
            try {
                dateParse = sdf_DATE.parse(params[2]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_DATE,dateParse.getTime());
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_TIME,params[3]);
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_DESCRIPTION,params[4]);
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_LOCATION_ID,params[5]);
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_NOTIFICATION_ID,getSelectedSmartNotifId());
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_EMAIL,params[6]);

            // Which row to update, based on the ID
            String selection = EventContract.EventEntry.COLUMN_NAME_EVENT_ID + " LIKE ?";
            String[] selectionArgs = {params[0]};

            db.update(
                    EventContract.EventEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            dbHelper.close();
            Toast.makeText(activity,"Événement modifié avec succés !",Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }

    public class UpdateLocationTask extends  AsyncTask<String, Void, Void>{

        /** progress dialog to show user that the backup is processing. */
        private ProgressDialog dialog;
        private EventActivity activity;

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        public UpdateLocationTask(EventActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            dialog.setMessage("Mise à jour du lieu...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            ContentValues values = new ContentValues();

            values.put(LocationContract.LocationEntry.COLUMN_NAME_LOCATION_NAME,params[1]);
            values.put(LocationContract.LocationEntry.COLUMN_NAME_METEO_TEMPERATURE,params[2]);
            values.put(LocationContract.LocationEntry.COLUMN_NAME_LOCATION_LONGITUDE,params[3]);
            values.put(LocationContract.LocationEntry.COLUMN_NAME_LOCATION_LATITUDE,params[4]);
            values.put(LocationContract.LocationEntry.COLUMN_NAME_METEO_ICON,params[5]);

            // Which row to update, based on the ID
            String selection = LocationContract.LocationEntry.COLUMN_NAME_LOCATION_ID + " LIKE ?";
            String[] selectionArgs = {params[0]};

            db.update(
                    LocationContract.LocationEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            dbHelper.close();
            new UpdateEventTask(EventActivity.this).execute(
                    ""+editEventId,
                    nameEvent.getText().toString(),
                    datepickertxtview.getText().toString(),
                    hour_picked_text_view.getText().toString(),
                    desc_even.getText().toString(),
                    ""+mEditLocation.getmLocationId(),
                    spinnerEmail.getSelectedItem().toString()
            );
        }
    }

}
