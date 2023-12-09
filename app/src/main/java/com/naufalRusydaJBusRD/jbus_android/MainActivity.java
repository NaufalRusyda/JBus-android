package com.naufalRusydaJBusRD.jbus_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterCompat;

import com.naufalRusydaJBusRD.jbus_android.model.Bus;
import com.naufalRusydaJBusRD.jbus_android.model.Station;
import com.naufalRusydaJBusRD.jbus_android.request.BaseApiService;
import com.naufalRusydaJBusRD.jbus_android.request.UtilsApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button[] btns;
    private int currentPage = 0;
    private final int pageSize = 8; // kalian dapat bereksperimen dengan field ini
    private int listSize;
    private int noOfPages;
    private List<Bus> listBus = new ArrayList<>();
    private Button prevButton = null;
    private Button nextButton = null;
    private ListView busListView = null;
    private HorizontalScrollView pageScroll = null;
    private BaseApiService apiService;
    public Bus detailedBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hubungkan komponen dengan ID nya
        prevButton = findViewById(R.id.prev_page);
        nextButton = findViewById(R.id.next_page);
        pageScroll = findViewById(R.id.page_number_scroll);
        busListView = findViewById(R.id.bus_list);

        listBus();

    }

    @Override
    protected void onResume() {
        super.onResume();

        apiService = UtilsApi.getApiService();

        // Make the API request to get all buses
        Call<List<Bus>> call = apiService.getAllBus();
        call.enqueue(new Callback<List<Bus>>() {
            @Override
            public void onResponse(Call<List<Bus>> call, Response<List<Bus>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Handle the list of buses obtained from the API
                    listBus = response.body();
                    goToPage(currentPage);
                    buttonListener();
                } else {
                    Toast.makeText(MainActivity.this, "Application error " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bus>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Problem with the server", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void buttonListener() {
        // listener untuk button prev dan button
        prevButton.setOnClickListener(v -> {
            currentPage = currentPage != 0 ? currentPage - 1 : 0;
            goToPage(currentPage);
        });
        nextButton.setOnClickListener(v -> {
            currentPage = currentPage != noOfPages - 1 ? currentPage + 1 : currentPage;
            goToPage(currentPage);
        });
    }

    private void listBus() {

        apiService = UtilsApi.getApiService();

        // Make the API request to get all buses
        Call<List<Bus>> call = apiService.getAllBus();
        call.enqueue(new Callback<List<Bus>>() {
            @Override
            public void onResponse(Call<List<Bus>> call, Response<List<Bus>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Handle the list of buses obtained from the API
                    listBus = response.body();
                    listSize = listBus.size();
                    paginationFooter();
                    goToPage(currentPage);
                    buttonListener();
                } else {
                    Toast.makeText(MainActivity.this, "Application error " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bus>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Problem with the server", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.user_button) {
            // User clicked the "User Button"
            moveActivity(this, AboutMeActivity.class);
            return true;
        }
        if (id == R.id.payment_button) {
            // User clicked the "User Button"
            moveActivity(this, CustomerPaymentActivity.class);
            return true;
        }
        // Add other conditions for additional menu items if needed
        return super.onOptionsItemSelected(item);
    }

    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }

    private void paginationFooter() {
        int val = listSize % pageSize;
        val = val == 0 ? 0 : 1;
        noOfPages = listSize / pageSize + val;
        LinearLayout ll = findViewById(R.id.btn_layout);
        btns = new Button[noOfPages];
        if (noOfPages <= 6) {
            ((FrameLayout.LayoutParams) ll.getLayoutParams()).gravity = Gravity.CENTER;
        }
        for (int i = 0; i < noOfPages; i++) {
            btns[i] = new Button(this);
            btns[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
            btns[i].setText("" + (i + 1));
            // ganti dengan warna yang kalian mau
            btns[i].setTextColor(getResources().getColor(R.color.black));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100, 100);
            ll.addView(btns[i], lp);
            final int j = i;
            btns[j].setOnClickListener(v -> {
                currentPage = j;
                goToPage(j);
            });
        }
    }

    private void goToPage(int index) {
        for (int i = 0; i < noOfPages; i++) {
            if (i == index) {
                btns[index].setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));
                btns[i].setTextColor(getResources().getColor(android.R.color.white));
                scrollToItem(btns[index]);
                viewPaginatedList(listBus, currentPage);
            } else {
                btns[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
                btns[i].setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }

    private void scrollToItem(Button item) {
        int scrollX = item.getLeft() - (pageScroll.getWidth() - item.getWidth()) / 2;
        pageScroll.smoothScrollTo(scrollX, 0);
    }

    private void viewPaginatedList(List<Bus> listBus, int page) {
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, listBus.size());
        List<Bus> paginatedList = listBus.subList(startIndex, endIndex);

        BusArrayAdapter busArrayAdapter = new BusArrayAdapter(this, paginatedList);
        busListView.setAdapter(busArrayAdapter);
    }

    public class BusArrayAdapter extends ArrayAdapter<Bus> {
        private TextView departureTextView;
        private TextView arrivalTextView;
        private Bus detailedBus;


        public BusArrayAdapter(Context context, List<Bus> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            detailedBus = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.bus_view, parent, false);
            }

            TextView busNameTextView = convertView.findViewById(R.id.bus_name);
            Bus bus = getItem(position);
            busNameTextView.setText(bus.name);

            View mainLayout = convertView.findViewById(R.id.main_layout);

            TextView departureTextView = convertView.findViewById(R.id.main_departure);
            TextView arrivalTextView = convertView.findViewById(R.id.main_arrival);

            mainLayout.setOnClickListener(v -> {
                Bus selectedBus = getItem(position);
                if (selectedBus != null) {
                    // Navigate to BusDetailActivity with the selected bus ID
                    Intent intent = new Intent(getContext(), BusDetailActivity.class);
                    intent.putExtra("busId", selectedBus.id); // Assuming Bus class has an 'id' field
                    getContext().startActivity(intent);
                }
            });

            apiService = UtilsApi.getApiService();

            Call<Bus> call = apiService.getBusbyId(detailedBus.id);
            call.enqueue(new Callback<Bus>() {
                @Override
                public void onResponse(Call<Bus> call, Response<Bus> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        detailedBus = response.body();
                        // Update UI with detailed bus information
                        if (detailedBus != null) {
                            // Format the Station objects into readable strings
                            String departureText = formatStationText(detailedBus.departure);
                            departureTextView.setText(departureText);

                            String arrivalText = formatStationText(detailedBus.arrival);
                            arrivalTextView.setText(arrivalText);

                        }                    } else {
                        Toast.makeText(MainActivity.this, "Failed to get bus details", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Bus> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Problem with the server", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }

        private String formatStationText (Station station){
            if (station != null) {
                // Format the Station information into a readable string
                return station.stationName + " - " + station.city;
            } else {
                // Return a more informative message
                return "Station information unavailable";
            }
        }


    }


}
