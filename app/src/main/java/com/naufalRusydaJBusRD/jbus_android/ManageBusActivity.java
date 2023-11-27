package com.naufalRusydaJBusRD.jbus_android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.naufalRusydaJBusRD.jbus_android.model.Bus;
import com.naufalRusydaJBusRD.jbus_android.request.BaseApiService;
import com.naufalRusydaJBusRD.jbus_android.request.UtilsApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageBusActivity extends AppCompatActivity {
    private ListView busListView;
    private BusListAdapter busListAdapter;
    private BaseApiService mApiService;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bus);
        // Initialize the ActionBar
        ActionBar actionBar = getSupportActionBar();

        mApiService = UtilsApi.getApiService();

        // Set the title of the ActionBar
        if (actionBar != null) {
            actionBar.setTitle("Manage Bus");
        }

        // Initialize the ListView and adapter
        busListView = findViewById(R.id.bus_list_view);
        busListAdapter = new BusListAdapter(this, new ArrayList<>());
        busListView.setAdapter(busListAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manage_bus_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_button) {
            // User clicked the "User Button"
            moveActivity(this, AddBusActivity.class);
            return true;
        }
        // Add other conditions for additional menu items if needed
        return super.onOptionsItemSelected(item);
    }

    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApiService = UtilsApi.getApiService();


        // Fetch the list of buses for the logged-in account
        int accountId = LoginActivity.loggedAccount.id;
        mApiService.getMyBus(accountId).enqueue(new Callback<List<Bus>>() {
            @Override
            public void onResponse(Call<List<Bus>> call, Response<List<Bus>> response) {
                if (response.isSuccessful()) {
                    // Update the adapter with the fetched data
                    busListAdapter.clear();
                    busListAdapter.addAll(response.body());
                } else {
                    Toast.makeText(ManageBusActivity.this, "Failed to fetch buses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bus>> call, Throwable t) {
                Toast.makeText(ManageBusActivity.this, "Problem with the server", Toast.LENGTH_SHORT).show();
            }
        });
    }

}