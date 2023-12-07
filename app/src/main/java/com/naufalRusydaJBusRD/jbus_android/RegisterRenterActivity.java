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

        mContext = this;
        mApiService = UtilsApi.getApiService();

        companyName = findViewById(R.id.aboutme_name);
        address = findViewById(R.id.aboutme_email);
        phoneNumber     = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.register_now);

        registerButton.setOnClickListener(v -> handleRegister());
    }

    protected void handleRegister() {
// handling empty field
        String companyS = companyName.getText().toString();
        String addressS = address.getText().toString();
        String phoneS = phoneNumber.getText().toString();
        if (companyS.isEmpty() || addressS.isEmpty() || phoneS.isEmpty()) {
            Toast.makeText(mContext, "Field cannot be empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Account loggedAccount = LoginActivity.loggedAccount;

        int id = loggedAccount.id;

        mApiService.registerRenter(id, companyS, addressS, phoneS).enqueue(new Callback<BaseResponse<Renter>>() {
            @Override
            public void onResponse(Call<BaseResponse<Renter>> call, Response<BaseResponse<Renter>> response) {
// handle the potential 4xx & 5xx error
                if (!response.isSuccessful()) {
                    Toast.makeText(mContext, "Application error " +
                            response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                BaseResponse<Renter> res = response.body();
// if success finish this activity (back to login activity)
                if (res.success) {
                    LoginActivity.loggedAccount.company = res.payload;
                    finish();
                } else {
                    Toast.makeText(mContext, res.message, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<Renter>> call, Throwable t) {
                Toast.makeText(mContext, "Problem with the server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}