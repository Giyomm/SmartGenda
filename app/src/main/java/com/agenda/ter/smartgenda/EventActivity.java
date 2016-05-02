package com.agenda.ter.smartgenda;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.DialogFragment;
import android.widget.TimePicker;
import android.widget.Toast;

import com.agenda.ter.model.Location;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    public DatePickerDialog dpd;
    EditText nameEvent;
    TextView locationLatLngTextView;
    ImageButton datepick;
    EditText desc_even;
    static TextView tv;
    Button saveEventButton;
    TextView datepickertxtview;

    private CalendarView calendar2;
    private Date selectedDate;
    Spinner spinner;

    Intent intentFromCalendar;
    private Location location;

    //LES EXTRAS
    public static final String EXTRA_LONGITUDE = "com.agenda.ter.LONG";
    public static final String EXTRA_LATITUDE = "com.agenda.ter.LAT";
    public static final String EXTRA_LOCALISATION_NAME = "com.agenda.ter.LOCATION_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        datepick = (ImageButton) findViewById(R.id.date_even_butonimage_id);
        desc_even = (EditText) findViewById(R.id.desc_even_edittextt_id);
        nameEvent = (EditText) findViewById(R.id.nom_even_edittext_id);
        locationLatLngTextView = (TextView) findViewById(R.id.event_location_texteview_id);String aa = locationLatLngTextView.getText().toString();
        datepickertxtview = (TextView) findViewById(R.id.datepicker_textview_id);
         tv = (TextView) findViewById(R.id.event_hourpicker_textview_id);
        saveEventButton = (Button)findViewById(R.id.event_saveEvent_bouton_id);

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
        //dp.setMaxDate(calendar.getTimeInMillis());

        intentFromCalendar = getIntent();
        long dateSelected = intentFromCalendar.getLongExtra(CalendarActivity.EXTRA_SELECTED_DATE, 0);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            double latitudeEvent = data.getDoubleExtra(EXTRA_LATITUDE,0);
            double longitudeEvent = data.getDoubleExtra(EXTRA_LONGITUDE,0);
            String locationName = data.getStringExtra(EXTRA_LOCALISATION_NAME);
            location = new Location(locationName,latitudeEvent,longitudeEvent);
            Log.d("LAT ET LONG","LAT: "+latitudeEvent+" LONG: "+longitudeEvent+ " NAME / "+locationName);
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
            tv.setText(String.valueOf(hourOfDay)+":"+String.valueOf(minute));
        }
    }


    public void saveEvent(View v){
        if(nameEvent.getText().toString()==null || nameEvent.getText().toString().equals("")){
            Toast.makeText(this, "Entrez le nom de l'événement !", Toast.LENGTH_SHORT).show();
            return;
        }
        if(datepickertxtview.getText().toString()==null || datepickertxtview.getText().toString().equals("")){
            Toast.makeText(this, "Entrez une date !", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tv.getText().toString()==null || tv.getText().toString().equals("")){
            Toast.makeText(this, "Entrez l'heure !",Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("DEST",locationLatLngTextView.getText().toString());
        if(locationLatLngTextView.getText().toString()==null || locationLatLngTextView.getText().toString().equals("")){
            Toast.makeText(this, "Entrez le lieu !",Toast.LENGTH_SHORT).show();
            return;
        }
    }


}
