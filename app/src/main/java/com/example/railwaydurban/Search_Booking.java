package com.example.railwaydurban;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class Search_Booking extends AppCompatActivity {
    private Spinner spinnerto, spinnerDeparture, spinnerkid, spinneradult, spinnerName, spinnertime, spinnerDate;
    private Button Bookbutton, logoutButton;
    List<String> Date = new ArrayList<>();
    List<String> trainNameList = new ArrayList<>();
    List<String> trainDeparturesList = new ArrayList<>();
    List<String> trainDestinationList = new ArrayList<>();
    List<String> trainDateList = new ArrayList<>();
    List<String> trainTimesList = new ArrayList<>();
    List<Integer> numbers = new ArrayList<>();
    DbContext DB;
    ///////////////////////////////////////////////////////  Booking confirmation variables
    public static final String EXTRA_STATIONFROM = "com.example.railwaydurban.EXTRA_STATIONFROM";
    public static final String EXTRA_STATIONTO = "com.example.railwaydurban.EXTRA_STATIONTO";
    public static final String EXTRA_DATE = "com.example.railwaydurban.EXTRA_DEPARTURE";
    public static final String EXTRA_TIME = "com.example.railwaydurban.EXTRA_TIME";
    public static final String EXTRA_TRAINNAME = "com.example.railwaydurban.EXTRA_TRAINNAME";
    public static final String EXTRA_NUMBERADULTS = "com.example.railwaydurban.EXTRA_NUMBERADULTS";
    public static final String EXTRA_NUMBERKID = "com.example.railwaydurban.EXTRA_NUMBERKIDS";
    public static final String EXTRA_TOTAL = "Com.example.railwaaydurban.EXTRA_TOTAL";
    public static final String EXTRA_TOTALADULTS = "Com.example.railwaaydurban.EXTRA_TOTALADULTS";
    public static final String EXTRA_TOTALKIDS = "Com.example.railwaaydurban.EXTRA_TOTALKIDS";
    /////////////////////////////////////////////////////////////////////////////////////////
    float adultcost, kidscost, totalcost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search__booking);
        DB = new DbContext(this);

        //Initiate the spinner view
        spinnerName = (Spinner) findViewById(R.id.spinnerline);
        spinnerDeparture = (Spinner) findViewById(R.id.spinnerfrom);
        spinnerto = (Spinner) findViewById(R.id.spinnerto);
        spinneradult = (Spinner) findViewById(R.id.spinneradult);
        spinnerkid = (Spinner) findViewById(R.id.spinnerkid);
        spinnertime = (Spinner) findViewById(R.id.spinnertime);
        spinnerDate = (Spinner) findViewById(R.id.spinnerDate);

        //Initialize search button
        Bookbutton = (Button) findViewById(R.id.bookButton);
        logoutButton = (Button) findViewById(R.id.logoutButton);

        // Add Numbers to Number of Tickets
        for (int i = 0; i < 6; i++) {
            numbers.add(i);
        }

        // set text to spinners
        ArrayAdapter<Integer> dataAdapter1 = new ArrayAdapter<Integer>(Search_Booking.this, android.R.layout.simple_spinner_item, numbers);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerkid.setAdapter(dataAdapter1);

        ArrayAdapter<Integer> dataAdapter2 = new ArrayAdapter<Integer>(Search_Booking.this, android.R.layout.simple_spinner_item, numbers);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinneradult.setAdapter(dataAdapter1);

        //calling methods
        showDeparturesOnSpinner();

        // Departure Spinner
        spinnerDeparture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showDestinationOnSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Train Names Spinner
        spinnerto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!trainNameList.isEmpty()) {
                    trainNameList.clear();
                }
                showTrainNamesOnSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Train dates on spinner
        spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showDateSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Train times on spinner
        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showTimesOnSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Bookbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnersQuantity()) {
                    if (checkSeats()) {
                        openBookingConfirmation();
                    }
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Search_Booking.this, Login.class);
                startActivity(i);
            }
        });

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //method to call variables in Booking_Confirmation
    public void openBookingConfirmation() {
        String stationfrom = spinnerDeparture.getSelectedItem().toString();
        String stationto = spinnerto.getSelectedItem().toString();
        String date = spinnerDate.getSelectedItem().toString();
        String time = spinnertime.getSelectedItem().toString();
        String trainname = spinnerName.getSelectedItem().toString();
        String adults = spinneradult.getSelectedItem().toString();
        String kids = spinnerkid.getSelectedItem().toString();

        Intent intent = new Intent(Search_Booking.this, Booking_Confirmation.class);
        intent.putExtra(EXTRA_STATIONFROM, stationfrom);
        intent.putExtra(EXTRA_STATIONTO, stationto);
        intent.putExtra(EXTRA_DATE, date);
        intent.putExtra(EXTRA_TIME, time);
        intent.putExtra(EXTRA_TRAINNAME, trainname);
        intent.putExtra(EXTRA_NUMBERADULTS, adults);
        intent.putExtra(EXTRA_NUMBERKID, kids);
        intent.putExtra(EXTRA_TOTALADULTS, CalcAdultCost());
        intent.putExtra(EXTRA_TOTALKIDS, CalckidsCost());
        intent.putExtra(EXTRA_TOTAL, CalcTotalCost());
        startActivity(intent);

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Calculates the total cost of travellers
    public String CalcTotalCost()
    {
        totalcost = (Float.parseFloat(CalcAdultCost()) + Float.parseFloat(CalckidsCost()));
        return (String.valueOf(totalcost));
    }

    //Calculates the total of adult travellers
    public String CalcAdultCost()
    {
        int adults = Integer.parseInt(spinneradult.getSelectedItem().toString());
        adultcost = 120 * adults;
        return (String.valueOf(adultcost));
    }

    //Calculates the total of kid travellers
    public String CalckidsCost()
    {
        int kids = Integer.parseInt(spinnerkid.getSelectedItem().toString());
        kidscost = 60 * kids;
        return (String.valueOf(kidscost));
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // validate no of passengers
    public boolean spinnersQuantity() {
        if (!spinneradult.getSelectedItem().toString().contains("0")) {
            return true;
        } else if (!spinneradult.getSelectedItem().toString().contains("0") && !spinnerkid.getSelectedItem().toString().contains("0")) {
            return true;
        } else if (!spinnerkid.getSelectedItem().toString().contains("0") && spinneradult.getSelectedItem().toString().contains("0")) {
            Toast.makeText(Search_Booking.this, "Kids cannot travel alone", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(Search_Booking.this, "Please select no. of passengers", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //SPINNER TRAIN NAME-  get list of departures based on user selected train name
    public void getDepartures()
    {
        Cursor res = DB.getTrainDepartures();

        if (res.getCount() == 0) {
            Toast.makeText(Search_Booking.this, "No available departures", Toast.LENGTH_LONG).show();
        }
        while (res.moveToNext()) {
            trainDeparturesList.add(res.getString(0)); //column 5 = Index =4
        }
    }

    // FROM SPINNER- Add data to from spinner
    public void showDeparturesOnSpinner() {
        getDepartures();
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(Search_Booking.this, android.R.layout.simple_spinner_item, trainDeparturesList);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeparture.setAdapter(dataAdapter1);
    }

//////////////////////////////////////////////////////////////////////////////////////
//method 2
//////////////////////////////////////////////////////////////////////////////////////
//// get destinations based on departures
    public void getDestination()
    {
        final String name = spinnerDeparture.getSelectedItem().toString();
        Cursor res = DB.getTrainDestination(name);
        if (res.getCount() == 0) {
            Toast.makeText(Search_Booking.this, "No available destinations", Toast.LENGTH_LONG).show();
        }
        while (res.moveToNext()) {
            trainDestinationList.add(res.getString(0));
        }
    }

    public void showDestinationOnSpinner() {
        getDestination();
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(Search_Booking.this, android.R.layout.simple_spinner_item, trainDestinationList);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerto.setAdapter(dataAdapter1);
    }
///////////////////////////////////////////////////////////////////////////////////////////////
    // method 3
////////////////////////////////////////////////////////////////////////////////////////////
// SPINNER TO - Get train names according to dates selected
    public void getTrainNames()
    {
        //Toast.makeText(Search_Booking.this, String.valueOf(dayOfMonth),Toast.LENGTH_LONG).show();
        String to = spinnerto.getSelectedItem().toString();
        String depart = spinnerDeparture.getSelectedItem().toString();
        Cursor res = DB.getTrainNamesfiltered(depart, to);
        if (res.getCount() == 0) {
            Toast.makeText(Search_Booking.this, "No Trains available to "+to, Toast.LENGTH_LONG).show();
        }
        while (res.moveToNext()) {
            trainNameList.add(res.getString(0));
        }
    }

    //  ADD TO TRAIN NAMES SPINNER- fill spinner with train names
    public void showTrainNamesOnSpinner()
    {
        getTrainNames();
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(Search_Booking.this, android.R.layout.simple_spinner_item, trainNameList);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerName.setAdapter(dataAdapter1);
    }

    ////////////////////////////////////////////////////////////////
    // METHOD 4
    /////////////////////////////////////////////////
    public void checkDate() {
        String to = spinnerto.getSelectedItem().toString();
        String from = spinnerDeparture.getSelectedItem().toString();
        String name = spinnerName.getSelectedItem().toString();
        Cursor res = DB.getTrainDepartureDates(name, from, to);
        if (res.getCount() == 0) {
            Toast.makeText(Search_Booking.this, "No departures dates available", Toast.LENGTH_LONG).show();
        }
        while (res.moveToNext()) {
            trainDateList.add(res.getString(0));
        }
    }

    //  ADD TO TRAIN NAMES SPINNER- fill spinner with train names
    public void showDateSpinner()
    {
        checkDate();
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(Search_Booking.this, android.R.layout.simple_spinner_item, trainDateList);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDate.setAdapter(dataAdapter1);
    }

    //////////////////////////////////////////////////////////////////////////////
    // METHOD 5
    /////////////////////////////////////////////////////////////////////////////
    public void checkTimes() {
        String to = spinnerto.getSelectedItem().toString();
        String from = spinnerDeparture.getSelectedItem().toString();
        String name = spinnerName.getSelectedItem().toString();
        String date = spinnerDate.getSelectedItem().toString();
        Cursor res = DB.getTrainDepartureTimes(name, date, from, to);
        if (res.getCount() == 0) {
           // Toast.makeText(Search_Booking.this, "Selected Time not available", Toast.LENGTH_LONG).show();
        }
        while (res.moveToNext()) {
            trainTimesList.add(res.getString(0));
        }
    }

    public void showTimesOnSpinner() {
        checkTimes();
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(Search_Booking.this, android.R.layout.simple_spinner_item, trainTimesList);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnertime.setAdapter(dataAdapter1);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // check available seats
    public boolean checkSeats() {
        String tname = spinnerName.getSelectedItem().toString();
        int seatsSelected = Integer.parseInt(spinneradult.getSelectedItem().toString()) + Integer.parseInt(spinnerkid.getSelectedItem().toString());

        Cursor res = DB.getAvailableSeats(tname);
        res.moveToFirst();
        if (res.getCount() > 0) {
            if (seatsSelected > Integer.parseInt(res.getString(0)) ) {
                Toast.makeText(Search_Booking.this, "Seats not available. "+Integer.parseInt(res.getString(0))+" seats remaining", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }
}




