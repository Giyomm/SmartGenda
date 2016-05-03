package com.agenda.ter.smartgenda;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

public class CalendarActivity extends AppCompatActivity {

    public final static String EXTRA_SELECTED_DATE = "com.agenda.ter.smartgenda.SELECTED_DATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        //SQLiteDatabase database = openOrCreateDatabase(SmartgendaDbHelper.DATABASE_NAME,MODE_PRIVATE,null);

        HashSet<Date> events = new HashSet<>();

        com.agenda.ter.smartgenda.CalendarView cv = ((com.agenda.ter.smartgenda.CalendarView)findViewById(R.id.calendar_view));
        cv.updateCalendar(events);

        // assign event handler
        cv.setEventHandler(new com.agenda.ter.smartgenda.CalendarView.EventHandler()
        {
            @Override
            public void onDayLongPress(Date date)
            {
                //show day dialog
                showDayDialog(date);
            }
        });

        /*
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
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

            //}
        //});
    }

    private void showDayDialog(final Date selectedDate) {
        AlertDialog.Builder eventDialogBuilder = new AlertDialog.Builder(CalendarActivity.this);
        eventDialogBuilder.setTitle(new SimpleDateFormat("dd/MM/yyyy").format(selectedDate));
        eventDialogBuilder.setMessage("Créez, modifiez ou supprimez une tâche.");

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
                        startActivity(intent);
                    }
                });

        eventDialogBuilder.show();
    }

    public void goToEventActivity(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
    }
}
