package com.example.railwaydurban;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

public class Registration extends AppCompatActivity {
    //Variables
    DbContext DB;
    private Button register;
    private TextInputEditText firstname, lastname, emailaddress, password, phonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        DB = new DbContext(this);

        //Links to all xml elements in activity_regristration.xml
        firstname = findViewById(R.id.subjectText);
        lastname = findViewById(R.id.lname);
        emailaddress = findViewById(R.id.emailText);
        password = findViewById(R.id.passwordText);
        phonenumber = findViewById(R.id.pnumber);
        register = (Button) findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validate input fields
                if (!validateName()) {
                    Toast.makeText(Registration.this, "Name error", Toast.LENGTH_LONG).show();
                } else if (!validateLastname()) {
                    Toast.makeText(Registration.this, "Last name error ", Toast.LENGTH_LONG).show();
                } else if (!validateEmail()) {
                    Toast.makeText(Registration.this, "Email error", Toast.LENGTH_LONG).show();
                } else if (!isValidPass(password.getText().toString())) {
                    password.setError("Passwords must be minimum 5 characters, 1 Uppercase Letter, 1 Number and 1 Special Character ");
                    Toast.makeText(Registration.this, "Password weak", Toast.LENGTH_LONG).show();
                } else if (!validatePhoneNo()) {
                    Toast.makeText(Registration.this, "Phone number error", Toast.LENGTH_LONG).show();
                } else {
                    // save shared preferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Email", emailaddress.getText().toString());
                    editor.commit();
                    registerUser();

                }

            }
        });
    }

    //Validation methods
    private Boolean validateName() {
        String val = firstname.getText().toString();

        if (val.isEmpty()) {
            firstname.setError("Field cannot be empty");
            return false;
        } else {
            firstname.setError(null);
            return true;
        }
    }

    private Boolean validateLastname() {
        String val = lastname.getText().toString();

        if (val.isEmpty()) {
            lastname.setError("Field cannot be empty");
            return false;
        } else {
            lastname.setError(null);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = emailaddress.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        DB = new DbContext(Registration.this);
        Cursor res = DB.validateEmail(val);
        if (val.isEmpty()) {
            emailaddress.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            emailaddress.setError("Please enter a valid email address");
            return false;
        }
        if (res.getCount() > 0) {
            emailaddress.setError("Email Address already in use");
            return false;
        } else {
            emailaddress.setError(null);
            return true;
        }
    }

    private Boolean validatePhoneNo() {
        String val = phonenumber.getText().toString();

        if (val.isEmpty()) {
            phonenumber.setError("Field cannot be empty");
            return false;
        } else if (!(val.length() >= 10)) {
            phonenumber.setError("Phone Number must be 10 Digits");
            return false;
        } else {
            phonenumber.setError(null);
            return true;
        }
    }

    public static boolean isValidPass(final String pass) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{5,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(pass);
        return matcher.matches();
    }

    //This function will execute when user click on Register Button
    public void registerUser() {

        boolean isInserted = DB.insertRegistrationData(firstname.getText().toString(), lastname.getText().toString(), emailaddress.getText().toString(), password.getText().toString(), phonenumber.getText().toString());

        if (isInserted == true) {
            Toast.makeText(Registration.this, "Registration Successful", Toast.LENGTH_LONG).show();
            Intent i = new Intent(Registration.this, Search_Booking.class);
            startActivity(i);
        } else {
            Toast.makeText(Registration.this, "Registration Failed", Toast.LENGTH_LONG).show();
        }

    }

}