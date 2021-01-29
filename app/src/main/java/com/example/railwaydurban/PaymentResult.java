package com.example.railwaydurban;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PaymentResult extends AppCompatActivity {

    Button downloadPDF, backButton;
    DbContext DB;
    String total, stationfrom, stationto, date, time, trainname, noadults, nokids, kidscost, adultscost, userEmail, uName, uSurname;
    static String status;
    Intent intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);
        downloadPDF = (Button) findViewById(R.id.buttonPDF);
        backButton = (Button) findViewById(R.id.buttonBack);
        DB = new DbContext(this);
        //Getting Intent
        intent1 = getIntent();

        // Retrieve shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("Email", "");

        // Retrieve shared preferences
        SharedPreferences detailShared = getSharedPreferences("pdfDetails", MODE_PRIVATE);

        stationfrom = detailShared.getString("stationfrom", "");
        stationto = detailShared.getString("stationto", "");
        trainname = detailShared.getString("trainname", "");
        date = detailShared.getString("date", "");
        time = detailShared.getString("time", "");
        noadults = detailShared.getString("noadults", "");
        nokids = detailShared.getString("nokids", "");
        adultscost = detailShared.getString("adultscost", "");
        kidscost = detailShared.getString("kidscost", "");
        total = detailShared.getString("total", "");

        //get Json data
        try {
            JSONObject jsonDetails = new JSONObject(intent1.getStringExtra("PaymentDetails"));
            //Displaying payment details
            showDetails(jsonDetails.getJSONObject("response"));
        } catch (JSONException e) {
            Log.e("Payment Details", e.toString());
        }
        // make booking if state approved
        try {
            JSONObject jsonDetails = new JSONObject(intent1.getStringExtra("PaymentDetails"));
            showDetails(jsonDetails.getJSONObject("response"));

            if (status.contains("approved")) {
                addBooking();
            }
        } catch (JSONException e) {
            Log.e("JSON ", e.toString());
        }
        //subtract available seats
        try {
            subtractSeats();
        } catch (Exception e) {
            Log.e("Seating Subtraction", e.toString());
        }

        downloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check device permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        new exportClass().execute();
                    } else {
                        Toast.makeText(PaymentResult.this, "Access to storage denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(PaymentResult.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PaymentResult.this, Schedules.class);
                startActivity(i);
            }
        });
    }


    private void showDetails(JSONObject jsonDetails) throws JSONException {
        //Views
        TextView textViewId = (TextView) findViewById(R.id.paymentId);
        TextView textViewStatus = (TextView) findViewById(R.id.paymentStatus);

        //Showing the paypal payment details and status from json object
        textViewId.setText(jsonDetails.getString("id"));
        textViewStatus.setText(jsonDetails.getString("state"));
        status=jsonDetails.getString("state");
        //TODO Test
        if (textViewStatus.getText().toString().contains(" "))
        {
            // disable boarding pass button id state is not approved
            downloadPDF.setVisibility(View.INVISIBLE);
            Toast.makeText(PaymentResult.this, "Transaction Error", Toast.LENGTH_LONG).show();
        }else if(status.length()==0)
        {
            downloadPDF.setVisibility(View.INVISIBLE);
            Toast.makeText(PaymentResult.this, "Transaction Error", Toast.LENGTH_LONG).show();
        }
    }

    //make booking
    public void addBooking() {
        boolean isInserted = DB.insertBookingData(userEmail, trainname, date, time, stationfrom, stationto, Integer.parseInt(String.valueOf(noadults)), Integer.parseInt(String.valueOf(nokids)), Float.parseFloat(total));

        if (isInserted == true) {
            Log.e("Booking ", "Booking Successful");
        } else {
            Log.e("Booking ", "Booking Failed- Internal Error");
        }
    }

    // check available seats
    public int checkSeats() {
        Cursor res = DB.getAvailableSeats(trainname);
        res.moveToFirst();
        if (res.getCount() == 0) {
            Log.e("Seat Check ", "Seat check failed");
            return 0;
        }
        int seats = Integer.parseInt(res.getString(0));
        return seats;
    }

    //updates seats
    public void subtractSeats() {
        try {
            int seatsSelected = Integer.parseInt(noadults) + Integer.parseInt(nokids);
            int finalSeats = checkSeats() - seatsSelected;
            Log.e("Final Seats", String.valueOf(finalSeats));

            Boolean res = DB.updateAvailableSeats(trainname, finalSeats);
            if (res) {
                Log.e("Seat", "Subtracted seats successfully");
            } else {
                Log.e("Seat", "Could not subtract seats");
            }
        } catch (Exception e) {
            Log.e("Seat exception", e.toString());
        }
    }

    // get user details
    public void getDetails() {
        Cursor res = DB.getUserDetails(userEmail);
        // res.moveToFirst();
        if (res.getCount() > 0) {
            while (res.moveToNext()) {
                uName = res.getString(1);
                uSurname = res.getString(2);
            }
        }
    }

    //////////////////// export pdf
    private class exportClass extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            getDetails();
            final String text = userEmail;
            final String textName = "Name: " + uName;
            final String textSurname = "Surname: " + uSurname;
            final String text1 = "Train Name: " + trainname;
            final String text2 = "Departure: " + stationfrom;
            final String text3 = "Destination: " + stationto;
            final String text4 = "Date: " + date;
            final String text5 = "Time: " + time;
            final String text6 = "No. of Adults: " + noadults;
            final String text7 = "No. of Kids: " + nokids;
            final String text8 = "Adult Cost: " + adultscost;
            final String text9 = "Kids Cost: " + kidscost;
            final String text10 = "Total: " + total;
            //new exportClass().execute(text);
            // create a new document
            PdfDocument document = new PdfDocument();

            // Page description
            PdfDocument.PageInfo pageDetails = new PdfDocument.PageInfo.Builder(300, 600, 1).create();

            // start page
            PdfDocument.Page pageStart = document.startPage(pageDetails);
            Canvas canvas = pageStart.getCanvas();
            //paint for title
            Paint title = new Paint();
            title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            title.setTextSize(20);

            //paint for heading
            Paint head = new Paint();
            head.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            head.setTextSize(18);

            //paint for body text
            Paint paint = new Paint();
            paint.setTextSize(15);

            canvas.drawText("Railway Durban", 50, 50, title);
            canvas.drawText("Boarding Pass", 50, 70, head);
            canvas.drawText(text, 50, 100, paint);
            canvas.drawText(textName, 50, 120, paint);
            canvas.drawText(textSurname, 50, 140, paint);
            canvas.drawText(text1, 50, 160, paint);
            canvas.drawText(text2, 50, 180, paint);
            canvas.drawText(text3, 50, 200, paint);
            canvas.drawText(text4, 50, 220, paint);
            canvas.drawText(text5, 50, 240, paint);
            canvas.drawText(text6, 50, 260, paint);
            canvas.drawText(text7, 50, 280, paint);
            canvas.drawText(text8, 50, 300, paint);
            canvas.drawText(text9, 50, 320, paint);
            canvas.drawText(text10, 50, 340, paint);
            // finish the page
            document.finishPage(pageStart);

            // write the document content to storage
            String directoryPath = Environment.getExternalStorageDirectory().getPath() + "/RailwayDurban/";
            File file = new File(directoryPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            //doc name
            String targetPdf = directoryPath + "Boarding-Pass.pdf";
            File filePath = new File(targetPdf);
            try {
                document.writeTo(new FileOutputStream(filePath));
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(PaymentResult.this, "Boarding pass downloaded", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (IOException e) {
                Log.e("PDF Export", "Error: " + e.toString());
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(PaymentResult.this, "Failed could not download", Toast.LENGTH_LONG).show();
                    }
                });
            }
            // close the document
            document.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

}