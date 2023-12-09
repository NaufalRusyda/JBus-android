package com.naufalRusydaJBusRD.jbus_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.naufalRusydaJBusRD.jbus_android.model.Account;
import com.naufalRusydaJBusRD.jbus_android.model.BaseResponse;
import com.naufalRusydaJBusRD.jbus_android.model.Bus;
import com.naufalRusydaJBusRD.jbus_android.model.Invoice;
import com.naufalRusydaJBusRD.jbus_android.model.Payment;
import com.naufalRusydaJBusRD.jbus_android.request.BaseApiService;
import com.naufalRusydaJBusRD.jbus_android.request.UtilsApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerPaymentActivity extends AppCompatActivity {
    private ListView paymentListView;
    private CustomerPaymentActivity.CPaymentListAdapter paymentListAdapter;
    private BaseApiService mApiService;
    private Context mContext;
    private Payment payment;
    private Double busPrice = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_payment);
        // Initialize the ActionBar
        ActionBar actionBar = getSupportActionBar();
        // Set the title of the ActionBar
        if (actionBar != null) {
            actionBar.setTitle("Manage Payment");
        }

        mApiService = UtilsApi.getApiService();

        Account loggedAccount = LoginActivity.loggedAccount;

        // Initialize the ListView and adapter
        paymentListView = findViewById(R.id.cp_list_view);
        paymentListAdapter = new CustomerPaymentActivity.CPaymentListAdapter(this, new ArrayList<>());
        paymentListView.setAdapter(paymentListAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mApiService = UtilsApi.getApiService();

        // Check if loggedAccount is not null before making API calls
        if (LoginActivity.loggedAccount != null) {
            // Fetch the list of payments for the logged-in buyer
            mApiService.getMyPayments(LoginActivity.loggedAccount.id).enqueue(new Callback<List<Payment>>() {
                @Override
                public void onResponse(Call<List<Payment>> call, Response<List<Payment>> response) {
                    if (response.isSuccessful()) {
                        // Update the adapter with the fetched data
                        paymentListAdapter.clear();
                        paymentListAdapter.addAll(response.body());
                    } else {
                        Toast.makeText(CustomerPaymentActivity.this, "Failed to fetch payments", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Payment>> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(CustomerPaymentActivity.this, "1Problem with the server"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public class CPaymentListAdapter extends ArrayAdapter<Payment> {

        private Context mContext;

        public CPaymentListAdapter(Context context, List<Payment> payments) {
            super(context, 0, payments);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.customer_payment_list, parent, false);
            }

            payment = getItem(position);

            TextView busNameTextView = convertView.findViewById(R.id.cp_bus_name);
            TextView scheduleTextView = convertView.findViewById(R.id.cp_schedule);

            // Fetch the Bus object using the busId from the Payment object
            mApiService.getBusbyId(payment.busId).enqueue(new Callback<Bus>() {
                @Override
                public void onResponse(Call<Bus> call, Response<Bus> response) {
                    if (response.isSuccessful()) {
                        Bus bus = response.body();
                        if (payment != null) {
                            // Assuming busName is a property of the Bus class
                            busNameTextView.setText(bus.name);
                        } else {
                            // Handle the case where payment is null
                        }
                    } else {
                        // Handle error
                        Toast.makeText(mContext, "Failed to fetch schedule information", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onFailure(Call<Bus> call, Throwable t) {
                    // Handle failure
                    Toast.makeText(mContext, "Problem with the server", Toast.LENGTH_SHORT).show();
                }
            });

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss");
            scheduleTextView.setText(dateFormat.format(payment.departureDate.getTime()) + "\t\t" + payment.busSeat + "Status: " + payment.status);

            Button invoiceButton = convertView.findViewById(R.id.cp_invoice);
            invoiceButton.setOnClickListener(v -> {
                Payment selectedPayment = getItem(position);
                if (selectedPayment != null) {
                    // Navigate to BusDetailActivity with the selected bus ID
                    Intent intent = new Intent(getContext(), InvoiceActivity.class);
                    intent.putExtra("busId", selectedPayment.busId); // Assuming Bus class has an 'id' field
                    intent.putExtra("paymentId", selectedPayment.id); // Assuming Bus class has an 'id' field
                    getContext().startActivity(intent);
                }
            });

            Button acceptButton = convertView.findViewById(R.id.cp_accept);
            acceptButton.setTag(position);

            // Declare final variables for position and convertView
            final int finalPosition = position;
            final View finalConvertView = convertView;
            final Payment curPayment = getItem(finalPosition);

            getBusPrice();

            acceptButton.setOnClickListener(new View.OnClickListener() {
                Account loggedAccount = LoginActivity.loggedAccount;

                @Override
                public void onClick(View v) {
                    mApiService.accept(curPayment.id, loggedAccount.id).enqueue(new Callback<BaseResponse<Payment>>() {
                        Invoice.PaymentStatus status = curPayment.status;

                        @Override
                        public void onResponse(Call<BaseResponse<Payment>> call, Response<BaseResponse<Payment>> response) {
                            if (response.isSuccessful()) {
                                BaseResponse<Payment> baseResponse = response.body();
                                status = baseResponse.payload.status;
                                TextView scheduleTextView = finalConvertView.findViewById(R.id.cp_schedule);
                                scheduleTextView.setText(dateFormat.format(curPayment.departureDate.getTime()) + "\t\t" + curPayment.busSeat + "Status: " + status);
                                Button acceptButton = finalConvertView.findViewById(R.id.cp_accept);
                                Button cancelButton = finalConvertView.findViewById(R.id.cp_cancel);
                                acceptButton.setVisibility(View.GONE);
                                cancelButton.setVisibility(View.VISIBLE);
                                invoiceButton.setVisibility(View.VISIBLE);

                                Toast.makeText(CustomerPaymentActivity.this, "Payment accepted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CustomerPaymentActivity.this, "Failed to accept payment", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse<Payment>> call, Throwable t) {
                            Toast.makeText(CustomerPaymentActivity.this, "Problem with the server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });


            Button cancelButton = convertView.findViewById(R.id.cp_cancel);
            cancelButton.setTag(position);
            if(payment.status == Invoice.PaymentStatus.FAILED) {
                cancelButton.setVisibility(View.GONE);
                acceptButton.setVisibility(View.GONE);
                invoiceButton.setVisibility(View.GONE);
            }
            if(payment.status == Invoice.PaymentStatus.SUCCESS) {
                cancelButton.setVisibility(View.VISIBLE);
                acceptButton.setVisibility(View.GONE);
                invoiceButton.setVisibility(View.VISIBLE);
            }
            if(payment.status == Invoice.PaymentStatus.WAITING) {
                cancelButton.setVisibility(View.GONE);
                acceptButton.setVisibility(View.VISIBLE);
                invoiceButton.setVisibility(View.GONE);
            }

            cancelButton.setOnClickListener(new View.OnClickListener() {

                Payment curPayment = getItem(position);
                @Override
                public void onClick(View v) {
                    mApiService.cancel(curPayment.id).enqueue(new Callback<BaseResponse<Payment>>() {
                        Invoice.PaymentStatus status = curPayment.status;

                        @Override
                        public void onResponse(Call<BaseResponse<Payment>> call, Response<BaseResponse<Payment>> response) {
                            if (response.isSuccessful() && payment != null) {
                                BaseResponse<Payment> baseResponse = response.body();
                                status = baseResponse.payload.status;
                                TextView scheduleTextView = finalConvertView.findViewById(R.id.cp_schedule);
                                scheduleTextView.setText(dateFormat.format(payment.departureDate.getTime()) + "\t\t" + payment.busSeat + "Status: " + status);
                                Button cancelButton = finalConvertView.findViewById(R.id.cp_cancel);
                                cancelButton.setVisibility(View.GONE);
                                Button acceptButton = finalConvertView.findViewById(R.id.cp_accept);
                                acceptButton.setVisibility(View.GONE);
                                invoiceButton.setVisibility(View.GONE);

                                Toast.makeText(CustomerPaymentActivity.this, "Payment cancelled successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CustomerPaymentActivity.this, "Failed to cancel payment", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse<Payment>> call, Throwable t) {
                            Toast.makeText(CustomerPaymentActivity.this, "Problem with the server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            return convertView;
        }

    }

    private void getBusPrice() {
        mApiService.getBusbyId(payment.busId).enqueue(new Callback<Bus>() {
            @Override
            public void onResponse(Call<Bus> call, Response<Bus> response) {
                if (response.isSuccessful()) {
                    Bus bus = response.body();
                    // Fetch the bus price
                    busPrice = bus.price.price;
                    // Update your UI or perform any actions with the busPrice

                } else {
                    // Handle error
                    Toast.makeText(mContext, "Failed to fetch schedule information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Bus> call, Throwable t) {
                // Handle failure
                Toast.makeText(mContext, "Problem with the server", Toast.LENGTH_SHORT).show();
            }
        });
    }

}