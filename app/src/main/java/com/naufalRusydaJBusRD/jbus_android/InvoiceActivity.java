package com.naufalRusydaJBusRD.jbus_android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.naufalRusydaJBusRD.jbus_android.model.Bus;
import com.naufalRusydaJBusRD.jbus_android.model.Payment;
import com.naufalRusydaJBusRD.jbus_android.model.Schedule;
import com.naufalRusydaJBusRD.jbus_android.model.Station;
import com.naufalRusydaJBusRD.jbus_android.request.BaseApiService;
import com.naufalRusydaJBusRD.jbus_android.request.UtilsApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceActivity extends AppCompatActivity {

    private BaseApiService apiService;
    private Bus detailedBus;
    private Payment detailedPayment;

    private TextView busNameTextView;
    private TextView priceTextView;
    private TextView facilitiesTextView;
    private TextView busTypeTextView;
    private TextView departureTextView;
    private TextView arrivalTextView;
    private TextView seatTextView;
    private TextView paymentStatusTextView;
    private TextView paymentDateTextView;

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        // Initialize the ActionBar
        ActionBar actionBar = getSupportActionBar();
        // Set the title of the ActionBar
        if (actionBar != null) {
            actionBar.setTitle("Invoice");
        }

        // Extract bus ID from the intent
        int busId = getIntent().getIntExtra("busId", -1);
        if (busId != -1) {
            // Make the API call to get detailed information using busId
            getBusDetails(busId);
        } else {
            // Handle the case when busId is not found in the intent
            Toast.makeText(this, "Bus ID not found", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        }

        // Extract bus ID from the intent
        int paymentId = getIntent().getIntExtra("paymentId", -1);
        if (paymentId != -1) {
            // Make the API call to get detailed information using busId
            getPaymentDetails(paymentId);
        } else {
            // Handle the case when busId is not found in the intent
            Toast.makeText(this, "Bus ID not found", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        }
        mContext = this;


        busNameTextView = findViewById(R.id.invoice_bus_name);
        seatTextView = findViewById(R.id.invoice_seat);
        facilitiesTextView = findViewById(R.id.invoice_facilities);
        busTypeTextView = findViewById(R.id.invoice_bus_type);
        departureTextView = findViewById(R.id.invoice_departure);
        arrivalTextView = findViewById(R.id.invoice_arrival);
        priceTextView = findViewById(R.id.invoice_price);
        paymentStatusTextView = findViewById(R.id.invoice_payment_status);

        // Update UI components when detailedBus is available
        updateUIComponents();

    }

    private void getBusDetails(int busId) {
        apiService = UtilsApi.getApiService();
        apiService.getBusbyId(busId).enqueue(new Callback<Bus>() {
            @Override
            public void onResponse(Call<Bus> call, Response<Bus> response) {
                if (response.isSuccessful() && response.body() != null) {
                    detailedBus = response.body();
                    // Update UI with detailed bus information
                    updateUIComponents();
                } else {
                    Toast.makeText(InvoiceActivity.this, "Failed to get bus details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Bus> call, Throwable t) {
                Toast.makeText(InvoiceActivity.this, "Problem with the server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPaymentDetails(int paymentId) {
        apiService = UtilsApi.getApiService();
        apiService.getPaymentById(paymentId).enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    detailedPayment = response.body();
                    // Update UI with detailed bus information
                    updateUIComponents();
                } else {
                    Toast.makeText(InvoiceActivity.this, "Failed to get bus details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                Toast.makeText(InvoiceActivity.this, "Problem with the server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIComponents() {
        // Check if detailedBus is not null before updating UI
        if (detailedBus != null && detailedPayment != null) {


            // Update UI components with detailed bus information
            busNameTextView.setText(detailedBus.name);

            // Set seatTextView with seats from detailedPayment
            String seatText = TextUtils.join(", ", detailedPayment.busSeat);
            seatTextView.setText(seatText);

            // Format the Price object into a readable string
            String priceText =  "" + detailedBus.price.price;
            priceTextView.setText(priceText);

            // Convert the list of facilities into a readable string
            String facilitiesText = TextUtils.join(", ", detailedBus.facilities);
            facilitiesTextView.setText(facilitiesText);

            // Get the readable representation of the BusType enum
            String busTypeText = detailedBus.busType.toString();
            busTypeTextView.setText(busTypeText);

            // Format the Station objects into readable strings
            String departureText = formatStationText(detailedBus.departure);
            departureTextView.setText(departureText);

            String arrivalText = formatStationText(detailedBus.arrival);
            arrivalTextView.setText(arrivalText);

            paymentStatusTextView.setText(String.valueOf(detailedPayment.status));

        }
    }

    private String formatStationText(Station station) {
        if (station != null) {
            // Format the Station information into a readable string
            return station.stationName + " - " + station.city.toString();
        } else {
            return "N/A"; // or handle it differently based on your requirements
        }
    }
}