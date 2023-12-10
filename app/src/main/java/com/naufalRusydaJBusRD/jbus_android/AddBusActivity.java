package com.naufalRusydaJBusRD.jbus_android;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.naufalRusydaJBusRD.jbus_android.model.Account;
import com.naufalRusydaJBusRD.jbus_android.model.BaseResponse;
import com.naufalRusydaJBusRD.jbus_android.model.Bus;
import com.naufalRusydaJBusRD.jbus_android.request.BaseApiService;
import com.naufalRusydaJBusRD.jbus_android.model.BusType;
import com.naufalRusydaJBusRD.jbus_android.model.Facility;
import com.naufalRusydaJBusRD.jbus_android.model.Station;
import com.naufalRusydaJBusRD.jbus_android.request.UtilsApi;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBusActivity extends AppCompatActivity {
    private EditText busName, capacity, price;
    private BusType selectedBusType;
    private Spinner busTypeSpinner;
    private BusType[] busType = BusType.values();
    private List<Station> stationList = new ArrayList<>();
    private int selectedDeptStationID;
    private int selectedArrStationID;
    private Spinner departureSpinner;
    private Spinner arrivalSpinner;
    private CheckBox acCheckBox, wifiCheckBox, toiletCheckBox, lcdCheckBox,
            coolboxCheckBox, lunchCheckBox, baggageCheckBox, electricCheckBox;

    private List<Facility> selectedFacilities = new ArrayList<>();
    private BaseApiService mApiService;
    private Context mContext;
    private Button addButton = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);
        getSupportActionBar().hide();

        initStation();
        initBusType();

        // Initialize UI components

        busName = findViewById(R.id.bus);
        capacity = findViewById(R.id.capacity);
        price  = findViewById(R.id.price);

        // Initialize Spinners for Departure and Arrival
        busTypeSpinner = findViewById(R.id.bus_type_dropdown);
        departureSpinner = findViewById(R.id.departure_dropdown);
        arrivalSpinner = findViewById(R.id.arrival_dropdown);

        // Initialize Checkboxes
        acCheckBox = findViewById(R.id.ac_checkbox);
        wifiCheckBox = findViewById(R.id.wifi_checkbox);
        toiletCheckBox = findViewById(R.id.toilet_checkbox);
        lcdCheckBox = findViewById(R.id.lcd_checkbox);
        coolboxCheckBox = findViewById(R.id.coolbox_checkbox);
        lunchCheckBox = findViewById(R.id.lunch_checkbox);
        baggageCheckBox = findViewById(R.id.baggage_checkbox);
        electricCheckBox = findViewById(R.id.electric_checkbox);

        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> handleAdd());

    }

    private void checkFacilities() {
        // Clear the list before updating
        selectedFacilities.clear();

        // Add selected facilities to the list
        if (acCheckBox.isChecked()) {
            selectedFacilities.add(Facility.AC);
        }
        if (wifiCheckBox.isChecked()) {
            selectedFacilities.add(Facility.WIFI);
        }
        if (toiletCheckBox.isChecked()) {
            selectedFacilities.add(Facility.TOILET);
        }
        if (lcdCheckBox.isChecked()) {
            selectedFacilities.add(Facility.LCD_TV);
        }
        if (coolboxCheckBox.isChecked()) {
            selectedFacilities.add(Facility.COOL_BOX);
        }
        if (lunchCheckBox.isChecked()) {
            selectedFacilities.add(Facility.LUNCH);
        }
        if (baggageCheckBox.isChecked()) {
            selectedFacilities.add(Facility.LARGE_BAGGAGE);
        }
        if (electricCheckBox.isChecked()) {
            selectedFacilities.add(Facility.ELECTRIC_SOCKET);
        }
    }

    private List<String> getStationNames() {
        List<String> stationNames = new ArrayList<>();
        for (Station station : stationList) {
            stationNames.add(station.stationName);
        }
        return stationNames;
    }

    private void initStation() {
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

                    // Use getStationNames() to get the list of station names
                    List<String> stationNames = getStationNames();

                    // Create custom adapters
                    ArrayAdapter deptAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, stationList);
                    ArrayAdapter arrAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, stationList);

                    // Set adapters to spinners
                    departureSpinner.setAdapter(deptAdapter);
                    arrivalSpinner.setAdapter(arrAdapter);

                    departureSpinner.setOnItemSelectedListener(deptOISL);
                    arrivalSpinner.setOnItemSelectedListener(arrOISL);

                }
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {
                Toast.makeText(mContext, "Problem with the server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initBusType() {
        AdapterView.OnItemSelectedListener busTypeOISL = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
// mengisi field selectedBusType sesuai dengan item yang dipilih
                selectedBusType = busType[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        // Initialize Spinner for BusType
        busTypeSpinner = findViewById(R.id.bus_type_dropdown);
        ArrayAdapter<BusType> busTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, busType);
        busTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        busTypeSpinner.setAdapter(busTypeAdapter);
        busTypeSpinner.setOnItemSelectedListener(busTypeOISL);
    }

    protected void handleAdd() {
        checkFacilities();
// handling empty field
        String busNameS = busName.getText().toString();
        String capacityS = capacity.getText().toString();
        String priceS = price.getText().toString();
        if (busNameS.isEmpty() || capacityS.isEmpty() || priceS.isEmpty()) {
            Toast.makeText(mContext, "Field cannot be empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        int capacity = Integer.parseInt(capacityS);
        int price = Integer.parseInt(priceS);

        Account loggedAccount = LoginActivity.loggedAccount;

        int id = loggedAccount.id;

        mApiService.create(id, busNameS, capacity, selectedFacilities, selectedBusType, price, selectedDeptStationID, selectedArrStationID).enqueue(new Callback<BaseResponse<Bus>>() {
            @Override
            public void onResponse(Call<BaseResponse<Bus>> call, Response<BaseResponse<Bus>> response) {
// handle the potential 4xx & 5xx error
                if (!response.isSuccessful()) {
                    Toast.makeText(mContext, "Application error " +
                            response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                BaseResponse<Bus> res = response.body();
// if success finish this activity (back to login activity)
                if (res.success) {
                    finish();
                    Toast.makeText(AddBusActivity.this, "Add bus berhasil", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, res.message, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<Bus>> call, Throwable t) {
                Toast.makeText(mContext, "Problem with the server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}