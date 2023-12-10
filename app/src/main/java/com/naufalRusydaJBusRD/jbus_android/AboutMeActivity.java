package com.naufalRusydaJBusRD.jbus_android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.naufalRusydaJBusRD.jbus_android.model.Account;
import com.naufalRusydaJBusRD.jbus_android.model.BaseResponse;
import com.naufalRusydaJBusRD.jbus_android.request.BaseApiService;
import com.naufalRusydaJBusRD.jbus_android.request.UtilsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity to display information about the user's account and perform actions like top-up and registration.
 *
 * @author Naufal Rusyda Santosa
 * @version 1.0
 */
public class AboutMeActivity extends AppCompatActivity {

    // UI Components
    private Button topupButton;
    private Button renterButton;
    private TextView renterStatus;
    private Button renterButton2;
    private TextView renterStatus2;
    private BaseApiService mApiService;
    private Context mContext;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView balanceTextView;
    private TextView companyNameTextView;
    private TextView companyAddressTextView;
    private TextView phoneNumberTextView;
    private Account loggedAccount;
    private View companyView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        // Set the title of the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("My Account");
        }

        mContext = this;
        mApiService = UtilsApi.getApiService();

        // Check if the user is logged in
        loggedAccount = LoginActivity.loggedAccount;
        if (loggedAccount == null) {
            finish();
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize UI components
        usernameTextView = findViewById(R.id.aboutme_name);
        emailTextView = findViewById(R.id.aboutme_email);
        balanceTextView = findViewById(R.id.aboutme_balance);

        companyView = findViewById(R.id.company_detail);
        companyNameTextView = findViewById(R.id.aboutme_company_name);
        companyAddressTextView = findViewById(R.id.aboutme_company_address);
        phoneNumberTextView = findViewById(R.id.aboutme_company_phone);

        // Set the account data
        usernameTextView.setText(loggedAccount.name);
        emailTextView.setText(loggedAccount.email);
        balanceTextView.setText(String.valueOf(loggedAccount.balance));

        if (LoginActivity.loggedAccount.company != null) {
            companyNameTextView.setText(loggedAccount.company.companyName);
            companyAddressTextView.setText(loggedAccount.company.address);
            phoneNumberTextView.setText(String.valueOf(loggedAccount.company.phoneNumber));
        }

        // Update the initial letter in the circle with the first letter of the name
        TextView initialTextView = findViewById(R.id.initial);
        if (loggedAccount.name.length() > 0) {
            initialTextView.setText(String.valueOf(loggedAccount.name.charAt(0)).toUpperCase());
        }

        // Initialize buttons and status text views
        topupButton = findViewById(R.id.topup);
        topupButton.setOnClickListener(v -> handleTopUp(v));

        renterButton = findViewById(R.id.renter_button);
        renterStatus = findViewById(R.id.renter_status);
        renterButton2 = findViewById(R.id.renter_button2);
        renterStatus2 = findViewById(R.id.renter_status2);

        // Toggle visibility based on company association
        if (LoginActivity.loggedAccount.company != null) {
            renterButton2.setVisibility(View.VISIBLE);
            renterStatus2.setVisibility(View.VISIBLE);
            renterButton.setVisibility(View.GONE);
            renterStatus.setVisibility(View.GONE);
            companyView.setVisibility(View.VISIBLE);
        } else {
            renterButton2.setVisibility(View.GONE);
            renterStatus2.setVisibility(View.GONE);
            renterButton.setVisibility(View.VISIBLE);
            renterStatus.setVisibility(View.VISIBLE);
            companyView.setVisibility(View.GONE);
        }

        // Set click listeners for renter buttons
        renterButton.setOnClickListener(v -> {
            moveActivity(this, RegisterRenterActivity.class);
            viewToast(this, "Register your company");
        });

        renterButton2.setOnClickListener(v -> {
            moveActivity(this, ManageBusActivity.class);
            viewToast(this, "Manage your buses");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Toggle visibility based on company association
        if (LoginActivity.loggedAccount.company != null) {
            renterButton2.setVisibility(View.VISIBLE);
            renterStatus2.setVisibility(View.VISIBLE);
            renterButton.setVisibility(View.GONE);
            renterStatus.setVisibility(View.GONE);
            companyView.setVisibility(View.VISIBLE);
        } else {
            renterButton2.setVisibility(View.GONE);
            renterStatus2.setVisibility(View.GONE);
            renterButton.setVisibility(View.VISIBLE);
            renterStatus.setVisibility(View.VISIBLE);
            companyView.setVisibility(View.GONE);
        }

        // Fetch updated account details from the server
        mApiService.getAccountbyId(loggedAccount.id).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Account loggedAccount = response.body();
                    balanceTextView.setText("IDR " + String.valueOf(loggedAccount.balance));
                } else {
                    Toast.makeText(AboutMeActivity.this, "Failed to get account details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Toast.makeText(mContext, "Problem with the server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to navigate to another activity
    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }

    // Method to display a short toast message
    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    // Method to handle the top-up action
    public void handleTopUp(View view) {
        // Get the amount from the EditText
        EditText amountEditText = findViewById(R.id.topup_amount);
        String amountStr = amountEditText.getText().toString();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Amount cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        if (amount <= 0) {
            Toast.makeText(this, "Top-up amount must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        mApiService.topUp(loggedAccount.id, amount).enqueue(new Callback<BaseResponse<Double>>() {
            @Override
            public void onResponse(Call<BaseResponse<Double>> call, Response<BaseResponse<Double>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AboutMeActivity.this, "Application error " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                BaseResponse<Double> res = response.body();

                if (res.success) {
                    // Update the balance in the TextView
                    balanceTextView.setText(String.valueOf(res.payload));

                    Toast.makeText(AboutMeActivity.this, "Top-up successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AboutMeActivity.this, res.message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Double>> call, Throwable t) {
                Toast.makeText(AboutMeActivity.this, "Problem with the server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
