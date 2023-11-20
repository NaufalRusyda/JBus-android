package com.naufalRusydaJBusRD.jbus_android;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutMeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        // Initialize components
        TextView usernameTextView = findViewById(R.id.username);
        TextView emailTextView = findViewById(R.id.email);
        TextView balanceTextView = findViewById(R.id.balance);

        // Replace "Your Name" with your actual name
        usernameTextView.setText("Naufal");

        // Replace "your_email@example.com" with your actual email address
        emailTextView.setText("naufal@mail.com");

        // Replace "1000" with your initial balance
        balanceTextView.setText("1000");
    }
}