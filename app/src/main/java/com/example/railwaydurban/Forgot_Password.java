package com.example.railwaydurban;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Forgot_Password extends AppCompatActivity {
    private TextInputEditText resetemail;
    private TextView textViewemail2, otpText, textViewemail3;
    private Button recoveryButton, otpButton;
    String inputresetemail;
    String randNumber;
    boolean sent = false;
    int counter = 3;
    DbContext DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot__password);
        DB = new DbContext(this);

        resetemail = (TextInputEditText) findViewById(R.id.emailText);
        otpButton = (Button) findViewById(R.id.otpButton);
        recoveryButton = (Button) findViewById(R.id.recoveryButton);
        textViewemail2 = (TextView) findViewById(R.id.textViewemail2);
        textViewemail3 = (TextView) findViewById(R.id.textViewemail3);
        otpText = (TextView) findViewById(R.id.otpText);

        otpButton.setVisibility(View.INVISIBLE);
        otpText.setVisibility(View.INVISIBLE);
        textViewemail3.setVisibility(View.INVISIBLE);

        // check permissions on device
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        recoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewemail2.setText("");
                //Generate random OTP
                Random random = new Random();
                randNumber = String.format("%04d", random.nextInt(10000));

                //Validate Fields
                inputresetemail = resetemail.getText().toString();
                if (inputresetemail.equals("")) {
                    resetemail.setError("Please enter a registered email address");
                    Toast.makeText(Forgot_Password.this, "Please enter a registered email address", Toast.LENGTH_LONG).show();
                } else if (!validateEmail()) {
                    Toast.makeText(Forgot_Password.this, "Email address does not exist", Toast.LENGTH_LONG).show();
                } else {
                    otpText.setText("");
                    try{
                        asyncOperation op = new asyncOperation();
                        op.doInBackground();
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(Forgot_Password.this, "Check Internet connection", Toast.LENGTH_LONG).show();
                    }
                    if (sent) {
                        counter--;
                        textViewemail2.setText("Please check email for OTP");

                        otpButton.setVisibility(View.VISIBLE);
                        otpText.setVisibility(View.VISIBLE);
                        textViewemail3.setVisibility(View.VISIBLE);
                        if (counter == 0) {
                            recoveryButton.setEnabled(false);
                            textViewemail2.setText("Maximum limits exceeded");
                        }
                    }
                }

            }
        });
        otpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(randNumber) == Integer.parseInt(otpText.getText().toString())) {
                    Toast.makeText(Forgot_Password.this, "Correct OTP", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Forgot_Password.this, ResetPassword.class);
                    startActivity(i);
                } else {
                    Toast.makeText(Forgot_Password.this, "Incorrect OTP", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    // Validate Email
    private Boolean validateEmail() {
        String val = resetemail.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        DB = new DbContext(Forgot_Password.this);
        Cursor res = DB.validateEmail(val);
        if (res.getCount() == 0) {
            resetemail.setError("Email Address does not exist");
            return false;
        } else if (val.isEmpty()) {
            resetemail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            resetemail.setError("Please enter a valid email address");
            return false;
        } else {
            resetemail.setError(null);
            return true;
        }
    }



    private final class asyncOperation extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            final String username = "farmworks69@gmail.com";
            final String password = ""; //Insert Password

            Properties props = new Properties();
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {
                //Build message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(inputresetemail));
                message.setSubject(" Railway Durban: Password Reset");
                message.setText("Dear User,"
                        + "\n\nReset Password One time code" +
                        "\n\n_______________________________________________________" +
                        "\n\n" +
                        "\n\n                     " + randNumber +
                        "\n\n________________________________________________________");
                
                Transport.send(message);
                Log.i("Email", "Sent");
                sent = true;

            } catch (MessagingException e) {
                Log.e("Email", e.toString());
            }
            return "Executed";
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(Forgot_Password.this, "Email sent", Toast.LENGTH_LONG).show();
        }
    }
}


