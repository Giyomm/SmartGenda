package com.agenda.ter.smartgenda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
    }

    public void goToEventActivity(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);

    }
}
