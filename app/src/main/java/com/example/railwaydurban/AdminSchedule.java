package com.example.railwaydurban;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class AdminSchedule extends AppCompatActivity {
    DbContext myDB;
    Button addBtn;
    EditText departure, destination;
    Spinner trainName, spinnerTime;
    DatePicker date;
    List<String> TList = new ArrayList<>();
    List<String> timeList = new ArrayList<>();
    int hours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_schedule);
        // connect to DB
        myDB = new DbContext(this);
        // initialize xml controls
        trainName = (Spinner) findViewById(R.id.spinnerTrainName);
        spinnerTime = (Spinner) findViewById(R.id.spinnerTime);
        date = (DatePicker) findViewById(R.id.datePicker1);
        departure = (EditText) findViewById(R.id.trainDepart);
        destination = (EditText) findViewById(R.id.trainDestination);
        addBtn = (Button) findViewById(R.id.insertBtn);

        // get today's date
        final Calendar calendar = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        //Min date setting part
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.YEAR, year);
        date.setMinDate(cal.getTimeInMillis());

        //get current hour
        Calendar rightNow = Calendar.getInstance();
        final int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
        int currentMin = rightNow.get(Calendar.MINUTE);

        //Add 2 decimal places to time spinner
        NumberFormat numberFormat = new DecimalFormat("#0.00");

        // do not show current hour in spinner, instead show next hour

        if (date.getDayOfMonth() == day) {
            if (currentMin >= 01 || currentMin >= 1) {
                hours = currentHour;
                hours++;
            }
        }

        date.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (date.getDayOfMonth() > day) {
                    hours = currentHour;
                }
            }
        });

        while (hours < 25) {
            if (hours < 13) {
                timeList.add(numberFormat.format(hours) + " AM");
            } else {
                timeList.add(numberFormat.format(hours) + " PM");
            }
            hours++;
        }
        //display hours in time spinner
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(AdminSchedule.this, android.R.layout.simple_spinner_item, timeList);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(dataAdapter1);

        try {
            listTrainNames();
        } catch (Exception e) {
            Log.e("Schedule Train list", e.toString());
        }
        try {
            addData();
        } catch (Exception e) {
            Toast.makeText(AdminSchedule.this, "Please add a Train first", Toast.LENGTH_LONG).show();
            Log.e("Schedule Admin", e.toString());
        }
    }

    // retrieve train names from db
    public void listTrainNames() {
        myDB = new DbContext(this);
        Cursor res = myDB.getTrainNames();
        if (res.getCount() == 0) {
            Toast.makeText(AdminSchedule.this, "No Trains available", Toast.LENGTH_LONG).show();
        }
        while (res.moveToNext()) {
            TList.add(res.getString(0));
        }
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(AdminSchedule.this, android.R.layout.simple_spinner_item, TList);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trainName.setAdapter(dataAdapter1);
    }

    // insert schedule to DB
    public void addData() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                date.getDayOfMonth() + date.getMonth() +
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat monthName = new SimpleDateFormat("MMMM");
                int monthNumber = Integer.parseInt(String.valueOf(date.getMonth() + 1));
                cal.set(Calendar.MONTH, monthNumber);

                String name = trainName.getSelectedItem().toString();
                String Year = String.valueOf(date.getYear());
                String Month = monthName.format(cal.getTime());
                String Day = String.valueOf(date.getDayOfMonth());

                if (Day.length() <= 1) {
                    Day = "0" + Day;
                }

                String Date = Day + " " + Month + " " + Year;
                String time = spinnerTime.getSelectedItem().toString();

                boolean isInserted = myDB.insertScheduleData(name, Date.toString(), time, departure.getText().toString(), destination.getText().toString());

                if (isInserted == true) {
                    Toast.makeText(AdminSchedule.this, "Insert Successful", Toast.LENGTH_LONG).show();
                    spinnerTime.setSelection(0);
                    departure.getText().clear();
                    destination.getText().clear();
                } else
                    Toast.makeText(AdminSchedule.this, "Insert Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

}