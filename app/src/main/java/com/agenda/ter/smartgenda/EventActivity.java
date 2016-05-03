package com.agenda.ter.smartgenda;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.DialogFragment;
import android.widget.TimePicker;
import android.widget.Toast;

import com.agenda.ter.database.EventContract;
import com.agenda.ter.database.SmartgendaDbHelper;
import com.agenda.ter.model.Location;

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

public class EventActivity extends AppCompatActivity {

    public DatePickerDialog dpd;
    private EditText nameEvent;
    private TextView locationLatLngTextView;
    private ImageButton datepick;
    private EditText desc_even;
    private static TextView hour_picked_text_view;
    private Button saveEventButton;
    private TextView datepickertxtview;
    private TextView meteoPicked;
    private ImageView iconMeteo;

    private CalendarView calendar2;
    private Date selectedDate;
    Spinner spinner;
    private ProgressBar meteoPbar;

    Intent intentFromCalendar;
    private Location location;

    //LAT ET LON DE L EVENEMENT
    double latitudeEvent;
    double longitudeEvent;
    String latitudeCity, longitudeCity;
    long dateSelected;

    //LES EXTRAS
    public static final String EXTRA_LONGITUDE = "com.agenda.ter.LONG";
    public static final String EXTRA_LATITUDE = "com.agenda.ter.LAT";
    public static final String EXTRA_LOCALISATION_NAME = "com.agenda.ter.LOCATION_NAME";

    //DATABASE
    private SmartgendaDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        dbHelper = new SmartgendaDbHelper(getApplicationContext());

        datepick = (ImageButton) findViewById(R.id.date_even_butonimage_id);
        desc_even = (EditText) findViewById(R.id.desc_even_edittextt_id);
        nameEvent = (EditText) findViewById(R.id.nom_even_edittext_id);
        locationLatLngTextView = (TextView) findViewById(R.id.event_location_texteview_id);
        datepickertxtview = (TextView) findViewById(R.id.datepicker_textview_id);
        hour_picked_text_view = (TextView) findViewById(R.id.event_hourpicker_textview_id);
        saveEventButton = (Button)findViewById(R.id.event_saveEvent_bouton_id);
        meteoPbar = (ProgressBar)findViewById(R.id.event_weather_progressbar_id);
        meteoPicked = (TextView)findViewById(R.id.event_weatherpicked_textview_id);
        iconMeteo = (ImageView)findViewById(R.id.event_weather_icon_id);

        //CACHER PROGRESSBAR
        meteoPbar.setVisibility(View.GONE);

        Calendar calendar = Calendar.getInstance();
        dpd = new DatePickerDialog(EventActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                datepickertxtview.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        DatePicker dp = dpd.getDatePicker();
        dp.setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH,Integer.MAX_VALUE);

        intentFromCalendar = getIntent();
        dateSelected = intentFromCalendar.getLongExtra(CalendarActivity.EXTRA_SELECTED_DATE, 0);

        if(dateSelected!= 0){
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = formatter.format(new Date(dateSelected));
            datepickertxtview.setText("" + dateString);
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

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



    // TIME PICKER ET QUE TIME PICKER :)

    //AFFICHER LE TIME PICKER
    public void showTimePicker(View v){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(),"TimePicker");
    }
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            //Use the current time as the default values for the time picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            //Create and return a new instance of TimePickerDialog
            return new TimePickerDialog(getActivity(),this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        //onTimeSet() callback method
        public void onTimeSet(TimePicker view, int hourOfDay, int minute){
            hour_picked_text_view.setText(String.valueOf(hourOfDay)+":"+String.valueOf(minute));
        }
    }


    public void saveEvent(View v) throws ParseException {
        if(nameEvent.getText().toString().equals("")){
            Toast.makeText(this, "Entrez le nom de l'événement !", Toast.LENGTH_SHORT).show();
            return;
        }
        if(datepickertxtview.getText().toString().equals("")){
            Toast.makeText(this, "Entrez une date !", Toast.LENGTH_SHORT).show();
            return;
        }
        if(hour_picked_text_view.getText().toString().equals("")){
            Toast.makeText(this, "Entrez l'heure !",Toast.LENGTH_SHORT).show();
            return;
        }

        new InsertEventTask(this).execute(nameEvent.getText().toString(),
                datepickertxtview.getText().toString()+" "+hour_picked_text_view.getText().toString(),
                desc_even.getText().toString());

    }

    // CLASSES ASYNCHRONES POUR LA REQUETE HTTP
    public class MyAsyncTaskgetNews extends AsyncTask<String, String, String> {

        private float temp;
        private Boolean findPrevison = false;

        @Override
        protected void onPreExecute() {


        }
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
                //Log.d("PROGRESS", progress[0]);
                JSONObject root = new JSONObject(progress[0]);
                SimpleDateFormat sdf_JSON = new SimpleDateFormat("dd.MM.yyyy");
                String dateSelectedString = sdf_JSON.format(dateSelected);

                JSONObject day_0 = root.getJSONObject("fcst_day_0");
                //Date d0 = sdf.parse(date_0);

                JSONObject day_1 = root.getJSONObject("fcst_day_1");
                //Date d1 = sdf.parse(date_1);

                JSONObject day_2 = root.getJSONObject("fcst_day_2");
                //Date d2 = sdf.parse(date_2);

                JSONObject day_3 = root.getJSONObject("fcst_day_3");
                //Date d3 = sdf.parse(date_3);

                JSONObject day_4 = root.getJSONObject("fcst_day_4");
                //Date d4 = sdf.parse(date_4);

                ArrayList<JSONObject> dayList = new ArrayList<>();
                dayList.add(day_0);dayList.add(day_1);dayList.add(day_2);dayList.add(day_3);dayList.add(day_4);

                // Log.d("DATE SIZE LISTE",""+dayList.size());

                for (JSONObject d : dayList) {
                    String date = d.getString("date");
                    //Log.d("DATE LISTE",new SimpleDateFormat("dd/MM/yyyy").format(d)+ " COMPARE WITH "+new SimpleDateFormat("dd/MM/yyyy").format(new Date(dateSelected)));
                    if(date.equals(dateSelectedString)) {
                        findPrevison = true;
                        temp = (d.getInt("tmin") + d.getInt("tmax")) / 2;
                        String iconPath = d.getString("icon");
                        Log.d("TMP", "" + temp);
                        Log.d("ICON", iconPath);
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

            SimpleDateFormat sdf_DATE_TIME = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            Date dateParse = null;
            try {
                dateParse = sdf_DATE_TIME.parse(params[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_DATE,dateParse.getTime());
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_DESCRIPTION,params[2]);
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_LOCATION_ID,0);
            values.put(EventContract.EventEntry.COLUMN_NAME_EVENT_NOTIFICATION_ID,0);

            long newRowId;
            newRowId = db.insert(
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
        }
    }

}
