package com.agenda.ter.smartgenda;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private String chemin_son;
    private DatePickerDialog dpd;
    private AlertDialog alertDialog;
    private ArrayList<String> list;
    private MyCustomAdapter adapter;
    private TextView txtInput;
    private ArrayList<HashMap<String, String>> songsList;

    private Spinner spinner;
    String selectedColor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // enlever le focus
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ListView listView = (ListView) findViewById(R.id.listView_id);
        txtInput = (TextView) findViewById(R.id.editText_heure_id);

        list = new ArrayList<String>();
        adapter = new MyCustomAdapter(list, this);
        listView.setAdapter(adapter);

        // DatePicker
        ImageButton datepick = (ImageButton) findViewById(R.id.date_even_butonimage_id);
        EditText desc_even = (EditText)findViewById(R.id.desc_even_edittextt_id);

        Calendar calendar = Calendar.getInstance();
        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            TextView datepickertxtview = (TextView)findViewById(R.id.datepicker_textview_id);
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                datepickertxtview.setText(dayOfMonth + "/" + (monthOfYear+1) + "/"+ year);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        // Fin_DatePicker

        //Spinner Color
        Spinner spinner = (Spinner) findViewById(R.id.spinnerColor);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.array_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedColor = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    public void showDatePicker(View view) {
        dpd.show();
    }

    public void showTimePicker(View v){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(),"TimePicker");
    }

    public void saveNotification(View view) {

        EditText nameEditText= (EditText) findViewById(R.id.editText_nomNotif_id); String name = nameEditText.getText().toString();
        TextView dateTextView= (TextView) findViewById(R.id.datepicker_textview_id);String date = dateTextView.getText().toString();
        String heure = txtInput.getText().toString();

        if(name .equals("") || name==null ) {Toast.makeText(getApplicationContext(), "Entrer le nom de la notification !!", Toast.LENGTH_SHORT).show(); return;}
        if(date == null || date.equals("")) {Toast.makeText(getApplicationContext(), "Entrer la date !!", Toast.LENGTH_SHORT).show();return;}
        if(heure == null || heure.equals("")) {Toast.makeText(getApplicationContext(), "Entrer l'heure !!", Toast.LENGTH_SHORT).show();return;}

        Toast.makeText(getApplicationContext(), "Notification enegistrer avec succée ", Toast.LENGTH_SHORT).show();

    }

    public void CloseSonWindow(View view) {

        alertDialog.dismiss();
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
            TextView tv = (TextView) getActivity().findViewById(R.id.editText_heure_id);
            //Set a message for user
            //tv.setText("Your chosen time is...\n\n");
            //Display the user changed time on TextView
            // tv.setText(tv.getText()+ "Hour : " + String.valueOf(hourOfDay)
            //       + "\nMinute : " + String.valueOf(minute) + "\n");
            tv.setText(String.valueOf(hourOfDay)+":"+String.valueOf(minute));

        }
    }


    public void AddToList(View v) {

        String newItem = txtInput.getText().toString();
        if(newItem == ""){Toast.makeText(getApplicationContext(), "Choisir l'eure de l'événement !!", Toast.LENGTH_SHORT).show();}
        else{
            list.add("Rappel "+list.size()+" : "+newItem);
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Rappel à : "+newItem, Toast.LENGTH_SHORT).show();
        }

    }

    public void browseAudioFiles(View view) {

        ArrayList<String> namess=new ArrayList<>();
        final ArrayList<String> listeSonPath=new ArrayList<>();
        String value = null;
        songsList = new ArrayList<>();
        String[] STAR = { "*" };

        Cursor cursor;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        cursor = managedQuery(uri, STAR, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String songName = cursor
                            .getString(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));


                    String path = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));


                    String albumName = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    int albumId = cursor
                            .getInt(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                    HashMap<String, String> song = new HashMap<String, String>();
                    song.put("songTitle",albumName+" "+songName+"___"+albumId);
                    song.put("songPath",path );
                    songsList.add(song);

                } while (cursor.moveToNext());

            }

        }

        for(int i = 0; i < songsList.size();i++){
            HashMap<String,String> h = songsList.get(i);
            for (String name: h.keySet()){

                String key =name.toString();
                 value = h.get(name).toString();
                if(key.equals("songPath")) {
                    String[] parts = value.split("/");
                    namess.add(parts[parts.length - 1]);
                    listeSonPath.add(value);
                }
            }
        }

        alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.custom, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Son de notification");
        ListView listViewSon = (ListView) convertView.findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,namess);
        listViewSon.setAdapter(adapter);
        alertDialog.show();


        // Select Item in listView
    final ListView listTemp = listViewSon;
        listViewSon.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = (String) listTemp.getItemAtPosition(position);

                for (int i=0;i<parent.getChildCount();i++){
                    parent.getChildAt(i).setBackgroundColor(Color.rgb(238,240,231));
                }
                view.setBackgroundColor(Color.rgb(204,233,243));

                Toast.makeText(getApplicationContext(), "Sonnerie : "+item, Toast.LENGTH_SHORT).show();
                chemin_son = listeSonPath.get(position);
            }
        });

    }

    public void GetSon(View v){

        TextView sonPath = (TextView) findViewById(R.id.textView_sonPath_id);
        String[] parts = chemin_son.split("/");
        String titreSonnerie = parts[parts.length - 1];
        String titreSonnerieAfficher="";
       try{
           for (int i=0 ; i<=15 ; i++){
               titreSonnerieAfficher = titreSonnerieAfficher+titreSonnerie.charAt(i);
           }
       }catch(Exception e){}

        sonPath.setText(titreSonnerieAfficher+"...");
        alertDialog.dismiss();
    }

    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<String>();
        private Context context;

        public MyCustomAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.reminder_list_text_view);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.reminder_list_delete_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                list.remove(position); //or some other task
                notifyDataSetChanged();
            }
        });

        return view;
    }
}



}
