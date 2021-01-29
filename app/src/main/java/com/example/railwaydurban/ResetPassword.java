package com.example.railwaydurban;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

public class ResetPassword extends AppCompatActivity {
    DbContext DB;
    TextInputEditText pass, confirmPass;
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        DB = new DbContext(this);
        pass = (TextInputEditText) findViewById(R.id.passText);
        confirmPass = (TextInputEditText) findViewById(R.id.passConfirmText);
        reset = (Button) findViewById(R.id.resetButton);


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isValidPass(pass.getText().toString()) && pass.getText().toString().length() <5){
                    pass.setError("Passwords must be minimum 5 characters, 1 Uppercase Letter, 1 Number and 1 Special Character ");
                    Toast.makeText(ResetPassword.this, "Password weak", Toast.LENGTH_LONG).show();
                }
                if (!pass.getText().toString().matches(confirmPass.getText().toString()))
                {
                    pass.setError("Passwords do not match");
                    confirmPass.setError("Passwords do not match");
                    Toast.makeText(ResetPassword.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }
                else {
                    resetPass();
                }
            }
        });

    }

    public void resetPass() {
        // Retrieve shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("Email", "");

        Boolean res = DB.updatePassword(userEmail.toString(), pass.getText().toString());
        if (res) {
            Toast.makeText(ResetPassword.this, "Password Reset Successful", Toast.LENGTH_LONG).show();
            Intent i = new Intent(ResetPassword.this,Login.class);
            startActivity(i);
        }else {
            Toast.makeText(ResetPassword.this, "Password Reset Failed", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isValidPass(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{5,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}