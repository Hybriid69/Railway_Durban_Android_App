package com.example.railwaydurban;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Contact extends AppCompatActivity {
    private EditText subjectText,messageText;
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        subjectText = findViewById(R.id.subjectText);
        messageText = findViewById(R.id.messageText);
        sendButton = findViewById(R.id.registerButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }
    private void sendEmail() {
        //Receiver
        String recipientList = "farmworks69@gmail.com";
        //Receiver list
        String[] recipients = recipientList.split(",");
        String subject = subjectText.getText().toString();
        String message = messageText.getText().toString();
        //Add details to intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("message/rfc822");
        //Choose mail client
        startActivity(Intent.createChooser(intent, "Choose an email client"));
        subjectText.getText().clear();
        messageText.getText().clear();
    }
}
