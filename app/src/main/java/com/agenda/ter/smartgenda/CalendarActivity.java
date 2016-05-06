package com.agenda.ter.smartgenda;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.agenda.ter.database.EventContract;
import com.agenda.ter.database.SmartgendaDbHelper;
import com.agenda.ter.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    public final static String EXTRA_SELECTED_DATE = "com.agenda.ter.smartgenda.SELECTED_DATE";

    //DATABASE
    private SmartgendaDbHelper dbHelper;

    //Event list
    private ArrayList<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        dbHelper = new SmartgendaDbHelper(getApplicationContext());
        eventList = new ArrayList<>();

        HashSet<Date> events = new HashSet<>();

        com.agenda.ter.smartgenda.CalendarView cv = ((com.agenda.ter.smartgenda.CalendarView)findViewById(R.id.calendar_view));
        cv.updateCalendar(events);

        // assign event handler
        cv.setEventHandler(new com.agenda.ter.smartgenda.CalendarView.EventHandler()
        {
            @Override
            public void onDayLongPress(Date date)
            {
                SimpleDateFormat sdf_DATE = new SimpleDateFormat("dd/MM/yyyy");
                Date dateParse = null;
                try {
                    dateParse = sdf_DATE.parse(new SimpleDateFormat("dd/MM/yyyy").format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                new GetEventTask(CalendarActivity.this).execute(""+dateParse.getTime());
            }
        });
    }

    private void showDayDialog(final Date selectedDate) {
        AlertDialog.Builder eventDialogBuilder = new AlertDialog.Builder(CalendarActivity.this);
        eventDialogBuilder.setTitle(new SimpleDateFormat("dd/MM/yyyy").format(selectedDate));
        eventDialogBuilder.setMessage("Créez, modifiez ou supprimez une tâche.");
        eventDialogBuilder.setIcon(R.mipmap.ic_app);

        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.calendar_event_list, null);
        eventDialogBuilder.setView(convertView);

        ListView events_list_view = (ListView) convertView.findViewById(R.id.event_list_day_events);
        EventListAdapter customAdapter = new EventListAdapter(this,R.layout.calendar_list_item_row,eventList);
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
                        startActivity(intent);
                    }
                });

        eventDialogBuilder.show();
    }

    public void goToEventActivity(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
    }

    public class EventListAdapter extends ArrayAdapter<Event>{

        public EventListAdapter(Context context, int resource, List<Event> items) {
            super(context, resource, items);
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

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
                    String [] _idStr = {""+id};
                    new DeleteEventTask(CalendarActivity.this).execute(_idStr);
                    eventList.remove(e);
                    notifyDataSetChanged();
                }
            });

            return v;
        }
    }

    public class GetEventTask extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;
        private CalendarActivity activity;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        public GetEventTask(CalendarActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            eventList.clear();
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
                    EventContract.EventEntry.COLUMN_NAME_EVENT_NOTIFICATION_ID
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
                Date _date = new Date(Long.valueOf(queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_DATE))));
                String _time = queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_TIME));
                String _desc = queryResult.getString(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_DESCRIPTION));
                int _location_id = queryResult.getInt(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_LOCATION_ID));
                int _notification_id = queryResult.getInt(queryResult.getColumnIndex(EventContract.EventEntry.COLUMN_NAME_EVENT_NOTIFICATION_ID));

                Event e = new Event(_id,_name,_date,_time,_desc,_location_id,_notification_id);
                eventList.add(e);
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

    public class DeleteEventTask extends AsyncTask<String, Void, Void>{

        private ProgressDialog dialog;
        private CalendarActivity activity;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        public DeleteEventTask(CalendarActivity activity) {
            this.activity = activity;
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
        }
    }

}
