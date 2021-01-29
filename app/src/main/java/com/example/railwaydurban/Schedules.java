package com.example.railwaydurban;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class Schedules extends AppCompatActivity {

    Spinner spinnerSchedule;
    DbContext myDB;
    Button buttonBook, buttonContact;
    TableLayout table;
    LinearLayout l1, l2, l3, l4, l5;
    List<String> trains = new ArrayList<String>();
    List<String> date = new ArrayList<String>();
    List<String> dateBackUp = new ArrayList<String>();
    List<String> time = new ArrayList<String>();
    List<String> depart = new ArrayList<String>();
    List<String> dest = new ArrayList<String>();
    List<String[]> dateNo = new ArrayList<String[]>();
    int color = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules);
        buttonBook = (Button) findViewById(R.id.buttonBook);
        buttonContact = (Button) findViewById(R.id.contactButton);

        l1 = (LinearLayout) findViewById(R.id.l1);
        l2 = (LinearLayout) findViewById(R.id.l2);
        l3 = (LinearLayout) findViewById(R.id.l3);
        l4 = (LinearLayout) findViewById(R.id.l4);
        l5 = (LinearLayout) findViewById(R.id.l5);

        buttonContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Schedules.this, Contact.class);
                startActivity(i);
            }
        });

        buttonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
                String userEmail = sharedPreferences.getString("Email", "");
                // route user to correct activity
                if (userEmail.length() > 0) {
                    if (userEmail.equals("Admin")) {
                        Toast.makeText(Schedules.this, userEmail + " already logged in", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(Schedules.this, Admin_Menu.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(Schedules.this, userEmail + " already logged in", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(Schedules.this, Search_Booking.class);
                        startActivity(i);
                    }
                } else {
                    Intent i = new Intent(Schedules.this, Login.class);
                    startActivity(i);
                }
            }
        });
        //display schedule
        showData();

    }

    public void showData() {
        String newDate="";
        myDB = new DbContext(Schedules.this);
        Cursor res = myDB.getScheduleData();
        if (res.getCount() == 0) {
            //Toast.makeText(Schedules.this, "Nothing Found", Toast.LENGTH_LONG).show();
            return;
        }

        while (res.moveToNext()) {
            trains.add(res.getString(1));
            dateBackUp.add(res.getString(2));
            time.add(res.getString(3));
            depart.add(res.getString(4));
            dest.add(res.getString(5));

            TextView tv2 = new TextView(this);

            try {
                //convert month name to digit
                String[] colDate = res.getString(2).split(" ");
                date.add(colDate[1]);
                Date date = new SimpleDateFormat("MMMM", Locale.ENGLISH).parse(colDate[1]);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int month = cal.get(Calendar.MONTH);
                String monthNo = String.valueOf(month);

                //add zero infront of one digit month
                if (String.valueOf(month).length() <= 1) {
                    monthNo = "0" + monthNo;
                }
                // get 1st 2 digits
                String sub1 = res.getString(2).substring(0, 2);
                // get last 2 digits
                String sub2 = res.getString(2).substring(res.getString(2).length() - 2);

                //Set date number instead of words
                newDate = sub1 + "/" + monthNo + "/" + sub2;
                tv2.setText(newDate);

            } catch (ParseException e) {
                tv2.setText(res.getString(2) + "  ");
                Log.e("Date", e.toString());
            }

            //add data to xml display controls
            int i = 1;
            TextView tv = new TextView(this);
            tv.setText(res.getString(1) + "   ");
            tv.setTextSize(14);
            tv.setId(i);
            tv.setTextColor(color);
            l1.addView(tv);

            tv2.setTextSize(14);
            tv2.setId(i);
            tv2.setTextColor(color);
            l2.addView(tv2);

            TextView tv3 = new TextView(this);
            tv3.setText(res.getString(3) + " ");
            tv3.setTextSize(14);
            tv3.setId(i);
            tv3.setTextColor(color);
            l3.addView(tv3);

            TextView tv4 = new TextView(this);
            tv4.setText(res.getString(4) + "    ");
            tv4.setTextSize(14);
            tv4.setId(i);
            tv4.setTextColor(color);
            l4.addView(tv4);

            TextView tv5 = new TextView(this);
            tv5.setText(res.getString(5));
            tv5.setTextSize(14);
            tv5.setId(i);
            tv5.setTextColor(color);
            l5.addView(tv5);
            i++;
        }
    }
}