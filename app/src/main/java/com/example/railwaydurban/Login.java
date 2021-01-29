package com.example.railwaydurban;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    //Variables
    DbContext DB;
    Button registerButton;
    private TextInputEditText emailaddress, password;
    private Button signin, register, forgotpassword;
    private TextView numattempts;
    boolean isValid = false;
    private int counter = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //connect to DB
        DB = new DbContext(this);

        //initialize xml controls
        registerButton = (Button) findViewById(R.id.registerButton);
        signin = (Button) findViewById(R.id.signinButton);
        emailaddress = findViewById(R.id.emailText);
        password = findViewById(R.id.passwordText);
        signin = findViewById(R.id.signinButton);
        register = findViewById(R.id.registerButton);
        forgotpassword = findViewById(R.id.forgotButton);
        numattempts = findViewById(R.id.errorText);
        numattempts.setVisibility(View.INVISIBLE);

        try{
            // Retrieve shared preferences
            SharedPreferences sharedPreferences1 = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
            String userEmail = sharedPreferences1.getString("Email", "");
            emailaddress.setText(userEmail);
        }
        catch (Exception e)
        {
            Log.e("Shared Pref",e.toString());
        }

        //Enable register button to navigate to registration screen
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save email in shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Email", emailaddress.getText().toString());
                editor.commit();

                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });

        //Enables forgot password button to navigate to forget password screen
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Forgot_Password.class);
                startActivity(intent);
            }
        });

        //Signin button
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numattempts.setVisibility(View.INVISIBLE);

                //get user input
                String inputemail = emailaddress.getText().toString();
                String inputpassword = password.getText().toString();

                if (inputemail.isEmpty()) {
                    emailaddress.setError("Field cannot be empty!");
                    Toast.makeText(Login.this, "Please enter all the required details!", Toast.LENGTH_SHORT).show();
                } else if (inputpassword.isEmpty()) {
                    password.setError("Field cannot be empty!");
                    Toast.makeText(Login.this, "Please enter all the required details!", Toast.LENGTH_SHORT).show();
                } else {
                    if (validate()) {
                        // save email in shared preferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("Email", emailaddress.getText().toString());
                        editor.commit();
                        if (emailaddress.getText().toString().contains("Admin") || emailaddress.getText().toString().contains("admin")) {
                            Toast.makeText(Login.this, "Sign in Successful!", Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(Login.this, Admin_Menu.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(Login.this, "Sign in Successful!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(Login.this, Search_Booking.class);
                            startActivity(i);
                        }
                    } else if (isValid) {
                        counter--;
                        numattempts.setVisibility(View.VISIBLE);
                        numattempts.setText("Number of attempts left: " + counter);
                        if (counter == 0) {
                            lockOutTimer();
                            // disable Login button if limit exceeded
                            signin.setEnabled(false);
                            int colorblack = Color.GRAY;
                            signin.setBackgroundColor(colorblack);
                        }
                    }
                }
            }
        });
    }

    //search user table in db to authenticate
    private boolean validate() {
        DB = new DbContext(Login.this);
        Cursor res = DB.checkCredentials(emailaddress.getText().toString(), password.getText().toString());
        if (res.getCount() == 0) {
            Toast.makeText(Login.this, "Email or password incorrect!", Toast.LENGTH_LONG).show();
            isValid = true;
            return false;
        }
        return true;
    }

    //lock out timer for too many incorrect password attempts
    public void lockOutTimer() {
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                numattempts.setVisibility(View.VISIBLE);
                numattempts.setText("Please wait: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                numattempts.setVisibility(View.VISIBLE);
                numattempts.setText("Please Retry!");
                signin.setEnabled(true);
                int color = Color.parseColor("#091455");
                signin.setBackgroundColor(color);
                counter = 5;
            }
        }.start();
    }
}