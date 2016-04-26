package com.agenda.ter.smartgenda;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class NotificationActivity extends AppCompatActivity {

    public DatePickerDialog dpd;

    public ArrayList<String> list;
    public MyCustomAdapter adapter;
    public EditText txtInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ListView listView = (ListView) findViewById(R.id.listView_id);
        txtInput = (EditText) findViewById(R.id.editText_heure_id);

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

    }

    public void showDatePicker(View view) {
        dpd.show();
    }

    public void AddToList(View v) {
        String newItem = txtInput.getText().toString();
        list.add("Rappel "+list.size()+" : "+newItem);
        adapter.notifyDataSetChanged();
    }

    public void testListView (View v){

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
