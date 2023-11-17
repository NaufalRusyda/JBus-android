package com.naufalRusydaJBusRD.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    // Define the variables
    private Button registerNow = null;
    private Button LoginButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        // Load the components to the variables
        registerNow = findViewById(R.id.register_button);
        LoginButton = findViewById(R.id.login_button);

        registerNow.setOnClickListener(v -> {
            moveActivity(this, RegisterActivity.class);
            viewToast(this, "Register akun anda");
        });

        LoginButton.setOnClickListener(v -> {
            moveActivity(this, MainActivity.class);
            viewToast(this, "Selamat datang");
        });
    }

    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }

    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }


}