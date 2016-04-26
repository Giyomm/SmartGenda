package com.agenda.ter.smartgenda;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;

import com.agenda.ter.database.SmartgendaDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {

    public final static String EXTRA_SELECTED_DATE = "com.agenda.ter.smartgenda.SELECTED_DATE";

    private CalendarView calendar;
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        //SQLiteDatabase database = openOrCreateDatabase(SmartgendaDbHelper.DATABASE_NAME,MODE_PRIVATE,null);

        //Init the calendar view
        calendar = (CalendarView) findViewById(R.id.calendar_view_id);

        //Select the current selected date to the current date
        selectedDate = new Date(calendar.getDate());

        //Set the selected date as the calendar minimum date to display
        calendar.setMinDate(selectedDate.getTime());

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Log.d("DATE SELECTED",""+dayOfMonth+"/"+(month+1)+"/"+year);
                String str_date = dayOfMonth+"/"+(month+1)+"/"+year;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    selectedDate = sdf.parse(str_date);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                AlertDialog.Builder eventDialogBuilder = new AlertDialog.Builder(CalendarActivity.this);
                eventDialogBuilder.setTitle(str_date);
                eventDialogBuilder.setMessage("Create, delete or modify an Event");

                eventDialogBuilder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                eventDialogBuilder.setPositiveButton(
                        "Add Event",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(CalendarActivity.this, EventActivity.class);
                                intent.putExtra(EXTRA_SELECTED_DATE,selectedDate.getTime());
                                startActivity(intent);
                            }
                        });

                eventDialogBuilder.show();

                /*final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                CalendarActivity.this,android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Hardik");
                arrayAdapter.add("Archit");
                arrayAdapter.add("Jignesh");
                arrayAdapter.add("Umang");
                arrayAdapter.add("Gatti");*/

                /*builderSingle.setAdapter(
                        arrayAdapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String strName = arrayAdapter.getItem(which);
                                AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                        CalendarActivity.this);
                                builderInner.setMessage(strName);
                                builderInner.setTitle("Your Selected Item is");
                                builderInner.setPositiveButton(
                                        "Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                builderInner.show();
                            }
                        });*/

            }
        });
    }

    public void goToEventActivity(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);

    }
}
