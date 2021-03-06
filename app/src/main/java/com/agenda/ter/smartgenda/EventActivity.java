package com.agenda.ter.smartgenda;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.agenda.ter.database.ReminderContract;
import com.agenda.ter.database.SmartgendaDbHelper;
import com.agenda.ter.model.Event;
import com.agenda.ter.model.Location;
import com.agenda.ter.model.Reminder;
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
import java.util.regex.Pattern;

/**
 * @author Smartgenda Team
 * EventActivity sert à ajouter ou modifier un événement.
 */

public class EventActivity extends AppCompatActivity {

    /**
     * idLocation : Identifiant d'un lieu
     * iconPath : URL de l'icone météo
     */
    private String idLocation;
    private String iconPath;

    /**
     * dpd : Un dialogue (pop-up) pour choisir une date
     * nameEvent : Champ de texte pour saisir le nom de l'événement
     * locationLatLngTextView : TextView pour afficher le lieu de l'événement
     * desc_even : Champ de texte pour saisir la description de l'événement
     * hour_picked_text_view : TextView affichant l'heure choisie
     * meteoPicked : TextView affichant la météo récupérée
     * iconMeteo : Image montrant l'état de la météo récupérée
     */
    public DatePickerDialog dpd;
    private EditText nameEvent;
    private TextView locationLatLngTextView;
    private EditText desc_even;
    private static TextView hour_picked_text_view;
    public static TextView datepickertxtview;
    private TextView meteoPicked;
    private ImageView iconMeteo;

    /**
     * notificationSpinner : Liste déroulante qui contient les notifications disponibles
     * spinnerEmail : Liste déroulante qui contient les emails disponibles
     */
    private Spinner notificationSpinner;
    private Spinner spinnerEmail;

    /**
     * meteoPbar : Une ProgressBar qui charge l'icone de la météo
     * intentFromCalendar : Intent venant de la page CalendarActivity
     * eventMode : 1 = ajouter un événement   ||  0 = modifier un événement
     * mEditLocaion : Objet de type Location
     */
    private ProgressBar meteoPbar;
    private Intent intentFromCalendar;
    private Boolean eventMode;
    private int editEventId;
    private Location mEditLocation;

    /**
     * Latitude et longitude d'un événement.
     */
    double latitudeEvent;
    double longitudeEvent;
    private String latitudeCity, longitudeCity;
    public long dateSelected;

    private ArrayList<Reminder> reminderList;

    /**
     * EXTRA_LONGITUDE : extra contenant la longitude d'un lieu
     * EXTRA_LATITUDE : extra contenant la latitude d'un lieu
     * EXTRA_LOCALISATION_NAME : extra contant le nom d'un lieu
     * EXTRA_NOTIFICATION_ALARM_NAME : extra contenant le nom d'une alarme
     *EXTRA_NOTIFICATION_ALARM_DATE_AND_TIME : extra contenant la date et l'heure d'une alarme
     */
    public static final String EXTRA_LONGITUDE = "com.agenda.ter.LONG";
    public static final String EXTRA_LATITUDE = "com.agenda.ter.LAT";
    public static final String EXTRA_LOCALISATION_NAME = "com.agenda.ter.LOCATION_NAME";
    public static final String EXTRA_NOTIFICATION_ALARM_NAME = "com.agenda.ter.NOTIFICATION_ALARM_NAME";
    public static final String EXTRA_NOTIFICATION_ALARM_DATE_AND_TIME = "com.agenda.ter.NOTIFICATION_ALARM_DATE_AND_TIME";
    public static final String EXTRA_NOTIFICATION_ALARM_LAST_ID = "com.agenda.ter.NOTIFICATION_ALARM_LAST_ID";

    /**
     * dbHelper : permet la communication entre les activités et la base de données
     * listNotif : Liste contenant les notification disponiles
     */
    private SmartgendaDbHelper dbHelper;
    ArrayList<SmartNotification> listNotif = new ArrayList<>();

    /**
     * alarmManager : Objet de type AlarmManager
     * newFragment : Dialogue affichant l'heure
     */
    AlarmManager alarmManager;
    DialogFragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        dbHelper = new SmartgendaDbHelper(getApplicationContext());

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
        spinnerEmail = (Spinner)findViewById(R.id.event_spinner_email);

        reminderList = new ArrayList<>();

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


    @Override
    protected void onResume() {
        super.onResume();

       try{
           if(NotificationActivity.listNotifPerso.size() > 0){
               for (SmartNotification sn : NotificationActivity.listNotifPerso) {
                   notificationSpinner.setSelection(NotificationActivity.listNotifPerso.indexOf(sn));
               }
               populateSpinner(NotificationActivity.listNotifPerso);
           }
       }catch(Exception e){}


    }


    /**
     * Recuperer les emails du téléphone et remplir le spinner avec les emails trouvés
     */
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

    /**
     * Convertir un Stream à un String
     * @param inputStream Objet de type InputStream
     * @return
     */
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

    /**
     * Récupere la latitude, la longitude, la météo depuis MapsActivity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            latitudeEvent = data.getDoubleExtra(EXTRA_LATITUDE,0);
            longitudeEvent = data.getDoubleExtra(EXTRA_LONGITUDE,0);
            String locationName = data.getStringExtra(EXTRA_LOCALISATION_NAME);
            try
            {
                longitudeCity=String.valueOf(longitudeEvent);
                latitudeCity=String.valueOf(latitudeEvent);
                String myurl = "http://www.prevision-meteo.ch/services/json/lat="+latitudeCity+"lng="+longitudeCity;
                changeProgressBarVisibility();
                new  MyAsyncTaskgetNews().execute(myurl);

            }catch (Exception ex){}
            locationLatLngTextView.setText(locationName+"");
        }
    }

    /**
     * Pop-up avertissant l'utilisateur s'il abondonne l'ajout ou la modification d'un événement
     */
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

    /**
     * Permet de remplir la zone du prochain événement par l'événement le plus proche
     * @param event Objet de type événement
     */
    private void fillEventActivityFields(Event event) {
        nameEvent.setText(event.getmEventName());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(event.getmEventDate());
        datepickertxtview.setText(dateString);
        hour_picked_text_view.setText(event.getmEventTime());
        desc_even.setText(event.getmEventDescription());
        new GetLocationTask(this).execute(""+event.getmEventLocationId());
        new GetSmartNotifByIDTask(this).execute(""+event.getmEventNotificationId());
    }

    /**
     * Permet de récuperer les informations du lieu de l'événement et l'afficher dans la zone du prochain événement
     * @param loc Objet de type Location
     */
    private void fillLocationNameInEditMode(Location loc){
        locationLatLngTextView.setText(loc.getmLocationName());
        meteoPicked.setText(loc.getmMeteoTemperature()+"° C");
        changeProgressBarVisibility();
        iconPath= loc.getmMeteoIcon();
        new ImageLoadTask(loc.getmMeteoIcon(), iconMeteo).execute();
    }

    /**
     * Récupere la notification séléctionnée par l'utilisateur
     * @param notif Objet de type SmartNotification
     */
     void setNotificationSpinner(SmartNotification notif){

        for (SmartNotification sn : listNotif) {
            if(sn.getmSmartNotificationId() == notif.getmSmartNotificationId())
                notificationSpinner.setSelection(listNotif.indexOf(sn));
        }
    }

    /**
     * Affiche DatePicker
     * @param view
     */
    public void showDatePicker(View view) {
        dpd.show();
    }

    /**
     * Permet de se diriger vers la page MapsActivity
     * @param view
     */
    public void goToMapsActivity(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent,1);
    }

    /**
     * Permet de se diriger vers la page NotificationActivity
     * @param view
     */
    public void goToNotificationPage(View view) {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    /**
     * Controle la visibilité du ProgressBar
     */
    public void changeProgressBarVisibility(){
        if(meteoPbar.getVisibility() == View.VISIBLE){
            meteoPbar.setVisibility(View.GONE);
        }
        else
            meteoPbar.setVisibility(View.VISIBLE);
    }

    /**
     * Remplir le spinner des notifications
     * @param list : Liste contenant toutes les notifications
     */
    public void populateSpinner(ArrayList<SmartNotification> list){
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<SmartNotification> adapter = new ArrayAdapter<SmartNotification>(this,R.layout.support_simple_spinner_dropdown_item,list);
        // Apply the adapter to the spinner
        notificationSpinner.setAdapter(adapter);
    }

    /**
     * Récupere l'identifiant d'une notification séléctionnée par l'utilisateur
     * @return l'identifiant de la notification séléctionnée
     */
    public String getSelectedSmartNotifId(){
        int id = ((SmartNotification)notificationSpinner.getSelectedItem()).getmSmartNotificationId();
        return String.valueOf(id);
    }

    /**
     * Afficher le TimePicker
     * @param v
     */
    public void showTimePicker(View v){
        newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(),"TimePicker");
    }

    /**
     * Classe permet l'instanciation et la récupération de l'heure choisie par l'utilisateur
     */
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

    /**
     * Controler s'il y a un champs obligatoire vide pendant l'ajout et la modification d'un événement
     * @param v
     * @throws ParseException
     */
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

    /**
     * Programmer une alarme en fonction des rappels choisies
     * @throws ParseException
     */
    public void programAlarm(Long _id) throws ParseException {
        //Create the alarm manager
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //Set the date and time of the event
        String dateAndTime = datepickertxtview.getText().toString() +" "+hour_picked_text_view.getText().toString();
        Date eventDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dateAndTime);

        //For each of the reminders of the event set the alarms
        for (Reminder rmnd : reminderList) {
            //Process the date and time fo the reminder
            long alarmMilli = eventDate.getTime() - rmnd.getmReminderTime();

            //If the reminder is pass the current time and date, we won't set the alarm to prevent the app to sent off
            //all the passed alarmed
            if(alarmMilli > System.currentTimeMillis()){
                final Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                alarmIntent.putExtra(EXTRA_NOTIFICATION_ALARM_NAME,nameEvent.getText().toString());
                alarmIntent.putExtra(EXTRA_NOTIFICATION_ALARM_DATE_AND_TIME,dateAndTime);

                PendingIntent pending_intent = PendingIntent.getBroadcast(EventActivity.this, (int)System.currentTimeMillis(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmMilli, pending_intent);
            }
        }

        Intent lastAlarmIntent = new Intent(this, AlarmReceiver.class);
        lastAlarmIntent.putExtra(EXTRA_NOTIFICATION_ALARM_NAME,nameEvent.getText().toString());
        lastAlarmIntent.putExtra(EXTRA_NOTIFICATION_ALARM_DATE_AND_TIME,dateAndTime);
        lastAlarmIntent.putExtra(EXTRA_NOTIFICATION_ALARM_LAST_ID,_id);

        PendingIntent pending_intent = PendingIntent.getBroadcast(EventActivity.this, (int)System.currentTimeMillis(), lastAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, eventDate.getTime(), pending_intent);

        Toast.makeText(this,"Événement ajouté avec succés !",Toast.LENGTH_SHORT).show();
        this.finish();
    }

    /**
     * Récupere les informations de la météo du lieu de l'événement
     */
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

            } catch (Exception e) {}
            return null;
        }
        protected void onProgressUpdate(String... progress) {

            try {

                JSONObject root = new JSONObject(progress[0]);
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

    /**
     * Récupere l'icone de la météo
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

    /**
     * Récupere un événement depuis la base de données
     */
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

    /**
     * Récupere un lieu depuis la base de données
     */
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

    /**
     * Permet de récuperer toutes les notifications depuis la base de données
     */
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

            String selection = NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_NAME+ " in(?,?)";
            String[] selectionArgs = {"Trés fréquente","Normale"};

            Cursor queryResult = db.query(
                    NotificationContract.NotificationEntry.TABLE_NAME,      // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
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

    /**
     * Permet de récuperer une notification par son ID
     */
    public class GetSmartNotifByIDTask extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;
        private SmartNotification notification;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        public GetSmartNotifByIDTask(EventActivity activity) {
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
                    NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_ID,
                    NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_NAME,
                    NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_RED,
                    NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_GREEN,
                    NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_BLUE
            };

            /*(WHERE id = _id_)*/
            //WHERE clause column
            String selection = NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_ID+ "=?";

            //WHERE clause values
            String[] selectionArgs = {params[0]};

            Cursor queryResult = db.query(
                    NotificationContract.NotificationEntry.TABLE_NAME,      // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
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

                notification = new SmartNotification (Integer.valueOf(id),nom,Integer.valueOf(r),Integer.valueOf(g),Integer.valueOf(b));
            }

            queryResult.close();
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            dbHelper.close();
            setNotificationSpinner(notification);
        }
    }

    /**
     * Récupere les rappels d'une notification
     */
    public class GetReminderTask extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;
        private EventActivity context;
        private Long eventID;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        public GetReminderTask(EventActivity activity, Long id) {
            context = activity;
            dialog = new ProgressDialog(activity);
            eventID = id;
        }

        protected void onPreExecute() {
            dialog.setMessage("Récupération des données");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            //SELECT * FROM reminder
            String[] projection = {
                    ReminderContract.ReminderEntry.COLUMN_NAME_REMINDER_ID,
                    ReminderContract.ReminderEntry.COLUMN_NAME_REMINDER_TIME,
                    ReminderContract.ReminderEntry.COLUMN_NAME_REMINDER_DISPLAY_MAP,
                    ReminderContract.ReminderEntry.COLUMN_NAME_REMINDER_NOTIFICATION_ID
            };

            /*(WHERE id = event.notif_id)*/
            //WHERE clause column
            String selection = ReminderContract.ReminderEntry.COLUMN_NAME_REMINDER_NOTIFICATION_ID+ "=?";

            //WHERE clause values
            String[] selectionArgs = {params[0]};

            Cursor queryResult = db.query(
                    ReminderContract.ReminderEntry.TABLE_NAME,// The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // The sort order
            );

            while (queryResult.moveToNext()) {

                int id = queryResult.getInt(queryResult.getColumnIndex(ReminderContract.ReminderEntry.COLUMN_NAME_REMINDER_ID));
                int time = queryResult.getInt(queryResult.getColumnIndex(ReminderContract.ReminderEntry.COLUMN_NAME_REMINDER_TIME));
                int display_map = queryResult.getInt(queryResult.getColumnIndex(ReminderContract.ReminderEntry.COLUMN_NAME_REMINDER_DISPLAY_MAP));
                int notif_id = queryResult.getInt(queryResult.getColumnIndex(ReminderContract.ReminderEntry.COLUMN_NAME_REMINDER_NOTIFICATION_ID));

                Reminder r = new Reminder(id,time,notif_id);
                reminderList.add(r);
            }

            queryResult.close();
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            dbHelper.close();
            try { programAlarm(eventID);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Inserer un événement dans la base de données
     */
    public class InsertEventTask extends  AsyncTask<String, Long, Long>{
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
        protected Long doInBackground(String... params) {

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

            Long newId = db.insert(
                    EventContract.EventEntry.TABLE_NAME,
                    null,
                    values);

            return newId;
        }

        @Override
        protected void onPostExecute(Long id) {
            super.onPostExecute(id);
            dialog.dismiss();
            dbHelper.close();
            //Recupère les reminder pour programmer l'alarme
            new GetReminderTask(activity,id).execute(getSelectedSmartNotifId());
        }
    }

    /**
     * Inserer un lieu dans la base de données
     */
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

    /**
     * Modifier un événement
     */
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

    /**
     * Modifier le lieu d'un événement
     */
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
