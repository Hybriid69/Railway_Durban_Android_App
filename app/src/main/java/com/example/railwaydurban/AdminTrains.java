package com.example.railwaydurban;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminTrains extends AppCompatActivity {
    DbContext DB;
    Button Add;
    EditText name, passengers, seats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_trains);
        DB = new DbContext(this);
        Add = (Button) findViewById(R.id.buttonAdd);
        name = (EditText) findViewById(R.id.noTrainName);
        passengers = (EditText) findViewById(R.id.noPassengers);
        seats = (EditText) findViewById(R.id.noSeats);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateTrainName() && validatePass() && validateAvailability()) {
                    addData();
                }
            }
        });

    }

    ////////// check if train name already exists
    private Boolean validateTrainName() {
        String tName = name.getText().toString();
        DB = new DbContext(AdminTrains.this);
        Cursor res = DB.checkTrainNames(tName);
        if (name.getText().toString().isEmpty()) {
            name.setError("Field cannot be empty");
            return false;
        }
        if (res.getCount() > 0) {
            name.setError("Train name already in use");
            return false;
        } else {
            name.setError(null);
            return true;
        }
    }

    public Boolean validatePass() {
        try {
            Integer checkPassengers = Integer.parseInt(String.valueOf(passengers.getText()));
        } catch (Exception e) {
            passengers.setError("Field cannot be empty");
            Log.e("Train Passengers", e.toString());
            return false;
        }
        return true;
    }

    public Boolean validateAvailability() {
        try {
            Integer checkPassengers = Integer.parseInt(String.valueOf(passengers.getText()));
            Integer Checkseat = Integer.parseInt(String.valueOf(seats.getText()));
            if (checkPassengers < Checkseat) {
                Toast.makeText(AdminTrains.this, "Available seats cannot be more than Capacity!", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (Exception e) {
            seats.setError("Field cannot be empty");
            Log.e("Train Seats", e.toString());
            return false;
        }
        return true;
    }

    public void addData() {
        boolean isInserted = DB.insertTrainData(name.getText().toString(), Integer.parseInt(passengers.getText().toString()), Integer.parseInt(seats.getText().toString()));

        if (isInserted == true) {
            Toast.makeText(AdminTrains.this, "Train Details Successfully Added", Toast.LENGTH_LONG).show();
            name.getText().clear();
            passengers.getText().clear();
            seats.getText().clear();
        } else
            Toast.makeText(AdminTrains.this, "Insert Failed", Toast.LENGTH_LONG).show();

    }

}