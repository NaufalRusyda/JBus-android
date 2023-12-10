package com.naufalRusydaJBusRD.jbus_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.naufalRusydaJBusRD.jbus_android.model.Bus;
import com.naufalRusydaJBusRD.jbus_android.model.BusType;
import com.naufalRusydaJBusRD.jbus_android.model.Station;
import com.naufalRusydaJBusRD.jbus_android.request.BaseApiService;
import com.naufalRusydaJBusRD.jbus_android.request.UtilsApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MainActivity class represents the main activity of the JBus Android application.
 *
 * @author Naufal Rusyda Santosa
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {
    private BusType[] busType = BusType.values();
    private List<Station> stationList = new ArrayList<>();
    private BaseApiService mApiService;
    private Context mContext;
    private Button[] btns;
    private int currentPage = 0;
    private final int pageSize = 8;
    private int listSize;
    private int noOfPages;
    private List<Bus> listBus = new ArrayList<>();
    private Button prevButton = null;
    private Button nextButton = null;
    private ListView busListView = null;
    private HorizontalScrollView pageScroll = null;
    private BaseApiService apiService;
    private Button filterBus;
    private int selectedDeptStationID;
    private int selectedArrStationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect components with their IDs
        prevButton = findViewById(R.id.prev_page);
        nextButton = findViewById(R.id.next_page);
        pageScroll = findViewById(R.id.page_number_scroll);
        busListView = findViewById(R.id.bus_list);
        filterBus = findViewById(R.id.main_filter);
        mContext = this;
        filterBus.setOnClickListener(v -> showDialog());

        listBus();
    }

    /**
     * Show a dialog to filter buses based on departure and arrival stations.
     */
    protected void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_bus_setter, null);
        Spinner departureSpinner = dialogView.findViewById(R.id.filter_departure);
        Spinner arrivalSpinner = dialogView.findViewById(R.id.filter_arrival);
        Button saveFilter = dialogView.findViewById(R.id.buttonSaveFilter);
        Button removeFilter = dialogView.findViewById(R.id.buttonRemoveFilter);

        // Spinner item selection listeners
        AdapterView.OnItemSelectedListener deptOISL = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDeptStationID = stationList.get(position).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        AdapterView.OnItemSelectedListener arrOISL = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedArrStationID = stationList.get(position).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        mApiService = UtilsApi.getApiService();
        mContext = this;

        mApiService.getAllStation().enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
                if (response.isSuccessful()) {
                    stationList = response.body();

                    // Create custom adapters
                    ArrayAdapter deptAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, stationList);
                    ArrayAdapter arrAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, stationList);

                    // Set adapters to spinners
                    if (departureSpinner != null && arrivalSpinner != null) {
                        // Use your custom adapters and set them to spinners
                        departureSpinner.setAdapter(deptAdapter);
                        arrivalSpinner.setAdapter(arrAdapter);

                        // Set the listeners
                        departureSpinner.setOnItemSelectedListener(deptOISL);
                        arrivalSpinner.setOnItemSelectedListener(arrOISL);
                    } else {
                        Toast.makeText(mContext, "Spinners not initialized properly", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {
                Toast.makeText(mContext, "Problem with the server", Toast.LENGTH_SHORT).show();
            }
        });

        // Save and remove filter button listeners
        saveFilter.setOnClickListener(t -> {
            updateBus();
            Toast.makeText(mContext, "Filter saved", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        removeFilter.setOnClickListener(t -> {
            onResume();
            Toast.makeText(mContext, "Filter removed", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.show();
    }

    /**
     * Update the list of buses based on the selected departure and arrival stations.
     */
    private void updateBus() {
        apiService = UtilsApi.getApiService();

        // Make the API request to get all buses
        Call<List<Bus>> call = apiService.getBusByDepartureArrival(selectedDeptStationID, selectedArrStationID);
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

    /**
     * Set listeners for the previous and next buttons.
     */
    private void buttonListener() {
        // listener for prev and next buttons
        prevButton.setOnClickListener(v -> {
            currentPage = currentPage != 0 ? currentPage - 1 : 0;
            goToPage(currentPage);
        });
        nextButton.setOnClickListener(v -> {
            currentPage = currentPage != noOfPages - 1 ? currentPage + 1 : currentPage;
            goToPage(currentPage);
        });
    }private void listBus() {
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

        // Get the SearchView and set up search functionality
        MenuItem searchItem = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the query submission if needed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the list based on the user's input
                filterBuses(newText);
                return true;
            }
        });

        return true;
    }

    /**
     * Filters the list of buses based on the user's input query.
     *
     * @param query The user's input query.
     */
    private void filterBuses(String query) {
        List<Bus> filteredList = new ArrayList<>();
        for (Bus bus : listBus) {
            // Check if the bus name, departure city, or arrival city contains the query
            if (bus.name.toLowerCase().contains(query.toLowerCase()) ||
                    bus.departure.stationName.toLowerCase().contains(query.toLowerCase()) ||
                    bus.departure.city.toString().toLowerCase().contains(query.toLowerCase()) ||
                    bus.arrival.stationName.toLowerCase().contains(query.toLowerCase()) ||
                    bus.arrival.city.toString().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(bus);
            }
        }

        // Update the displayed buses with the filtered list
        goToPage(currentPage);
        viewPaginatedList(filteredList, currentPage);
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

    /**
     * Moves to the specified activity.
     *
     * @param ctx The context.
     * @param cls The class of the target activity.
     */
    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }

    /**
     * Sets up the pagination footer with page buttons.
     */
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
            // change with the color you prefer
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

    /**
     * Navigates to the specified page and updates the displayed list.
     *
     * @param index The index of the target page.
     */
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

    /**
     * Scrolls to the specified item within the pagination horizontal scroll view.
     *
     * @param item The target item (button) to scroll to.
     */
    private void scrollToItem(Button item) {
        int scrollX = item.getLeft() - (pageScroll.getWidth() - item.getWidth()) / 2;
        pageScroll.smoothScrollTo(scrollX, 0);
    }

    /**
     * Updates the displayed list of buses based on the given list and current page.
     *
     * @param listBus The list of buses to be displayed.
     * @param page    The current page index.
     */
    private void viewPaginatedList(List<Bus> listBus, int page) {
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, listBus.size());
        List<Bus> paginatedList = listBus.subList(startIndex, endIndex);

        BusArrayAdapter busArrayAdapter = new BusArrayAdapter(this, paginatedList);
        busListView.setAdapter(busArrayAdapter);
    }

    /**
     * Custom ArrayAdapter for displaying Bus objects in the ListView.
     */
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
                        }
                    } else {
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

        /**
         * Formats the Station information into a readable string.
         *
         * @param station The Station object.
         * @return A formatted string representing the station information.
         */
        private String formatStationText(Station station) {
            if (station != null) {
                return station.stationName + " - " + station.city;
            } else {
                return "Station information unavailable";
            }
        }
    }
}