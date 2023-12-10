package com.naufalRusydaJBusRD.jbus_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.naufalRusydaJBusRD.jbus_android.model.Account;
import com.naufalRusydaJBusRD.jbus_android.model.BaseResponse;
import com.naufalRusydaJBusRD.jbus_android.request.BaseApiService;
import com.naufalRusydaJBusRD.jbus_android.request.UtilsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LoginActivity handles user authentication and navigation to the main activity.
 *
 * @author Naufal Rusyda Santosa
 * @version 1.0
 */
public class LoginActivity extends AppCompatActivity {

    // Define the variables
    private Button registerNow = null;
    private Button loginButton = null;
    public static Account loggedAccount; // Holds information about the logged-in account

    private BaseApiService mApiService;
    private Context mContext;
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide(); // Hide the action bar

        // Load the components to the variables
        registerNow = findViewById(R.id.register_now);
        loginButton = findViewById(R.id.login_button);

        // Set onClickListener for the "Register Now" button
        registerNow.setOnClickListener(v -> {
            moveActivity(this, RegisterActivity.class);
            viewToast(this, "Register your account");
        });

        // Set onClickListener for the "Login" button
        loginButton.setOnClickListener(v -> {
            moveActivity(this, MainActivity.class);
            viewToast(this, "Welcome");
        });

        mContext = this;
        mApiService = UtilsApi.getApiService();

        // Initialize UI components
        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        // Set onClickListener for the "Login" button to handle login logic
        loginButton.setOnClickListener(v -> handleLogin());
    }

    /**
     * Move to another activity.
     *
     * @param ctx The context.
     * @param cls The target activity class.
     */
    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }

    /**
     * Display a short toast message.
     *
     * @param ctx     The context.
     * @param message The message to display.
     */
    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Handle the login process by making an API call.
     */
    protected void handleLogin() {
        String emailStr = emailEditText.getText().toString();
        String passwordStr = passwordEditText.getText().toString();

        // Validate that email and password are not empty
        if (emailStr.isEmpty() || passwordStr.isEmpty()) {
            Toast.makeText(mContext, "Field cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Make an API call to perform login
        mApiService.login(emailStr, passwordStr).enqueue(new Callback<BaseResponse<Account>>() {
            @Override
            public void onResponse(Call<BaseResponse<Account>> call, Response<BaseResponse<Account>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(mContext, "Application error " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                BaseResponse<Account> res = response.body();

                if (res.success) {
                    // Login successful, store the logged account and navigate to the main activity
                    loggedAccount = res.payload;
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    finish(); // Finish the login activity
                    Toast.makeText(mContext, "Welcome " + loggedAccount.name, Toast.LENGTH_SHORT).show();
                } else {
                    // Login failed, display the error message
                    Toast.makeText(mContext, res.message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Account>> call, Throwable t) {
                // Handle failure in making the API call
                Toast.makeText(mContext, "Problem with the server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
