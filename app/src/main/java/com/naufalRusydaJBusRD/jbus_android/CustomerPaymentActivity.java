package com.naufalRusydaJBusRD.jbus_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Activity for managing customer payments.
 *
 * @author Naufal Rusyda Santosa
 * @version 1.0
 */
public class CustomerPaymentActivity extends AppCompatActivity {
    private ListView paymentListView;
    private CPaymentListAdapter paymentListAdapter;
    private BaseApiService mApiService;
    private Context mContext;
    private Payment payment;
    private TextView balanceTextView;

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

        // Initialize API service and UI components
        mApiService = UtilsApi.getApiService();
        Account loggedAccount = LoginActivity.loggedAccount;
        balanceTextView = findViewById(R.id.cp_balance);
        updateBalance();

        // Initialize the ListView and adapter
        paymentListView = findViewById(R.id.cp_list_view);
        paymentListAdapter = new CPaymentListAdapter(this, new ArrayList<>());
        updatePaymentList();
    }

    /**
     * Fetches the list of payments from the server and updates the adapter.
     */
    private void updatePaymentList() {
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
                    Toast.makeText(CustomerPaymentActivity.this, "Problem with the server" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        paymentListView.setAdapter(paymentListAdapter);
    }

    /**
     * Fetches the account balance from the server and updates the UI.
     */
    private void updateBalance() {
        mApiService.getAccountbyId(LoginActivity.loggedAccount.id).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Account loggedAccount = response.body();
                    balanceTextView.setText("IDR " + String.valueOf(loggedAccount.balance));
                } else {
                    Toast.makeText(CustomerPaymentActivity.this, "Failed to get bus details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Toast.makeText(mContext, "Problem with the server", Toast.LENGTH_SHORT).show();
            }
        });
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
                    Toast.makeText(CustomerPaymentActivity.this, "Problem with the server" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Custom ArrayAdapter for displaying customer payments in a ListView.
     */
    public class CPaymentListAdapter extends ArrayAdapter<Payment> {
        private Context mContext;

        public CPaymentListAdapter(Context context, List<Payment> payments) {
            super(context, 0, payments);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // Inflate the layout if convertView is null
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.customer_payment_list, parent, false);
            }

            // Get the current payment item
            payment = getItem(position);

            // Initialize TextViews and other UI components
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

            // Format and set the schedule information
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss");
            scheduleTextView.setText(dateFormat.format(payment.departureDate.getTime()) + "\t\t" + payment.busSeat + " Status: " + payment.status);

            // Set click listener for navigating to BusDetailActivity
            View customerBusLayout = convertView.findViewById(R.id.customer_bus_detail);
            customerBusLayout.setOnClickListener(v -> {
                Payment selectedPayment = getItem(position);
                if (selectedPayment != null) {
                    // Navigate to BusDetailActivity with the selected bus ID
                    Intent intent = new Intent(getContext(), BusDetailActivity.class);
                    intent.putExtra("busId", selectedPayment.busId); // Assuming Bus class has an 'id' field
                    getContext().startActivity(intent);
                }
            });

            // Set up remove button
            ImageView removeButton = convertView.findViewById(R.id.remove_payment);
            if (payment.status == Invoice.PaymentStatus.FAILED) {
                removeButton.setVisibility(View.VISIBLE);
            }
            removeButton.setOnClickListener(v -> {
                Payment selectedPayment = getItem(position);
                if (selectedPayment != null) {
                    // Remove the payment
                    mApiService.removePayment(selectedPayment.id).enqueue(new Callback<BaseResponse<Payment>>() {
                        @Override
                        public void onResponse(Call<BaseResponse<Payment>> call, Response<BaseResponse<Payment>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                updatePaymentList();
                                Toast.makeText(CustomerPaymentActivity.this, "Remove successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CustomerPaymentActivity.this, "Failed to get bus details", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse<Payment>> call, Throwable t) {
                            Toast.makeText(mContext, "Problem with the server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            // Set up invoice button
            Button invoiceButton = convertView.findViewById(R.id.cp_invoice);
            invoiceButton.setOnClickListener(v -> {
                Payment selectedPayment = getItem(position);
                if (selectedPayment != null) {
                    // Navigate to InvoiceActivity with the selected payment and bus ID
                    Intent intent = new Intent(getContext(), InvoiceActivity.class);
                    intent.putExtra("busId", selectedPayment.busId);
                    intent.putExtra("paymentId", selectedPayment.id);
                    getContext().startActivity(intent);
                }
            });

            // Set up accept button
            Button acceptButton = convertView.findViewById(R.id.cp_accept);
            acceptButton.setTag(position);

            // Declare final variables for position, convertView, and current payment
            final int finalPosition = position;
            final View finalConvertView = convertView;
            final Payment curPayment = getItem(finalPosition);

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
                                updateBalance();

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

            // Set up cancel button
            Button cancelButton = convertView.findViewById(R.id.cp_cancel);
            cancelButton.setTag(position);

            // Set visibility based on payment status
            if (payment.status == Invoice.PaymentStatus.FAILED) {
                cancelButton.setVisibility(View.GONE);
                acceptButton.setVisibility(View.GONE);
                invoiceButton.setVisibility(View.GONE);
            }
            if (payment.status == Invoice.PaymentStatus.SUCCESS) {
                cancelButton.setVisibility(View.VISIBLE);
                acceptButton.setVisibility(View.GONE);
                invoiceButton.setVisibility(View.VISIBLE);
            }
            if (payment.status == Invoice.PaymentStatus.WAITING) {
                cancelButton.setVisibility(View.GONE);
                acceptButton.setVisibility(View.VISIBLE);
                invoiceButton.setVisibility(View.GONE);
            }

            // Set up cancel button click listener
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
                                removeButton.setVisibility(View.VISIBLE);
                                updateBalance();

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
}
