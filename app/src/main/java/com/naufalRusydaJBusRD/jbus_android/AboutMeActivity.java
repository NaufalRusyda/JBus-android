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
        String name = "Kaufal";
        usernameTextView.setText(name);

        // Replace "your_email@example.com" with your actual email address
        emailTextView.setText("naufal@mail.com");

        // Replace "1000" with your initial balance
        balanceTextView.setText("1000");

        // Update the initial letter in the circle with the first letter of the name
        TextView initialTextView = findViewById(R.id.initial);
        if (name.length() > 0) {
            initialTextView.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }
    }
}
