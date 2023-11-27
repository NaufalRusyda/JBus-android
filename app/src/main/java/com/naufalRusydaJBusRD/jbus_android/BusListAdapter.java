package com.naufalRusydaJBusRD.jbus_android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.naufalRusydaJBusRD.jbus_android.model.Bus;

import java.util.List;
public class BusListAdapter extends ArrayAdapter<Bus> {
    private Context mContext;

    public BusListAdapter(Context context, List<Bus> buses) {
        super(context, 0, buses);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_bus_item, parent, false);
        }

        Bus bus = getItem(position);

        TextView busNameTextView = convertView.findViewById(R.id.bus_name);
        busNameTextView.setText(bus.name);

        ImageView calendarIconImageView = convertView.findViewById(R.id.calendar_icon);
        calendarIconImageView.setTag(position); // Set a tag to identify the position
        calendarIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                Bus bus = getItem(position);

                // Start the BusScheduleActivity with the bus ID
                Intent intent = new Intent(mContext, BusScheduleActivity.class);
                intent.putExtra("busId", bus.id);

                // Use the context (mContext) to start the activity
                mContext.startActivity(intent);
            }
        });


        return convertView;
    }


}

