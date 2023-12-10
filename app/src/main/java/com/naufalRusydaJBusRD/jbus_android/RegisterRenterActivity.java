package com.naufalRusydaJBusRD.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.naufalRusydaJBusRD.jbus_android.model.Account;
import com.naufalRusydaJBusRD.jbus_android.model.BaseResponse;
import com.naufalRusydaJBusRD.jbus_android.model.Renter;
import com.naufalRusydaJBusRD.jbus_android.request.BaseApiService;
import com.naufalRusydaJBusRD.jbus_android.request.UtilsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for registering a renter.
 *
 * @author Naufal Rusyda Santosa
 * @version 1.0
 */
public class RegisterRenterActivity extends AppCompatActivity {

    private BaseApiService mApiService;
    private Context mContext;
    private EditText companyName, address, phoneNumber;
    private Button registerButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_renter);
        getSupportActionBar().hide();

        // Initialize variables
        mContext = this;
        mApiService = UtilsApi.getApiService();

        companyName = findViewById(R.id.aboutme_name);
        address = findViewById(R.id.aboutme_email);
        phoneNumber = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.register_now);

        // Set click listener for the register button
        registerButton.setOnClickListener(v -> handleRegister());
    }

    /**
     * Handles the registration process for a renter.
     */
    protected void handleRegister() {
        // Handling empty fields
        String companyS = companyName.getText().toString();
        String addressS = address.getText().toString();
        String phoneS = phoneNumber.getText().toString();

        if (companyS.isEmpty() || addressS.isEmpty() || phoneS.isEmpty()) {
            Toast.makeText(mContext, "Field cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the logged-in account
        Account loggedAccount = LoginActivity.loggedAccount;

        // Make a renter registration API call
        int id = loggedAccount.id;
        mApiService.registerRenter(id, companyS, addressS, phoneS).enqueue(new Callback<BaseResponse<Renter>>() {
            @Override
            public void onResponse(Call<BaseResponse<Renter>> call, Response<BaseResponse<Renter>> response) {
                // Handle potential 4xx & 5xx errors
                if (!response.isSuccessful()) {
                    Toast.makeText(mContext, "Application error " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Process the response
                BaseResponse<Renter> res = response.body();

                // If registration is successful, update the logged account and finish this activity (back to login activity)
                if (res.success) {
                    LoginActivity.loggedAccount.company = res.payload;
                    finish();
                } else {
                    Toast.makeText(mContext, res.message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Renter>> call, Throwable t) {
                Toast.makeText(mContext, "Problem with the server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
