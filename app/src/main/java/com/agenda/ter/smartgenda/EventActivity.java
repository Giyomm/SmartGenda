package com.agenda.ter.smartgenda;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.Calendar;

public class EventActivity extends AppCompatActivity {

    public DatePickerDialog dpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ImageButton datepick = (ImageButton) findViewById(R.id.date_even_butonimage_id);
        EditText desc_even = (EditText)findViewById(R.id.desc_even_edittextt_id);

        Calendar calendar = Calendar.getInstance();
        dpd = new DatePickerDialog(EventActivity.this, new DatePickerDialog.OnDateSetListener() {
            TextView datepickertxtview = (TextView)findViewById(R.id.datepicker_textview_id);
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                datepickertxtview.setText(dayOfMonth + "/" + (monthOfYear+1) + "/"+ year);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }


    public void showDatePicker(View view) {
        dpd.show();
    }
}
