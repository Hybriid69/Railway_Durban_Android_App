package com.example.railwaydurban;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.Random;

public class Booking_Confirmation extends AppCompatActivity {

    //Paypal  request code
    public static final int PAYPAL_REQUEST_CODE = 123;
    public static final String PAYPAL_CLIENT_ID = "AcRirgJjMBhC4xjTuJdIYYEN_MC49aWcUP_s2Z7QKkzuxb-iuJ1FeDbpB7hkrfxdVYiFRqMnqQQunUza";

    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration() // Start with paypal sandbox environment
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PAYPAL_CLIENT_ID)
            .merchantName("Railway Durban")
            .merchantPrivacyPolicyUri(
                    Uri.parse("https://www.paypal.com/webapps/mpp/ua/privacy-full"))
            .merchantUserAgreementUri(
                    Uri.parse("https://www.paypal.com/webapps/mpp/ua/useragreement-full"));
    String total, stationfrom, stationto, date, time, trainname, noadults, nokids, kidscost, adultscost, randomNumber;


    ///////////////////////////////////////////////////////  Booking confirmation variables - send to boarding pass
    public static final String Booking_STATIONFROM = "com.example.railwaydurban.Booking_STATIONFROM";
    public static final String Booking_STATIONTO = "com.example.railwaydurban.Booking_STATIONTO";
    public static final String Booking_DATE = "com.example.railwaydurban.Booking_DEPARTURE";
    public static final String Booking_TIME = "com.example.railwaydurban.Booking_TIME";
    public static final String Booking_TRAINNAME = "com.example.railwaydurban.Booking_TRAINNAME";
    public static final String Booking_NUMBERADULTS = "com.example.railwaydurban.Booking_NUMBERADULTS";
    public static final String Booking_NUMBERKID = "com.example.railwaydurban.Booking_NUMBERKIDS";
    public static final String Booking_TOTAL = "Com.example.railwaaydurban.Booking_TOTAL";
    public static final String Booking_TOTALADULTS = "Com.example.railwaaydurban.Booking_TOTALADULTS";
    public static final String Booking_TOTALKIDS = "Com.example.railwaaydurban.Booking_TOTALKIDS";
    /////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking__confirmation);
        Intent intentConfig = new Intent(this, PayPalService.class);
        intentConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intentConfig);

        Random rand = new Random();
        randomNumber = String.format("%05d", rand.nextInt(100000));

        // set data from search booking activity to xml controls
        final TextView emailText = (TextView) findViewById(R.id.emailText);
        TextView StationFrom = (TextView) findViewById(R.id.viewstationfrom);
        final TextView StationTo = (TextView) findViewById(R.id.viewstationto);
        TextView Departure = (TextView) findViewById(R.id.viewdate);
        TextView Time = (TextView) findViewById(R.id.viewtime);
        TextView TrainName = (TextView) findViewById(R.id.viewtrainname);
        TextView NoAdults = (TextView) findViewById(R.id.viewadults);
        TextView NoKids = (TextView) findViewById(R.id.viewkids);
        TextView KidsCost = (TextView) findViewById(R.id.viewtotalkids);
        TextView AdultsCost = (TextView) findViewById(R.id.viewtotaladults);
        TextView TotalCost = (TextView) findViewById(R.id.viewtotal);

        //Calls variables from Booking confirmation screen
        final Intent intent = getIntent();
        stationfrom = intent.getStringExtra(Search_Booking.EXTRA_STATIONFROM);
        stationto = intent.getStringExtra(Search_Booking.EXTRA_STATIONTO);
        date = intent.getStringExtra(Search_Booking.EXTRA_DATE);
        time = intent.getStringExtra(Search_Booking.EXTRA_TIME);
        trainname = intent.getStringExtra(Search_Booking.EXTRA_TRAINNAME);
        noadults = intent.getStringExtra(Search_Booking.EXTRA_NUMBERADULTS);
        nokids = intent.getStringExtra(Search_Booking.EXTRA_NUMBERKID);
        kidscost = intent.getStringExtra(Search_Booking.EXTRA_TOTALKIDS);
        adultscost = intent.getStringExtra(Search_Booking.EXTRA_TOTALADULTS);
        total = intent.getStringExtra(Search_Booking.EXTRA_TOTAL);

        // save details in shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("pdfDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("stationfrom", stationfrom);
        editor.putString("stationto", stationto);
        editor.putString("trainname", trainname);
        editor.putString("date", time);
        editor.putString("time", time);
        editor.putString("noadults", noadults);
        editor.putString("nokids", nokids);
        editor.putString("adultscost", adultscost);
        editor.putString("kidscost", kidscost);
        editor.putString("total", total);
        editor.commit();

        // Retrieve shared preferences
        SharedPreferences sh = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        String userEmail = sh.getString("Email", "");

        //assigning the variables called from Search_Booking to textviews in Booking_Confirmation
        emailText.setText(userEmail.toString());
        StationFrom.setText(stationfrom);
        StationTo.setText(stationto);
        Departure.setText(date);
        Time.setText(time);
        TrainName.setText(trainname);
        NoAdults.setText(noadults);
        NoKids.setText(nokids);
        KidsCost.setText("R " + kidscost);
        AdultsCost.setText("R " + adultscost);
        TotalCost.setText("R " + total);

        //Initiate the button
        Button pay = (Button) findViewById(R.id.logoutButton);
        Button back = (Button) findViewById(R.id.backButton);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPayment();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Booking_Confirmation.this, Search_Booking.class);
                startActivity(i);
                ;
            }
        });
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getPayment() {
        //convert to Rands
        double finalTotal = Double.parseDouble(String.valueOf(total)) * 0.058;
        //Create paypal payment
        PayPalPayment payment = new PayPalPayment(new
                BigDecimal(finalTotal), "USD", "Train Ticket", PayPalPayment.PAYMENT_INTENT_SALE);

        //Create Paypal Payment activity
        Intent intent = new Intent(Booking_Confirmation.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Generate invoice number
        payment.invoiceNumber(randomNumber);

        //Adding payment detail to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        //Starting the intent activity for result
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //check the result is OK
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                Log.d("CONFIRM", String.valueOf(confirm));

                //check if confirmation is not null
                if (confirm != null) {
                    try {
                        //Get payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.d("paymentExample", paymentDetails);
                        Log.i("paymentExample", paymentDetails);
                        Log.d("Pay Confirm : ", String.valueOf(confirm.getPayment().toJSONObject()));

                        // Starting a PaymentResult activity to show the payment details and status to show
                        startActivity(new Intent(Booking_Confirmation.this, PaymentResult.class).putExtra("PaymentDetails", paymentDetails));

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred : ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}
